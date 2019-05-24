package com.example.htpad.spread_thoughts_proj1.message;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.htpad.spread_thoughts_proj1.Adapter;
import com.example.htpad.spread_thoughts_proj1.R;
import com.example.htpad.spread_thoughts_proj1.model.ChatModel;
import com.example.htpad.spread_thoughts_proj1.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatActivity extends Activity {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
    private String userName;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btn_close = (Button)findViewById(R.id.chatActivity_previous);
        closeActivity();
        RecyclerView recyclerView = findViewById(R.id.chatActivity_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());

    }


    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();


        public ChatRecyclerViewAdapter()
        {

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid)
                    .equalTo(true).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //
                    chatModels.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()){

                        chatModels.add(item.getValue(ChatModel.class));
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);



            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final CustomViewHolder customViewHolder = ((CustomViewHolder)holder);

            String destitnationUid = null;

            //챗방에 있는 유저 체크
            for (String user : chatModels.get(position).users.keySet()){
                if (!user.equals(uid)){
                    destitnationUid = user;
                    destinationUsers.add(destitnationUid);
                }
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(destitnationUid)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserModel userModel = dataSnapshot.getValue(UserModel.class);

                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userModel.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(customViewHolder.imageView);

                    customViewHolder.textview_tittle.setText(userModel.userName);
                        userName = userModel.userName;
//                    notifyDataSetChanged();



//
//                    Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
//                    commentMap.putAll(chatModels.get(position).comments);
//                    String lastMessageKey = (String) commentMap.keySet().toArray()[0];
//                    customViewHolder.textView_lastMessage.setText(chatModels.get(position).comments.get(lastMessageKey).message);
//
//
//                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//                    Long unixTime = (Long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
//                    Date date = new Date(unixTime);
//                    customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
//
//

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //메세지를 내림 차순으로 정렬 후, 마지막 메세지 키값 가져옴.
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            if (commentMap.size()!=0) {
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_lastMessage.setText(chatModels.get(position).comments.get(lastMessageKey).message);


                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                if (chatModels.size() != 0) {
                    Long unixTime = (Long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                    Date date = new Date(unixTime);
                    customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));

                }

            }


            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (ChatActivity.this, MessageActivity.class);
                    intent.putExtra("destination_uid",destinationUsers.get(position));
                    intent.putExtra("destination_name",userName);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(ChatActivity.this,R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());
                }
            });



        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }
    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textview_tittle;
        public TextView textView_lastMessage;
        public TextView textView_timestamp;

        public CustomViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.chatItem_imageview);
            textview_tittle = view.findViewById(R.id.chatItem_textView);
            textView_lastMessage = view.findViewById(R.id.chatItem_textView_lastmessage);
            textView_timestamp = view.findViewById(R.id.chatItem_textView_timestamp);


        }
    }
}
