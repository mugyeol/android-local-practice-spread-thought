package com.example.htpad.spread_thoughts_proj1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.htpad.spread_thoughts_proj1.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Registration extends AppCompatActivity {
    String TAG_USERKEY = "유저_정보_확인";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        i= pref_user.getInt("counting",0); /// user key counting 번호를 사진 이미지와 동기화. 유저 이미지 num = user key

        et_PW = findViewById(R.id.pw_REGISTER);
        et_PW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        imgview_gallery = (ImageView)findViewById(R.id.gallery_regi);
        imgbtn= (ImageView) findViewById(R.id.random_img_regi);
        btn_complete_register = findViewById(R.id.complete_register);
        et_ID = findViewById(R.id.id_REGISTER);
        et_NAME = findViewById(R.id.name_register);

        lets_capture();
        lets_goto_gallery();
        downloading();
        complete_register();
        clear_userinfo();

        Log.d("확인인트",""+i);
    }
    private ImageView imgbtn;
    private ImageView imgview_gallery;
    private EditText et_ID;
    private EditText et_PW;
    private EditText et_NAME;
    private int i;
    private ImageView btn_camera, imgView,btn_gallery;
    private File photoFile;
    private Uri uri;
    private int REQUEST_PROFILE_IMAGE= 91;
    private int IMAGE_REQUEST_PROFILE = 45;
    private String imagePath_2, imagePath;
    private Handler handle;
    private Thread thread;
    private Uri downUri;
    private String downUrl;
    private Button btn_complete_register;
    private File outputFile; //파일명까지 포함한 경로
    private File path;//디렉토리경로
    private Document doc;
    private ProgressDialog asyncDialog;
    private ProgressDialog progressBar;



        //회원 가입 완료
    void complete_register(){

        asyncDialog = new ProgressDialog(Registration.this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("Loading...");
        asyncDialog.setIndeterminate(true);
        asyncDialog.setCancelable(false);

        btn_complete_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (et_ID.getText().toString()==null || et_NAME.getText().toString()==null || et_PW.getText().toString()==null){
                    Toast.makeText(Registration.this, "회원 정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();

                    return;
                }else {
                    if (et_ID.getText().toString().contains("@") && et_PW.getText().length()>=6){

                        if (et_ID.getText().toString().contains("com") || et_ID.getText().toString().contains("co.kr") ){



                            CheckTypesTask loadingTask = new CheckTypesTask(Registration.this);
                            //로그인 성공. (loading data task)


                            loadingTask.execute();


                        }else{
                            Toast.makeText(Registration.this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();

                        }
                    }else{

                        Toast.makeText(Registration.this, "올바른 이메일 또는 비밀번호 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }

    Boolean finishLoading;

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public CheckTypesTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            // show dialog
            asyncDialog.show();
            Log.d("에이싱크체크","onpreExecute");


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            try {
//                for (int i = 0; i < 5; i++) {
//                    //asyncDialog.setProgress(i * 30);
//                    Thread.sleep(500);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            try {

                Thread.sleep(1000);


                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(et_ID.getText().toString(), et_PW.getText().toString())
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid(); // 중복 될리가 없음.
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(et_NAME.getText().toString()).build();
                                task.getResult().getUser().updateProfile(userProfileChangeRequest);



                                FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri)
                                        .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()){
                                                    throw task.getException();
                                                }
                                                return FirebaseStorage.getInstance().getReference().child("userImages").child(uid).getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()){
                                            downUri = task.getResult();
                                            downUrl = downUri.toString();
                                            UserModel userModel = new UserModel();
                                            userModel.userName = et_NAME.getText().toString();
                                            userModel.profileImageUrl = downUrl;
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Log.d("체크_image",""+downUrl);

                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref_user.edit();
                                                    editor.putString("ID"+pref_user.getInt("counting",0), et_ID.getText().toString());
                                                    editor.putString("PW"+pref_user.getInt("counting",0), et_PW.getText().toString());
                                                    editor.putString("NAME"+pref_user.getInt("counting",0), et_NAME.getText().toString());
                                                    editor.putString("profileIMG"+pref_user.getInt("counting",0), imagePath);
                                                    editor.putInt("LAST_USER_CODE", pref_user.getInt("counting", 0)); //0부터 시작.


                                                    Log.d("ch_eck","first"+pref_user.getInt("counting",0));
                                                    editor.apply();
                                                    Log.d(TAG_USERKEY,"ISNORMALLOGIN"+i);

                                                    int b = pref_user.getInt("counting", 0)+1;
                                                    editor.putInt("counting",b); ///user code +1 -> 다음번 usercode로 적용.
                                                    editor.apply();
                                                    Log.d("ch_eck","first"+pref_user.getInt("counting",0));

                                                    finishLoading = true;
                                                    Intent intent = new Intent();
                                                    setResult(RESULT_OK, intent);
                                                    asyncDialog.dismiss();
                                                    Toast.makeText(Registration.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                                    finish();

                                                }
                                            });

                                        }

                                    }
                                });

                            }
                        });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("에이싱크체크","doinBackgroundStart"+finishLoading);


            return null;


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("에이싱크체크", "doonPostExecute");





        }
    }


























    public void downloading(){

        progressBar=new ProgressDialog(Registration.this);
        progressBar.setMessage("랜덤 프로필 이미지 다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(true);


        imgbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //1
                //웹브라우저에 아래 링크를 입력하면 Alight.avi 파일이 다운로드됨.

                thread_start();

            }
        });
    }

    void thread_start(){
        handle = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i=0; i<1; i++) {
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                Animation anim = AnimationUtils.loadAnimation(
                                        Registration.this,R.anim.twits);
                                imgbtn.startAnimation(anim);
                            }
                        });
                    }
                    Thread.sleep(550);
                    handle.post(new Runnable() {
                        @Override
                        public void run() {

                            path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            outputFile= new File(path, i+".jpg"); //파일명까지 포함함 경로의 File 객체 생성. i 값은 회원 코드와 동기화.
                            String fileURL = "a";//형식적인것

                            if (outputFile.exists()) { //이미 다운로드 되어 있는 경우

                                outputFile.delete(); //파일 삭제

                                final DownloadFilesTask downloadTask = new DownloadFilesTask(Registration.this);

                                downloadTask.execute(fileURL);


                                progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(Registration.this, "랜덤 이미지 다운로드가 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                                        downloadTask.cancel(true);
                                    }
                                });

                            } else { //새로 다운로드 받는 경우
                                final DownloadFilesTask downloadTask = new DownloadFilesTask(Registration.this);
                                downloadTask.execute(fileURL);
                                progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(Registration.this, "랜덤 이미지 다운로드가 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                                        downloadTask.cancel(true);

                                    }
                                });
                            }

                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(run);
        thread.setDaemon(true);
        thread.start();
    }



