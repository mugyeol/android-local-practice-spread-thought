package com.example.htpad.spread_thoughts_proj1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;


public class ReadPart extends AppCompatActivity {
    Boolean delete_OR_not;
    int RQ_TO_EDIT = 10;
    int get_posi;
    String imagePath;
    String tittle_READ;
    String contents_READ;
    Boolean edit_or_not;
    View edit_View;
    View delete_View;
    TextView tv_TITTLE, tv_CONTENTS, tv_WRITER;
    ImageView PreviousPage;
    private FrameLayout frameLayout;


    void setColor_againstIMG(){
        frameLayout.setBackgroundColor(getResources().getColor(R.color.transparentblack));
        AppCompatImageView edit_icon = findViewById(R.id.go_to_edit);
        ImageView delete_icon = findViewById(R.id.trash);
        PreviousPage = findViewById(R.id.previouspage);
        tv_TITTLE = (TextView)findViewById(R.id.tittle_myREAD_);
        tv_WRITER = findViewById(R.id.Auther_inread);
        edit_icon.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        delete_icon.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        PreviousPage.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_TITTLE.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_WRITER.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
//        Drawable alpha =imgView.getDrawable();
//        alpha.setAlpha(Factory.transparency);
//        imgView.setBackgroundColor(getResources().getColor(R.color.imageBacground));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_readpart);
        frameLayout = findViewById(R.id.myread_framelayout);
        imgView = (ImageView)findViewById(R.id.image_myREAD_);


        go_to_previousPage();
//            thumbs_up_cliked();
        to_delete();
        to_edit();
        getIncomingIntent();
        edit_or_not=false;


    }

    ImageView imgView;
    public void to_edit(){
        edit_View = (View) findViewById(R.id.edit_myREAD);
        edit_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadPart.this,WritingPlace.class);
                intent.putExtra("key_tittle",tittle_READ);
                edit_or_not = true;
                intent.putExtra("key_boolean",edit_or_not);
                intent.putExtra("key_imgStr",imagePath);
                intent.putExtra("key_contents",contents_READ);
                intent.putExtra("key_code",3); /// 새로 쓰는게 아닌 수정임을 알림.

                startActivityForResult(intent,RQ_TO_EDIT);
            }
        });
    }
    //수정 후 받는 리절트

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tv_TITTLE=(TextView) findViewById(R.id.tittle_myREAD_);
        tv_CONTENTS= (TextView) findViewById(R.id.contents_myREAD_);
        if (requestCode==RQ_TO_EDIT && resultCode==RESULT_OK) {
            tittle_READ = data.getStringExtra("key_tittle");
            imagePath = data.getStringExtra("key_imgStr");  //data.get 은 null 오류 안남.
            contents_READ = data.getStringExtra("key_contents");
            edit_or_not = data.getBooleanExtra("key_boolean",false);

            tv_TITTLE.setText(tittle_READ);
            tv_CONTENTS.setText(contents_READ);
            if (imagePath != null) {
//                ExifInterface exif = null;
//                try {
//                    exif = new ExifInterface(imagePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//
//                int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
//                Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
//                imgView.setImageBitmap(Factory.rotate(scaled, exifDegree));
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
                        })
                        .into(imgView);


                setColor_againstIMG();
            } else if (imagePath == null) {
                imagePath=null;
//                Toast.makeText(this, "사진 x", Toast.LENGTH_SHORT).show(); /// null인데도 사진 셋 되서 위에 2라인 추가.
            }
        }
    }


    public void getIncomingIntent(){
        tittle_READ = getIntent().getExtras().getString("key_tittle");
        contents_READ = getIntent().getExtras().getString("key_contents");
        get_posi = getIntent().getIntExtra("key_position",0); //받아서 my timeline으로 넘기기.

        String author = getIntent().getExtras().getString("key_author");
        Log.d("show_me_string","tittle >>>>>>>>"+tittle_READ);
        if (getIntent().hasExtra("key_imgStr")) {
            imagePath=getIntent().getExtras().getString("key_imgStr");
        }
        setString_Image(tittle_READ,contents_READ,imagePath,author);
    }



    public void setString_Image(String tittle_READ, String contents_READ,String imagePath, String author){
        Log.d("show_me_passedString","tittle>>>>>"+tittle_READ);
        tv_TITTLE = (TextView)findViewById(R.id.tittle_myREAD_);
        tv_CONTENTS = (TextView)findViewById(R.id.contents_myREAD_);
        imgView = (ImageView) findViewById(R.id.image_myREAD_);
        tv_WRITER = findViewById(R.id.Auther_inread);

        tv_TITTLE.setText(tittle_READ);
        tv_WRITER.setText(author);
        tv_CONTENTS.setText(contents_READ);
        if (imagePath!=null) {
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(imagePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
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

            setColor_againstIMG();

        }

    }

    public void go_to_previousPage (){
       PreviousPage= (ImageView) findViewById(R.id.previouspage);
        PreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send text to timeline by using intent.
            if (edit_or_not==true) {
                Intent intent = new Intent();
                intent.putExtra("key_tittle", tittle_READ);
                intent.putExtra("key_imgStr", imagePath);
                intent.putExtra("key_contents", contents_READ);
                intent.putExtra("key_position", get_posi);
                Log.d("포지션확인", "backing position >> " + get_posi);

                int RESULT_OK = 2;
                setResult(RESULT_OK, intent);
                finish();
            }else if (edit_or_not==false){
                finish();
                }


            }
        });
    }



    public void to_delete(){
        delete_View = (View) findViewById(R.id.delete_myreadpart);
        delete_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showMessage();
            }
        });
    }
    public void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("삭제하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("key_boolean",delete_OR_not);
                intent.putExtra("key_position",get_posi);
                Log.d("포지션확인","try_deleting position >> "+get_posi);
                int RESULT_OK = 1;
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("생명주기_READ","onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("생명주기_READ","onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("생명주기_READ","onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("생명주기_READ","onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("생명주기_READ","onDestroy");
    }


}


