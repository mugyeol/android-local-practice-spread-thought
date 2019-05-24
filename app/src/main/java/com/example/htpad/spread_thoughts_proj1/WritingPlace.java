package com.example.htpad.spread_thoughts_proj1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingPlace extends Activity {

    public WritingPlace(){

    }

    String str;
    int request_from_decotittle = 30;
    String image_path;
    ImageView img_thumbnail;
    Uri fileUri;
    String tittle_EDIT;
    String contents_EDIT;
    int get_posi_EDIT;
    String imagePath;
    ///글쓰기 종료

    TextView tv_TITTLE;
    EditText tv_CONTENTS;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writingplace);
        complete_WPLACE();
        go_to_DECO();
        finish_Writting_place();
        textchangeeffect();
        et_contents = findViewById(R.id.contents_WPLACE);

        et_contents.setScroller(new Scroller(WritingPlace.this));
        et_contents.setVerticalScrollBarEnabled(true);
//        et_contents.setMovementMethod(new ScrollingMovementMethod());
        if(getIntent().hasExtra("key_code")){ // 수정인지 추가인지 check. 수정일 경우에만 get incoming intent.
        getIncomingIntent();
    }else{

    }


    }


    public void getIncomingIntent(){
        tittle_EDIT = getIntent().getExtras().getString("key_tittle");
        contents_EDIT = getIntent().getExtras().getString("key_contents");
//        get_posi_EDIT = getIntent().getIntExtra("key_position",0);
        if (getIntent().hasExtra("key_imgStr")) {
            imgStr_WPLACE=getIntent().getExtras().getString("key_imgStr");
        }
        setString_Image(tittle_EDIT,contents_EDIT,imgStr_WPLACE);
    }


    public void setString_Image(String tittle_EDIT, String contents_EDIT,String imgStr_WPLACE){
        Log.d("show_me_passedString","tittle>>>>>"+tittle_EDIT);
        tv_TITTLE = (TextView)findViewById(R.id.tittle_WPLACE);
        tv_CONTENTS = (EditText)findViewById(R.id.contents_WPLACE);
        imgView = (ImageView) findViewById(R.id.image_WPLACE);
        tv_TITTLE.setText(tittle_EDIT);

        tv_CONTENTS.setText(contents_EDIT);


        if (imgStr_WPLACE!=null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(imgStr_WPLACE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = exifOrientationToDegrees(exifOrientation);
//            Bitmap bitmap = BitmapFactory.decodeFile(imgStr_WPLACE);
//            int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//            Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
//            imgView.setImageBitmap(rotate(scaled,exifDegree));
            Glide.with(this).load(imgStr_WPLACE).into(imgView);
        }

    }


    //제목 꾸미기 시작.
    public void go_to_DECO() {
        ImageView to_deco = (ImageView) findViewById(R.id.image_WPLACE);
        to_deco.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                et_tittle=(EditText)findViewById(R.id.tittle_WPLACE);
                et_contents= (EditText)findViewById(R.id.contents_WPLACE);
                tittle_WPLACE = et_tittle.getText().toString();

                Intent intent = new Intent(WritingPlace.this, TittleDecorating.class);
                intent.putExtra("key_tittle",tittle_WPLACE);
                intent.putExtra("key_imgStr",imgStr_WPLACE);
                startActivityForResult(intent, request_from_decotittle);
            }
        });
    }
    String contents_WPLACE;
    EditText et_contents;
    EditText et_tittle;
    String tittle_WPLACE;
    String imgStr_WPLACE;

    //새로 쓰는거면 메인에 추가, 새로 쓰는거 아니면 읽기 메인에서 수정.
    public void complete_WPLACE () {
        Button btn =findViewById(R.id.complete_WPLACE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                et_tittle=(EditText)findViewById(R.id.tittle_WPLACE);
                et_contents = (EditText)findViewById(R.id.contents_WPLACE);
                tittle_WPLACE = et_tittle.getText().toString();
                contents_WPLACE = et_contents.getText().toString();
                Boolean edit_or_not = true;   //수정으로 들어온 경우 수정 사항이 있는지 없는지 확인해 주는것.
                                             //<추가 or 수정> 확인과는 관련 없음. 추가인지 수정인지는
                                            //애초에 requestcode (Mypage에서 불렀는지, 읽기에서 불렀는지)로 분류됨.
                Log.d("포지션확인!","타이틀>>"+tittle_WPLACE);
                if (tittle_WPLACE.equals("") && contents_WPLACE.equals("")){
                    Toast.makeText(WritingPlace.this, "제목과 내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else if (contents_WPLACE.equals("")){
                    Toast.makeText(WritingPlace.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else if (tittle_WPLACE.equals("")){
                    Toast.makeText(WritingPlace.this, "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                Intent intent = new Intent();
                intent.putExtra("key_tittle",tittle_WPLACE);
                if (imgStr_WPLACE!=null) {
                    intent.putExtra("key_imgStr", imgStr_WPLACE);
                }
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat abc = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                    String time = abc.format(date);
                    String a = time.substring(0,4);
                    Log.d("체크_타임",""+a);
                    intent.putExtra("key_time",time);
                    intent.putExtra("key_contents",contents_WPLACE);
                intent.putExtra("key_boolean",edit_or_not);
                setResult(RESULT_OK,intent);
                finish();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        et_tittle=(EditText)findViewById(R.id.tittle_WPLACE);
//        et_contents= (EditText)findViewById(R.id.contents_WPLACE);
    if (requestCode==request_from_decotittle && resultCode==RESULT_OK) {
        tittle_WPLACE = data.getStringExtra("key_tittle");
        imgStr_WPLACE = data.getStringExtra("key_imgStr");  //data.get 은 null 오류 안남.
        et_tittle.setText(tittle_WPLACE);
        if (imgStr_WPLACE != null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(imgStr_WPLACE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = exifOrientationToDegrees(exifOrientation);
//            Bitmap bitmap = BitmapFactory.decodeFile(imgStr_WPLACE);
            img_thumbnail = (ImageView)findViewById(R.id.image_WPLACE);

//            int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//            Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
            Glide.with(this).load(imgStr_WPLACE).into(img_thumbnail);


        } else if (imgStr_WPLACE == null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imgStr_WPLACE);
//            img_thumbnail = (ImageView)findViewById(R.id.image_WPLACE);
//            img_thumbnail.setImageBitmap(bitmap);
            imgStr_WPLACE=null;
            Toast.makeText(this, "사진 x", Toast.LENGTH_SHORT).show(); /// null인데도 사진 셋 되서 위에 2라인 추가.
        }
    }
        }
    public void finish_Writting_place() {
        Button btn = (Button) findViewById(R.id.cancel_WPLACE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation== ExifInterface.ORIENTATION_ROTATE_90){

            return 90;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }
    private Bitmap rotate (Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

TextView tv;
    void textchangeeffect (){
        et_contents = (EditText)findViewById(R.id.contents_WPLACE);
        tv = (TextView) findViewById(R.id.counting_words);

        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 변경 후 발생할 이벤트를 작성.
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                int a= et_contents.getText().length();
                int allowable = 2000;
                String comment = "최대 500자 까지 작성 가능합니다";



                //텍스트가 변경될때마다 발생할 이벤트를 작성.
                if (et_contents.isFocusable())
                {
                    tv.setText(Integer.toString(a)+" / "+Integer.toString(allowable));

                    if (a>=allowable){
                        Toast.makeText(WritingPlace.this, comment, Toast.LENGTH_SHORT).show();

                    }

                }
            }
        };
        et_contents.addTextChangedListener(watcher);


    }



}//class












//class
//        try {
//                if (requestCode == request_from_decotittle && resultCode == RESULT_OK && null != data) {
//
//                String returnString = data.getStringExtra("value4");
//                EditText tittle_writtingPlace = (EditText) findViewById(R.id.tittle_WPLACE);
//                tittle_writtingPlace.setText(returnString);
//
//                image_path = data.getStringExtra("imagePath");
//                Log.d("내가확인하고픈_imgPath_wrtting",image_path);
//
//                fileUri = Uri.parse(image_path);
//                Log.d("내가확인하고픈_fileUri_wrtting",image_path);
//                img_thumbnail = (ImageView) findViewById(R.id.to_Deco_WPLACE);
//                img_thumbnail.setImageURI(fileUri);
//                } else {
//                //tittle deco에서 x 눌렀을때.
//                }
//                } catch (Exception e) {
//                e.printStackTrace();
//                }





// (shared preference)
//send text to readPart by using shared preference.
//go_to_tittle_decorate_page
//                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//                editor.putString("value2",editted_tittle);
//                editor.apply();