package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.htpad.spread_thoughts_proj1.model.ItemList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TimeLine extends AppCompatActivity {

    String contents;
    RecyclerView recyclerView;
    String tittle;
    String img_string;
    String time;
    ImageView ToWrite;
    Adapter adapter;
    int RQ_TO_WRITE=20;
    String author;
    int a;
    ArrayList<ItemList> itemLists = new ArrayList<>();
    TextView tv_popupMSG;
    Handler handle;
Thread thread;

    void thread_start(final TextView tv){
        handle = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("사이즈","ㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹ");
                    while (true) {



                        Log.d("사이즈","스레드루프"+check_empty_or_not);

                        Thread.sleep(1000);

                            for (int i = 0; i < 4; i++) {
                                Thread.sleep(350);

                                handle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
                                        SharedPreferences prefs = getSharedPreferences("My_pref"+pref_user.getInt("key_user",10), MODE_PRIVATE);
                                        check_empty_or_not = prefs.getInt("empty_or_not",0);
                                        Log.d("사이즈", "스레드런" + check_empty_or_not);

                                        if (tv.getVisibility() == View.VISIBLE) {
                                            tv.setVisibility(View.INVISIBLE);
                                        } else if (check_empty_or_not == 0) {
                                            tv.setVisibility(View.VISIBLE);
                                        } else {
                                            Log.d("사이즈", "리턴>>>>>>>>>>>>>>>");
                                            return;
                                        }
                                    }
                                });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(run);
        thread.setDaemon(true);
        thread.start();

    }



    int check_empty_or_not;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        Log.d("포지션확인","마이파트 onCreate");

        tv_popupMSG = findViewById(R.id.pop_up_message);


        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences("My_pref"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        check_empty_or_not = prefs.getInt("empty_or_not",0);

        //preference파일네임에 usercode 더해서 user구분

        to_write();
        go_previous();



        //데이터 불러오기
        if (check_empty_or_not==0) {
//            Toast.makeText(this, "저장된 글 없음", Toast.LENGTH_SHORT).show();
        }else{
            for (int i = 1; i <= check_empty_or_not; i++) {
                tittle = prefs.getString("arrayPREF_tittle" + i, "");
                img_string = prefs.getString("arrayPREF_imgstr" + i, null);
                contents = prefs.getString("arrayPREF_contents" + i, "");
                author = prefs.getString("arrayPREF_author" + i, "");
                time = prefs.getString("TIME"+i,"");



                addData();
                Log.d("포지션확인", "for문 i >>" + i);
        }
        }





            recyclerView = findViewById(R.id.recycler);
            setRecyclerView();


            Log.d("사이즈","스레드스타트"+check_empty_or_not);
            thread_start(tv_popupMSG);


    }

    void go_previous(){

        ImageView go_back = findViewById(R.id.go_previous_timeline);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (TimeLine.this,TimeLine_for_everybody.class);
                startActivity(intent);
                finish();
            }
        });
}

    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(this,itemLists);
        recyclerView.setAdapter(adapter);
    }

    void addData(){
        ItemList data_pkg = new ItemList(tittle,author,img_string,contents,time);
        itemLists.add(data_pkg);
    }
    void setData(int position){
        ItemList data_pkg = new ItemList(tittle,author,img_string,contents,time);
        itemLists.set(position,data_pkg);
    }
    void removeData(int position)


    {
        itemLists.remove(position);
    }
    void to_write(){
        ToWrite = (ImageView) findViewById(R.id.to_write);
        ToWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeLine.this,WritingPlace.class);
                startActivityForResult(intent,RQ_TO_WRITE);
            }
        });
    }
    static int RQ_TO_READ=333;
    int get_position;
    int empty_or_not;
    String Writer;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Log.d("포지션확인","onactivityresult");
        //새 글 쓰기 결과값.
        if (requestCode==RQ_TO_WRITE && resultCode==RESULT_OK){
            int array_size = itemLists.size();
            tittle=data.getStringExtra("key_tittle");
            img_string=data.getStringExtra("key_imgStr");
            contents=data.getStringExtra("key_contents");
            time=data.getStringExtra("key_time");
            SharedPreferences pref= getSharedPreferences("USER_INFO",MODE_PRIVATE);
            int user_code = pref.getInt("key_user",0);
            author = pref.getString("NAME"+user_code,"");

            if (img_string!=null){
            }
            else{
                img_string=null;
            }

            addData();
            adapter.notifyItemInserted(array_size);

        } else if (requestCode==RQ_TO_READ && data!=null){
            //수정
            if (resultCode==2) {
                get_position = data.getIntExtra("key_position",0);
                contents=data.getStringExtra("key_contents");
                tittle = data.getStringExtra("key_tittle");
                img_string = data.getStringExtra("key_imgStr");
                if (img_string!=null) {
                }else{
                    img_string=null;
                }
                time = itemLists.get(get_position).getTime(); //처음 추가 시에 딴 시간 적용.
                Log.d("체크","포지션onresult >>>>"+get_position+"    시간>>>>"+time);

                setData(get_position);
                adapter.notifyItemChanged(get_position);
            }
            //삭제
            else if (resultCode==1){
                get_position = data.getIntExtra("key_position",0);
                removeData(get_position);
                adapter.notifyDataSetChanged();
            }else{
            }
        }
        empty_or_not = itemLists.size();
        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences("My_pref"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs.edit();
        editor2.putInt("empty_or_not",empty_or_not);
        editor2.apply();


        Log.d("사이즈","온리절트"+prefs.getInt("empty_or_not",0));



        // adapter arraylist size는 마지막 아이템 삭제 할 시에 거치지 않음. 때문에 여기서 array 사이즈 put 그리고 oncreate에서 get.
        //oncreate에서 get했을때 0일 경우 for문 (데이터 불러오기위한) 사용할 필요 없도록 조건 설정.

    } //onactivityresult

    void sort_time (){
        //시간 순 정렬...
        Collections.sort(itemLists, new Comparator<ItemList>(){
            @Override
            public int compare(ItemList lhs, ItemList rhs) {
                return rhs.getTime().compareTo(lhs.getTime());
            }

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent (TimeLine.this,TimeLine_for_everybody.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("포지션확인","마이파트 onStart");
        sort_time();
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("포지션확인","onResume");


        }//ONRESUME

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("포지션확인","onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("포지션확인","onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("포지션확인","onDestroy");
    }
}//class

