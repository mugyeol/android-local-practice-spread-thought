package com.example.htpad.spread_thoughts_proj1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.htpad.spread_thoughts_proj1.model.ItemList;

import java.util.ArrayList;

public class Replies extends AppCompatActivity {

    EditText et_write_reply;
    String replies_to_send, profile_imagePath, replier;
    ArrayList<ItemList> itemLists_reply = new ArrayList<>();
    RecyclerView recyclerview;
    Adapter_reply adapter;
    static String post_position;
    int  usercode_for_permission;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replies);



        post_position = getIntent().getExtras().getString("key_post_position","");
        Log.d("체크_리플라이타임",""+post_position);
        SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA"+post_position,MODE_PRIVATE);
        int get_reply_arraysize = pref_reply.getInt("final_reply_arraysize",0);

        if (get_reply_arraysize == 0) {
//            Toast.makeText(this, "has been no reply on this post", Toast.LENGTH_SHORT).show();
        }else
        {
        for (int i = 1; i<=pref_reply.getInt("last_arraysize",0); i++) {
            replier = pref_reply.getString("reply_user_name" + i, "");
            profile_imagePath = pref_reply.getString("reply_img_str" + i, null);
            replies_to_send = pref_reply.getString("reply_contents" + i, "");
            usercode_for_permission = pref_reply.getInt("reply_usercode" + i, 0);
            addData();
        }
        }
        sendREPLY();
        close_reply_activity();
    recyclerview = findViewById(R.id.recycler_reply);

    setRecyclerView();

//        adapter.setItemClick(new Adapter_reply.ItemClick() {
//            @Override
//            public void onClick(View view, int position) {
//                if(itemLists_reply.size()!=0) {
//
//                    itemLists_reply.remove(position);
//                    adapter.notifyDataSetChanged();
//
//                    int final_arraysize_reply=itemLists_reply.size();
//                    System.out.println("어레이사이즈_마지막"+final_arraysize_reply);
//
//                    SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA"+post_position,MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref_reply.edit();
//                    editor.putInt("final_reply_arraysize",final_arraysize_reply);
//                    editor.apply();
//                }
//            }
//        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerview,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                    final int fromPos = viewHolder.getAdapterPosition();
//                    final int toPos = viewHolder.getAdapterPosition();
//                    // move item in `fromPos` to `toPos` in adapter.

                return true;// true if moved, false otherwise
            }
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                int position = holder.getAdapterPosition();
                int dragFlags = 0; // whatever your dragFlags need to be
                int swipeFlags = check_user(position) ?  0 : ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            public boolean check_user (int position){
                    ItemList itemControl_reply = itemLists_reply.get(position);
                    SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                      if (pref_user.getInt("key_user", 0) == itemControl_reply.getUser_code_for_permission()){
                         return false;
                          }else{
                              return true;
                            }
                            }
            @Override

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Remove swiped item from list and notify its RecyclerView
                int position = viewHolder.getAdapterPosition();
                ItemList itemControl_reply = itemLists_reply.get(position);
                SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
//                if (pref_user.getInt("key_user", 0) == itemControl_reply.getUser_code_for_permission()) {
                    itemLists_reply.remove(position);
                    adapter.notifyDataSetChanged();

                    int final_arraysize_reply = itemLists_reply.size();

                    SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA" + post_position, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref_reply.edit();
                    editor.putInt("final_reply_arraysize", final_arraysize_reply);
                    editor.apply();
//                }









            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);


    }

    void close_reply_activity(){
        Button imgbtn = (Button) findViewById(R.id.cancel_REPLY);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void sendREPLY(){
    ImageView imgBtn_sendReply = findViewById(R.id.send_btn_reply);
        imgBtn_sendReply.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // replier = 현재 유저 네임.
            // imgStr = 현재 유저 프로필 이미지.
            // contents = getString from editText.
            et_write_reply = findViewById(R.id.et_write_reply);

            SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            Log.d("체크","keyuser>>>>>>>>"+pref_user.getInt("key_user",0));

            usercode_for_permission = pref_user.getInt("key_user",0);
            replies_to_send = et_write_reply.getText().toString();
            profile_imagePath = pref_user.getString("profileIMG"+usercode_for_permission,null);
            replier = pref_user.getString("NAME"+usercode_for_permission,"");



            Log.d("체크","usercode>>>>>>>>"+usercode_for_permission);
            Log.d("체크","userName>>>>>>>>"+replier);


            int previous_array_size = itemLists_reply.size();//사이즈 여기서 재야 +1 되기 전 사이즈에 추가 가능.
            System.out.println("어레이사이즈_추가_직전"+previous_array_size);

            addData();
            adapter.notifyItemInserted(previous_array_size);
            et_write_reply.getText().clear();
            hideKeyboard(Replies.this);


        }
    });
    }
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //on create loading 용.
    void addData(){
        ItemList reply_pkg = new ItemList(replier, replies_to_send, profile_imagePath, usercode_for_permission);
        itemLists_reply.add(reply_pkg);
        int final_arraysize_reply=itemLists_reply.size();
        System.out.println("어레이사이즈_마지막"+final_arraysize_reply);

        SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA"+post_position,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_reply.edit();
        editor.putInt("final_reply_arraysize",final_arraysize_reply);
        editor.apply();


    }



        void setRecyclerView(){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerview.setLayoutManager(layoutManager);
            adapter = new Adapter_reply(this,itemLists_reply);
            recyclerview.setAdapter(adapter);
        }





}
