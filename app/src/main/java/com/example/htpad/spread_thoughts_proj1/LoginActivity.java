package com.example.htpad.spread_thoughts_proj1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends AppCompatActivity {
    String TAG_USERKEY = "유저_정보_확인";
    SessionCallback callback;

    Button btn_custom_login;
    LoginButton btn_kakao_login;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static Activity _LoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        _LoginActivity = LoginActivity.this;
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.default_config);

        ItisKakao = false;
        SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_user.edit();
        editor.putBoolean("isKakao",ItisKakao);
        editor.apply();




        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }

        kakao_login();

        go_simpleLogin();
    }


    void go_simpleLogin(){
        Button btn = findViewById(R.id.go_to_simpleLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Simple_Login.class);
                startActivity(intent);
//                finish();
            }
        });
    }

    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }


    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if ( !readAccepted || !writeAccepted  )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  LoginActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }


    private Boolean ItisKakao;
    void kakao_login() {


        Session.getCurrentSession().clearCallbacks();
        btn_custom_login = (Button) findViewById(R.id.btn_kakao_login_customized);
        btn_custom_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ItisKakao = true;
                SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref_user.edit();
                editor.putBoolean("isKakao",ItisKakao);
                editor.apply();


                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                btn_kakao_login.performClick();



            }
        });

        btn_kakao_login = (LoginButton) findViewById(R.id.btn_kakao_login);


    }


    //카카오 로그인 콜백 클래스
    private class SessionCallback implements ISessionCallback {


        // 로그인에 성공한 상태

        @Override

        public void onSessionOpened() {

            requestMe();

        }


        // 로그인에 실패한 상태

        @Override

        public void onSessionOpenFailed(KakaoException exception) {

            Log.e("확인_SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());

        }


        // 사용자 정보 요청

        public void requestMe() {

            // 사용자정보 요청 결과에 대한 Callback

            UserManagement.getInstance().requestMe(new MeResponseCallback() {

                // 세션 오픈 실패. 세션이 삭제된 경우,

                @Override

                public void onSessionClosed(ErrorResult errorResult) {

                    Log.e("확인_SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());

                }


                // 회원이 아닌 경우,

                @Override

                public void onNotSignedUp() {

                    Log.e("확인_SessionCallback :: ", "onNotSignedUp");

                }


                // 사용자정보 요청에 성공한 경우,

                @Override

                public void onSuccess(UserProfile userProfile) {



                    Log.e("확인_SessionCallback :: ", "onSuccess");


                    String nickname = userProfile.getNickname();

                    String email = userProfile.getEmail();

                    String profileImagePath = userProfile.getProfileImagePath();

                    String thumnailPath = userProfile.getThumbnailImagePath();

                    String UUID = userProfile.getUUID();

                    long id = userProfile.getId();



                    Boolean isFirst = true; // 처음이냐 아니냐 밝혀라.



                    ////////
                    SharedPreferences pref_user = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref_user.edit();

                    for (int i=0; i<=pref_user.getInt("LAST_USER_CODE",0); i++){
                        if (nickname.equals(pref_user.getString("NAME"+i,"")))
                        ///카카오에서 이메일 주소 안넘겨 주는 관계로 일단 닉네임으로 이름 구별.
                        ///완전 고유하게 하려면 따로 이메일 입력 창 띄워서 그걸로 구별 해야됨(인증된 이메일)


                        {

                            isFirst = false;
                            editor.putInt("key_user", i);
                            editor.apply();
                            Log.d(TAG_USERKEY,"ISNOTFIRSTKAKAOLOGIN"+i);
                            Intent intent = new Intent(LoginActivity.this, TimeLine_for_everybody.class);
                            startActivity(intent);
                            finish();
                            break;

                        }


                    }

                    if(isFirst) {
                        editor.putString("NAME" + pref_user.getInt("counting", 0), nickname);
                        editor.putString("profileIMG" + pref_user.getInt("counting", 0), thumnailPath);
//                    editor.putString("email" + pref_user.getInt("counting", 0), email);
//                    editor.putInt("isURL_orStringIMG",1);

                        editor.putInt("LAST_USER_CODE", pref_user.getInt("counting", 0)); //0부터 시작.
                        editor.putInt("key_user", pref_user.getInt("counting", 0));

                        editor.apply();
                        Log.d(TAG_USERKEY,"IS_FIRSTKAKAOLOGIN"+pref_user.getInt("counting", 0));

                        Log.d("로그인_처음", "유저키값" + pref_user.getInt("key_user", 3333));

                        int b = pref_user.getInt("counting", 0) + 1;
                        editor.putInt("counting", b); ///user code +1 -> 다음번 usercode로 적용.
                        editor.apply();
                        Log.d("로그인_다음", "다음 유저키값" + pref_user.getInt("counting", 3333));

                        Intent intent = new Intent(LoginActivity.this, TimeLine_for_everybody.class);
                        startActivity(intent);
//                        finish();
                    }
                ///////////

                    Log.e("카카오확인_Profile : ", nickname + "");

                    Log.e("카카오확인_Profile : ", email + "");

                    Log.e("카카오확인_Profile : ", profileImagePath + "");

                    Log.e("카카오확인_Profile : ", thumnailPath + "");

                    Log.e("카카오확인_Profile : ", UUID + "");

                    Log.e("카카오확인_Profile : ", id + "");


                }


                // 사용자 정보 요청 실패

                @Override

                public void onFailure(ErrorResult errorResult) {

                    Log.e("확인_SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());

                }

            });


        }


    }
    

}