//랜덤이미지 다운로드 에이싱크 태스크
    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }


        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();
        }
//    int index;

        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) { //3
            String a = string_url[0];
            String unsplashURL = "https://unsplash.com/t/nature";
            ArrayList<String> URLset = new ArrayList<>();
            Random random_instance = new Random();
            int randomInt = random_instance.nextInt(39);
            System.out.println(randomInt);
            Log.d("체크랜덤", ""+randomInt);

            try {
                //unsplash에 connect.
                doc = Jsoup.connect(unsplashURL).get();
            } catch (IOException E)
            {
            }
            // Get all img tags
            Elements img = doc.getElementsByTag("img");

            int counter = 0;


            //img src 추출. 및 arraylist에 쌓기.
            for (Element el : img) {
                // If alt is empty or null, add one to counter
                if (el.attr("alt") == null || el.attr("alt").equals("")) {
                    counter++;
                    URLset.add(el.attr("src"));
                    System.out.println("image tag: " + el.attr("src") + " Alt: " + el.attr("alt"));

                }else {

                }
            }
            System.out.println("Number of set alt: " + counter);

            //random 이용해서 이미지 하나 겟.
            final String fileURL = URLset.get(randomInt); ///URL imagPath

            Log.d("체크파일유알엘",""+downUrl);




            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;


            //sd카드에 저장하기 위해 랜덤하게 get한 url 커넥션 오픈, 커넥트.
            try {
                URL url = new URL(fileURL);
                connection = url.openConnection();
                connection.connect();


                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                String z = (Long.toString(FileSize));
                Log.d("파일_사이즈",""+z);

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);
                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile= new File(path, i+".jpg"); //파일명까지 포함함 경로의 File 객체 생성
                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);
                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }
                    downloadedSize += count;
                    Log.d("다운로드체크","count"+count);
                    Log.d("다운로드체크","downloadsize"+downloadedSize);

                    if (FileSize > 0) {
                        float per = ((float)downloadedSize/FileSize) * 100;
                        Log.d("다운로드체크","per"+per);
                        Log.d("다운로드체크","Filesize"+FileSize);

                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                        Log.d("다운로드체크","str"+str);
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);
                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }

                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;
        }


        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();

            if ( size > 0) {
//                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" + size.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "랜덤 프로필 이미지가 설정 되었습니다.", Toast.LENGTH_LONG).show();

                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));


                imageUri = Uri.fromFile(outputFile); // -> for firebase


                sendBroadcast(mediaScanIntent);
                imagePath = outputFile.getPath();


                Set_Random_URL_IMG(imagePath);

            }
            else
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
        }

    }




    private void Set_Random_URL_IMG(String imagePath) {

        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imagePath);
            Log.d("show_me_path_exif","see>>"+exif);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("show_me_exif","see>>"+exif);

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);

        ImageView imgview= findViewById(R.id.profile_regi);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
        imgview.setImageBitmap(Factory.rotate(scaled,exifDegree));//이미지 뷰에 비트맵 넣기

