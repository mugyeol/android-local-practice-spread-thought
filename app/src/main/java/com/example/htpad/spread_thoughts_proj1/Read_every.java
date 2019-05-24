package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Read_every extends AppCompatActivity {

    String imagePath;
    TextView tv_TITTLE,tv_WRITER,tv_CONTENTS, tv_countingReply;
    ImageView thumbs_up_normal, thumbs_up_color,imgView, REPLY, SHARE, previous;
    Button Handle_thumbsup;
    String post_position;
    String TAG = "클래스_리드에브리데이";
    private FrameLayout frameLayout;
    String tittle_READEVERY;
    String contents_READEVERY;
    String author;
    private TextView by;


    void setColor_againstIMG(){
        frameLayout.setBackgroundColor(getResources().getColor(R.color.transparentblack));
        thumbs_up_normal.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        thumbs_up_color.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        previous .setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        REPLY.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        SHARE.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_TITTLE.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_WRITER.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_countingReply.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
        by.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
//        Drawable alpha =imgView.getDrawable();
//        alpha.setAlpha(Factory.transparency);
//        imgView.setBackgroundColor(getResources().getColor(R.color.imageBacground));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_);


        frameLayout = findViewById(R.id.readEvery_framelayout);
        thumbs_up_normal = (ImageView) findViewById(R.id.thumbs_up);
        thumbs_up_color = (ImageView) findViewById(R.id.thumbs_up_colored_);
        tv_TITTLE = (TextView)findViewById(R.id.tittle_READEVERY);
        tv_WRITER = findViewById(R.id.Auther_READEVERY);
        REPLY = findViewById(R.id.reply);
        SHARE = findViewById(R.id.share);
        previous = findViewById(R.id.previouspage_READEVERY);
        tv_countingReply = findViewById(R.id.count_replies);
        tv_CONTENTS = findViewById(R.id.contents_READEVERY);
        imgView =  findViewById(R.id.image_READEVERY);
        thumbs_up_normal = findViewById(R.id.thumbs_up);
        thumbs_up_color =  findViewById(R.id.thumbs_up_colored_);
        Handle_thumbsup =  findViewById(R.id.thumbsUP_handler);
        by = findViewById(R.id.readevery_by);






        go_previous();
        getIncomingIntent();
        go_reply();
        thumbs_up_cliked();
        shareKakao();



                                             //check_like & isitAlready
        //좋아요 안눌린 상태에서 들어와서 좋아요 누르면 true  & false (데이터 저장)
        //좋아요 눌린상태에서 와서 취소하고 나가면 false & true (해당 데이터 삭제)
        //좋아요 눌렀던 게시물에 들어와서 그냥 나가면 true & true -> 낫띵해픈 (게시물 이미 저장되어 있음)
        //좋아요 안눌린 상태에 들어와서 안누르고 나가면 false & false - > 낫띵해픈



    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        Boolean check_like = prefs_liking.getBoolean("isLiked"+post_position,false);
        Boolean isitAlready = prefs_liking.getBoolean("isitAlready"+post_position,false);
        final SharedPreferences.Editor editor2 = prefs_liking.edit();
        Log.d(TAG, "check_like"+check_like);
        Log.d(TAG, "isitAlready"+isitAlready);

        if (check_like && !isitAlready ){
            editor2.putString("likedTittle"+post_position,tittle_READEVERY);
            editor2.putString("likedContents"+post_position,contents_READEVERY);
            editor2.putString("likedAuthor"+post_position,author);
            editor2.putString("likedPost"+post_position,post_position); // post position이 liked에서는 타임으로 쓰임.
            editor2.putString("likedImage"+post_position,imagePath);
            editor2.putBoolean("isLiked"+post_position,check_like);
            editor2.apply();
            postposition_savearray();
//            Toast.makeText(this, "좋아요 클릭! 데이터 추가!", Toast.LENGTH_SHORT).show();

        }else if (!check_like && isitAlready==true){
//            Toast.makeText(this, "좋아요 취소", Toast.LENGTH_SHORT).show();

            String json= prefs_liking.getString("position_time_list","");

            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject dataJsonObject = jsonArray.getJSONObject(i);

                    if(post_position.equals(dataJsonObject.getString("json_postposition"))){


                        jsonArray.remove(i);
                        Log.d(TAG, i+"번 JSON 오브젝트"+dataJsonObject.getString("json_postposition")+"제거"+jsonArray.toString());
                        Log.d(TAG, i+"post position"+post_position+"제거"+jsonArray.toString());
                    }
                }
                editor2.putString("position_time_list",jsonArray.toString());
                editor2.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (!check_like && !isitAlready){

//            Toast.makeText(this, "좋아요 안누른거 보러 와서 그냥 가기. ", Toast.LENGTH_SHORT).show();

        }else if (check_like && isitAlready){

//            Toast.makeText(this, "좋아요 눌렀던거 보러 와서 그냥 나가기 ", Toast.LENGTH_SHORT).show();

        }
    }


    Boolean isLiked;
    public void thumbs_up_cliked (){


        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        final SharedPreferences.Editor editor2 = prefs_liking.edit();


        Handle_thumbsup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //좋아요!
                if (thumbs_up_normal.getVisibility() == View.VISIBLE)
                {
                    thumbs_up_normal.setVisibility(View.INVISIBLE);
                    thumbs_up_color.setVisibility(View.VISIBLE);
                    isLiked = true;
                }
                //좋아요 취소!
                else if (thumbs_up_color.getVisibility() == View.VISIBLE)
                {
                    thumbs_up_color.setVisibility(View.INVISIBLE);
                    thumbs_up_normal.setVisibility(View.VISIBLE);
                    isLiked = false;
                }
                editor2.putBoolean("isLiked"+post_position,isLiked);
                editor2.apply();
            }
        });
    }


    JSONArray jsonArray;
    private void postposition_savearray(){
        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        final SharedPreferences.Editor editor2 = prefs_liking.edit();

        String json= prefs_liking.getString("position_time_list",""); //저장 되었던 jsonArray.toString 겟.
        Log.d(TAG, "JSON 겟"+json);

        //첫번째 좋아요.
        if(json.equals("")){
            jsonArray = new JSONArray();
            try {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("json_postposition", post_position);

                jsonArray.put(jsonObject);

                editor2.putString("position_time_list", jsonArray.toString());
                Log.d(TAG, "JSON 첫 저장"+jsonArray.toString());

                editor2.apply();
                //            wrapObject.put("list",jsonArray);
                //실제 데이터 전송 메소드
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(this, "json exception", Toast.LENGTH_SHORT).show();
            }

        }
        //두번째 좋아요부터.
        else {

            try {

                jsonArray = new JSONArray(json);
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("json_postposition", post_position);

                jsonArray.put(jsonObject);

                editor2.putString("position_time_list", jsonArray.toString());
                Log.d(TAG, "JSON 저장"+jsonArray.toString());

                editor2.apply();
                //            wrapObject.put("list",jsonArray);
                //실제 데이터 전송 메소드
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(this, "json exception", Toast.LENGTH_SHORT).show();
            }
        }
    }




