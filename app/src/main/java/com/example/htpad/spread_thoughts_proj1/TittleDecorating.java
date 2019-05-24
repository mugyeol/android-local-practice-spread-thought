package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TittleDecorating extends AppCompatActivity {

    ImageView btn_gallery;
    ImageView btn_camera;
    int IMAGE_REQUEST =55;
    String imagePath_2;
    File photoFile;
    int REQUEST_IMAGE_CAPTURE=77;
    Uri uri;
    ImageView imgView, complete_DECO_Btn, cancel_DECO_Btn;
    String imagePath;
    String tittle_DECO_EDITTED;
    EditText et_tittle;
    FrameLayout frameLayout;



    void setColor_againstIMG(){
        frameLayout.setBackgroundColor(getResources().getColor(R.color.transparentblack));

        cancel_DECO_Btn = findViewById(R.id.cancel_DECO);
        complete_DECO_Btn=  findViewById(R.id.complete_DECO);
        TextView tv_header = findViewById(R.id.header_deco);
        et_tittle = findViewById(R.id.tittle_DECO);

        cancel_DECO_Btn.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        complete_DECO_Btn.setColorFilter(getResources().getColor(R.color.TEXT_COMEOUT));
        tv_header.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
        et_tittle.setTextColor(getResources().getColor(R.color.TEXT_COMEOUT));
//        Drawable alpha =imgView.getDrawable();
//        alpha.setAlpha(Factory.transparency);
//        imgView.setBackgroundColor(getResources().getColor(R.color.imageBacground));
    }



    //    int get_posi; // 수정에서 불렀을때 위해서
    int get_posi;
    public void getIncomingIntent(){
        if(getIntent().hasExtra("key_tittle")){
            tittle_DECO_EDITTED = getIntent().getExtras().getString("key_tittle");
            if (getIntent().hasExtra("key_imgStr")) {
                imagePath=getIntent().getExtras().getString("key_imgStr");
            }
            setString_Image(tittle_DECO_EDITTED,imagePath);
        }

    }

    public void setString_Image(String tittle_DECO, String imagePath){
        et_tittle = (EditText)findViewById(R.id.tittle_DECO);
        imgView = (ImageView)findViewById(R.id.image_DECO);
        et_tittle.setText(tittle_DECO);
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

            setColor_againstIMG();

        }

    }
    ///추가
    void complete (){
        complete_DECO_Btn=  findViewById(R.id.complete_DECO);
        complete_DECO_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_tittle = (EditText)findViewById(R.id.tittle_DECO);
                tittle_DECO_EDITTED = et_tittle.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("key_tittle",tittle_DECO_EDITTED);
//                intent.putExtra("position",get_posi);
                if (imagePath!=null) {
                    intent.putExtra("key_imgStr",imagePath);   //create할때랑 달리 여기는 예외처리 안해줘도 에러가 안나네..
                }else{
                    imagePath=null;
                }
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tittle_deco);
        frameLayout = findViewById(R.id.tittleDeco_framelayout);
        imgView = (ImageView) findViewById(R.id.image_DECO);
        lets_capture();
        lets_goto_gallery();
        cancel_DECO_clicked();
        getIncomingIntent();
        complete();


    }


    void cancel_DECO_clicked(){
        cancel_DECO_Btn =  findViewById(R.id.cancel_DECO);
        cancel_DECO_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }





    void lets_capture(){
        btn_camera = (ImageView) findViewById(R.id.capture_DECO);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTakePhotoIntent();
            }
        });
    }

    //imgage 파일 들어갈 폴더 생성 (카메라)
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);   //파일 저장할 directory
        File image = File.createTempFile(   //directory 안의 파일 (이미지 넣을곳)
                imageFileName,  /*prefix*/   // image이름
                "jpg",   /*suffix*/  // 파일 형식
                storageDir       /*directory*/
        );
        imagePath_2 = image.getAbsolutePath(); //image 파일이 path get.
        return image; // image -> get 한 path return
    }

    private void sendTakePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            photoFile=null;
            try{
                photoFile = createImageFile();
            }catch(IOException ex){
            }
            if (photoFile!=null){
                uri= FileProvider.getUriForFile(this,getPackageName(),photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //카메라 가지고 온 사진 set
    private void getPictureForPhoto() {


        Glide.with(this).load(imagePath_2)
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

        imagePath=imagePath_2;

    }

    //갤러리 사진 경로 생성.
    private String getRealPathFromURI(Uri uri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }
    //    갤러리에서 가지고온 이미지 로드
    private void sendPicture(Uri uri) {

        imagePath = getRealPathFromURI(uri); // path 경로


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

    //갤러리 실행
    public void lets_goto_gallery(){
        btn_gallery = (ImageView) findViewById(R.id.gallery_DECO);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_TOgallery = new Intent(Intent.ACTION_PICK);
                intent_TOgallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent_TOgallery.setType("image/*");
                startActivityForResult(intent_TOgallery, IMAGE_REQUEST);
                //title 어디에 쓰이는거임?
            }
        });
    }



    //이미지 PICKED, ON_RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
            sendPicture(data.getData());
        }
        else if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            getPictureForPhoto();
        }
        else if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_CANCELED){
            imagePath_2 = imagePath;
            imagePath=imagePath_2;
        }
    }


}//class

