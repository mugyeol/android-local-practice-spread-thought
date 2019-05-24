package com.example.htpad.spread_thoughts_proj1.message;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.htpad.spread_thoughts_proj1.R;
import com.example.htpad.spread_thoughts_proj1.model.ChatModel;
import com.example.htpad.spread_thoughts_proj1.model.NotificationModel;
import com.example.htpad.spread_thoughts_proj1.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends Activity {

    private Boolean ReceiverLocation;
    private String destinationUid;
    private ImageView button;
    private EditText editText;

    private TextView textView_nameTittle;
    private  String uid;
    private String chatRoomUid;
    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
    private  UserModel destinationUserModel;

    private Button btn_close;

    void closeActivity (){
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        ReceiverLocation = true;


        //message activity에 들어왔을 때, uid와 destinationUid 는 고유함.
        uid =FirebaseAuth.getInstance().getCurrentUser().getUid(); // 채팅을 요구하는 아이디 (단말기에 로그인 된 uid)
        destinationUid = getIntent().getStringExtra("destination_uid"); //채팅을 당하는 아이디.
        String destinationName = getIntent().getStringExtra("destination_name");

        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_user.edit();
        editor.putBoolean("checkLocation"+uid, ReceiverLocation);
        editor.apply();


        button = (ImageView) findViewById(R.id.messageActivity_button);
        editText = (EditText)findViewById(R.id.messageActivity_edittext);
        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);
        textView_nameTittle = (TextView) findViewById(R.id.messageActivity_destinationName);
        textView_nameTittle.setText(destinationName);

        btn_close = (Button)findViewById(R.id.messageActivity_previous);
        closeActivity();

        //메세지 전송 버튼.데이터 베이스, chatroom에 저장 하는곳
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
//                chatModel.uid = uid;
//                chatModel.destinationUid = destinationUid;


                chatModel.users.put(uid,true);
                chatModel.users.put(destinationUid,true);


                if (chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });
                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP; //server value -> 파이어베이스에서 제공
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
                            .child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            sendGcm();

                            editText.setText("");
                        }
                    });

                }

            }
        });
        checkChatRoom();

    }

    @Override
    protected void onPause() {
        super.onPause();

        ReceiverLocation = false;

        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_user.edit();
        editor.putBoolean("checkLocation"+uid, ReceiverLocation);
        editor.apply();

    }

    void sendGcm(){

        Gson gson = new Gson();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();
        notificationModel.data.destinationUid = destinationUid;


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyBIgmKdcpSHgMsr0-bULu9OIHFXEy76oMY")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });



        }

    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    ChatModel newRoom = new ChatModel();
                    newRoom.users.put(uid, true);
                    newRoom.users.put(destinationUid, true);


                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    return;
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        FirebaseDatabase.getInstance().getReference().child("chatrooms")
//                .orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot item : dataSnapshot.getChildren()){
//                    ChatModel chatModel = item.getValue(ChatModel.class);
//                    if (chatModel.users.containsKey(destinationUid)){
//                        chatRoomUid = item.getKey();
//                        button.setEnabled(true);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
//                        recyclerView.setAdapter(new RecyclerViewAdapter());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        void getMessageList(){
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            comments.clear();
                            for (DataSnapshot item : dataSnapshot.getChildren()){
                                comments.add(item.getValue(ChatModel.Comment.class));
                            }
                            //메세지 갱신
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size()-1);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


            return new MessageViewHolder (view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);



            //내가보낸 메세지.
            if (comments.get(position).uid.equals(uid)){
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.textView_message.setTextColor(getResources().getColor(R.color.black));
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                //상대방이 보낸 메세지
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.textView_message.setTextColor(getResources().getColor(R.color.black));

                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);

            }

            Long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);


//            private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");



        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;


            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView) view.findViewById(R.id.messageItem_textView_name);
                imageView_profile =(ImageView) view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_Linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_Linearlayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textView_timestamp);




            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);
    }
}
