package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.htpad.spread_thoughts_proj1.message.ChatActivity;
import com.example.htpad.spread_thoughts_proj1.message.PeopleActivity;
import com.example.htpad.spread_thoughts_proj1.model.ItemList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TimeLine_for_everybody extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerview;
    Adapter_Every adapter;
    String tittle;
    String author;
    String contents ;
    String time;
    String img_string;
    ArrayList<ItemList> itemLists_every = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_main);


///        현재 user 번호 순서대로 timeline에 정렬됨...... 포스팅 한 순서대로 하는거 실패했음.
        // 0번 회원만 가입하고 들어오면 0번에 저장된 아이템만 나옴. 3번까지 가입하면 3번까지 나옴.
        //회원 초기화 했을때 해당 회원번호에 저장된 데이터 날리는것도 구현해야함.
        // 각 회원이 추가한 아이템들 타임라인 갔을때 추가 안된다. (on Create에서 데이터 불러오고 view추가되기 때문에.)
        //on Resume이나 onactivity result에서 데이터 수정 관련 구현해야됨.


        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
            loop1 : for (int a = 0; a<=pref_user.getInt("LAST_USER_CODE",0); a++){
                SharedPreferences prefs = getSharedPreferences("My_pref"+a, MODE_PRIVATE);

               if (prefs.getInt("empty_or_not",0)==0){ ///각 유저 array의 최종 array size.
                   continue loop1;
               }else {
                   for (int i = 1; i <= prefs.getInt("empty_or_not", 0); i++) {
                       tittle = prefs.getString("arrayPREF_tittle" + i, "k");
                       img_string = prefs.getString("arrayPREF_imgstr" + i, null);
                       contents = prefs.getString("arrayPREF_contents" + i, "");
                       author = prefs.getString("arrayPREF_author" + i, "");
                       time = prefs.getString("TIME"+i,"");
                       addData();
                   }
               }

            }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        //시간 순 정렬...
        Collections.sort(itemLists_every, new Comparator<ItemList>(){
            @Override
            public int compare(ItemList lhs, ItemList rhs) {
                return rhs.getTime().compareTo(lhs.getTime());
            }

        });
        //데이터 초기화 위해서 저장해줌.
        SharedPreferences.Editor editor3 = pref_user.edit();
        editor3.putInt("arraysize_timelineEVERY",itemLists_every.size());
        editor3.apply();


            //타임라인 첫번재 아이템에 사진이 있을 경우. 텍스트, 아이콘 컬러 체인지.
        if (itemLists_every.size()!=0) {
            if (itemLists_every.get(0).getImgString() != null) {
                TextView header = findViewById(R.id.header_everyTIMELINE);
                header.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    toggle.getDrawerArrowDrawable().setColor(getColor(R.color.TEXT_COMEOUT));
                } else {
                    toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.TEXT_COMEOUT));
                }
            }
        }
        setImg();
        recyclerview=findViewById(R.id.recycler_everybodyTimeline);
        setRecyclerView();


if (!pref_user.getBoolean("isKakao",false)){
    passPushTokenToServer();


}


    }


    void passPushTokenToServer(){


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }



String profile_imagePath;

    //프로필 부분.
    void setImg (){
        SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);

       profile_imagePath = pref_user.getString("profileIMG"+pref_user.getInt("key_user",3333),null);
        String user_name = pref_user.getString("NAME"+pref_user.getInt("key_user",3333),"");

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView img =  header.findViewById(R.id.profile_drawer);
        TextView tv = header.findViewById(R.id.nickName);
        TextView tv_logout = header.findViewById(R.id.log_out_navheader);



        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //카카오 아이디면 카카오 로그아웃. 일반 로그인이면 그냥 finish 해도 어짜피 로그아웃.
                if(profile_imagePath!=null && profile_imagePath.substring(0,4).equals("http")) {

                    support_logout();
                    LoginActivity login = (LoginActivity) LoginActivity._LoginActivity;
                    login.finish();
                    finish();
                }else{


                    LoginActivity login = (LoginActivity) LoginActivity._LoginActivity;
                    login.finish();
                    finish();

                }
                }
        });




        tv.setText(user_name);

            //카카오 이미지는 피카소로 이미지 세팅.
        if(profile_imagePath!=null && profile_imagePath.substring(0,4).equals("http")) {
            Picasso.with(this).load(profile_imagePath).fit().into(img);

        }


             else{

            if (profile_imagePath != null) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(profile_imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
                Bitmap bitmap = BitmapFactory.decodeFile(profile_imagePath);
                int limit_height = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, limit_height, true);
                img.setImageBitmap(Factory.rotate(scaled, exifDegree));
            }
            //null일 경우 기본이미지.


                }
            }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.My_page) {
            // Handle the camera action
            Intent intent = new Intent(TimeLine_for_everybody.this,TimeLine.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.Favorite){
            Intent intent = new Intent(TimeLine_for_everybody.this, Liked.class);
            startActivity(intent);

        }else if (id==R.id.people){
            Intent intent = new Intent(TimeLine_for_everybody.this, PeopleActivity.class);
            startActivity(intent);

        }else if (id==R.id.direct_talk){
            Intent intent = new Intent(TimeLine_for_everybody.this, ChatActivity.class);
            startActivity(intent);

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();

            LoginActivity login = (LoginActivity) LoginActivity._LoginActivity;
            if (login!=null) {
                login.finish();
            }

        }
    }


    void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        adapter = new Adapter_Every(this,itemLists_every);
        recyclerview.setAdapter(adapter);
    }

    void addData(){
        ItemList data_pkg = new ItemList(tittle,author,img_string,contents,time);
        itemLists_every.add(data_pkg);
    }



    void support_logout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {

            @Override

            public void onCompleteLogout() {

                Toast.makeText(TimeLine_for_everybody.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();

            }

        });

    }



}