//        Glide.with(this).load(imagePath).into(imgView);






    }



    ///모든 회원 정보 삭제.
    void clear_userinfo(){
        Button btn = findViewById(R.id.clear_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                for (int i = 0; i <= pref.getInt("LAST_USER_CODE", 0); i++) {
                    SharedPreferences prefs = getSharedPreferences("My_pref" + i, MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = prefs.edit();
                    editor2.clear();
                    editor2.apply();
                }
                for (int i = 0; i <= pref.getInt("LAST_USER_CODE", 0); i++) {
                    SharedPreferences prefs = getSharedPreferences("Liking" + i, MODE_PRIVATE);
                    SharedPreferences.Editor editor4 = prefs.edit();
                    editor4.clear();
                    editor4.apply();
                }

                for (int i= 0; i<=pref.getInt("arraysize_timelineEVERY",0); i++) {

                    SharedPreferences pref_reply = getSharedPreferences("REPLIES_DATA" + i, MODE_PRIVATE);
                    SharedPreferences.Editor editor3 = pref_reply.edit();
                    editor3.clear();
                    editor3.apply();
                }

                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();
            }

        });
    }



    private String getRealPathFromURI(Uri uri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }




    void lets_capture(){
        btn_camera = (ImageView) findViewById(R.id.capture_regi);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTakePhotoIntent();
            }
        });
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
                startActivityForResult(takePictureIntent,REQUEST_PROFILE_IMAGE);
            }
        }
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


    //카메라 가지고 온 사진 set
    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath_2);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath_2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = Factory.exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        imgView = (ImageView) findViewById(R.id.profile_regi);
        int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);

        imgView.setImageBitmap(Factory.rotate(scaled, exifDegree));//이미지 뷰에 비트맵 넣기
        imagePath=imagePath_2;

    }
    //    갤러리에서 가지고온 이미지 셋.
    private void sendPicture(Uri uri) {

        imagePath = getRealPathFromURI(uri); // path 경로
        Log.d("show_me_path","see>>"+imagePath);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imagePath);
            Log.d("show_me_path_exif","see>>"+exif);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("show_me_exif","see>>"+exif);

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = Factory.exifOrientationToDegrees(exifOrientation);

        imgView = (ImageView) findViewById(R.id.profile_regi);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        int limit_height = (int)(bitmap.getHeight()*(1024.0/bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,1024,limit_height,true);
        imgView.setImageBitmap(Factory.rotate(scaled,exifDegree));//이미지 뷰에 비트맵 넣기

    }





    public void lets_goto_gallery(){
        btn_gallery = (ImageView) findViewById(R.id.gallery_regi);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                Intent intent_TOgallery = new Intent(Intent.ACTION_PICK);
                intent_TOgallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent_TOgallery.setType("image/*");
                startActivityForResult(intent_TOgallery, IMAGE_REQUEST_PROFILE);
                //title 어디에 쓰이는거임?




            }
        });
    }

    Uri imageUri;
    //이미지 PICKED, ON_RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_PROFILE && resultCode==RESULT_OK){
            imageUri = data.getData(); // -> for firebase.
            sendPicture(data.getData());// -> for local.
        }
        else if (requestCode==REQUEST_PROFILE_IMAGE && resultCode==RESULT_OK){
            getPictureForPhoto();


        }
        else if (requestCode==REQUEST_PROFILE_IMAGE && resultCode==RESULT_CANCELED){
            imagePath_2 = imagePath;
            imagePath=imagePath_2;


        }
    }

}

