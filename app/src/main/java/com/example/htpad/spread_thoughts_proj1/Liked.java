package com.example.htpad.spread_thoughts_proj1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.htpad.spread_thoughts_proj1.model.ItemList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Liked extends AppCompatActivity {
RecyclerView recyclerview;
Adapter_Liked adapter;
ArrayList<ItemList> itemLists_liked = new ArrayList<>();
    String tittle;
    String author;
    String contents ;
    String time;
    String img_string;
    Boolean check_like;
    String TAG = "클래스_라잌드";

    // 서버에서 배열 데이터를 전송 받을 때
    ArrayList<String> whichpost;
    private void receiveArray(){

        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);

        whichpost = new ArrayList<>();
        try {
            String json= prefs_liking.getString("position_time_list","");
            Log.d(TAG, "JSON 겟"+json);

            JSONArray jsonArray = new JSONArray(json);

            for(int i = 0; i <jsonArray.length(); i++){
                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                whichpost.add(dataJsonObject.getString("json_postposition"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Toast.makeText(this, "json exception Liked", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_recyclerview);
        receiveArray();

        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        SharedPreferences.Editor editor3 = pref_user.edit();
        if (whichpost.size()==0){

        }else {
            for (int i = 0; i < whichpost.size(); i++) {

                tittle = prefs_liking.getString("likedTittle"+whichpost.get(i), "");
                author = prefs_liking.getString("likedAuthor"+whichpost.get(i), "");
                contents = prefs_liking.getString("likedContents"+whichpost.get(i), "");
                time = prefs_liking.getString("likedPost"+whichpost.get(i), "");
                img_string = prefs_liking.getString("likedImage"+whichpost.get(i), null);
                check_like = prefs_liking.getBoolean("isLiked"+whichpost.get(i),false);

//                if (check_like){

                    addData();
//                }
//                else if (!check_like)

//                {



//                }
            }
            Log.d("이미지확인",""+img_string);

        }
        recyclerview= (RecyclerView) findViewById(R.id.recycler_liked);
        setRecyclerView();

        go_previous();

    }


    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        adapter = new Adapter_Liked(this,itemLists_liked);
        recyclerview.setAdapter(adapter);
    }

    void addData(){
        ItemList data_pkg = new ItemList(tittle,author,img_string,contents,time);
        itemLists_liked.add(data_pkg);


        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs_liking.edit();
        editor.putInt("last_likingarray",itemLists_liked.size());
        editor.apply();



    }
    void go_previous (){
        ImageView img = (ImageView) findViewById(R.id.close_liked);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