int check_caller;

    void go_reply (){

        ImageView imgbtn = (ImageView) findViewById(R.id.reply);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Read_every.this, Replies.class);
                intent.putExtra("key_post_position",post_position);

                startActivity(intent);

            }
        });

    }



    public void setString_Image(String tittle_READEVERY, String contents_READEVERY,String imagePath, String author){


        if (imagePath!=null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(imagePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//            int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//            Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
//            imgView.setImageBitmap(Factory.rotate(scaled,exifDegree));



            Glide.with(this).load(imagePath)
                    .apply(new RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.loading_animation)
                    .fitCenter()
                    .centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            frameLayout.setBackgroundColor(getResources().getColor(R.color.transparentblack));

                            return false;
                        }
                    }).into(imgView);


            tv_WRITER.setText(author);
            tv_TITTLE.setText(tittle_READEVERY);
            tv_CONTENTS.setText(contents_READEVERY);
            setColor_againstIMG();
        }
    }

    void go_previous(){
        ImageView imgbtn = findViewById(R.id.previouspage_READEVERY);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check_caller==1) {
                    Intent intent = new Intent(Read_every.this, Liked.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (check_caller==1) {
            Intent intent = new Intent(Read_every.this, Liked.class);
            startActivity(intent);
        }
        finish();
    }
   private int counted_replies;
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA" + post_position, MODE_PRIVATE);
        counted_replies = pref_reply.getInt("final_reply_arraysize",0);
        tv_countingReply.setText(Integer.toString(counted_replies));

        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        final SharedPreferences prefs_liking = getSharedPreferences("Liking"+pref_user.getInt("key_user",10), MODE_PRIVATE);
        final SharedPreferences.Editor editor2 = prefs_liking.edit();


        Boolean check_like = prefs_liking.getBoolean("isLiked"+post_position,false);

        if (check_like){
            thumbs_up_normal.setVisibility(View.INVISIBLE);
            thumbs_up_color.setVisibility(View.VISIBLE);
            Boolean isitAlready = true;
            editor2.putBoolean("isitAlready"+post_position,isitAlready);
            editor2.apply();

        }else if (!check_like){
            thumbs_up_color.setVisibility(View.INVISIBLE);
            thumbs_up_normal.setVisibility(View.VISIBLE);
            Boolean isitAlready = false;
            editor2.putBoolean("isitAlready"+post_position,isitAlready);
            editor2.apply();
        }else{
//            Toast.makeText(this, "첫 방문.", Toast.LENGTH_SHORT).show();
        }
    }
    public void getIncomingIntent(){

        tittle_READEVERY = getIntent().getExtras().getString("key_tittle");
        contents_READEVERY = getIntent().getExtras().getString("key_contents");
        author = getIntent().getExtras().getString("key_author");
        post_position = getIntent().getExtras().getString("key_post_position");
        check_caller = getIntent().getExtras().getInt("from_liking");
        if (getIntent().hasExtra("key_imgStr")) {
            imagePath=getIntent().getExtras().getString("key_imgStr");
        }

        setString_Image(tittle_READEVERY,contents_READEVERY,imagePath, author);
    }
    public void shareKakao()

    {
        ImageView imgbtn = findViewById(R.id.share);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedTemplate params = FeedTemplate
                        .newBuilder(ContentObject.newBuilder(tittle_READEVERY,
                                imagePath,
                                LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com").build())
                                .setDescrption(contents_READEVERY)
                                .build())
                        .setSocial(SocialObject.newBuilder().setCommentCount(counted_replies)
                                .build())
                        .addButton(new ButtonObject("글, 생각", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))

                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${shared_product_id}");

                KakaoLinkService.getInstance().sendDefault(Read_every.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    }
                });


            }







        });



    }


}
