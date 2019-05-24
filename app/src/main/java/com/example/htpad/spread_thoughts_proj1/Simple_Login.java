package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Simple_Login extends AppCompatActivity {
    Button btn_register;
    int RQ_CODE_REGI = 111;
    String ID;
    String PW;
    EditText ET_ID;
    EditText ET_PW;
    boolean check;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_id_password);
        ET_ID = findViewById(R.id.ID_LOGIN);
        ET_PW = findViewById(R.id.PW_LOGIN);
        ET_PW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.signOut();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

//        logout();

        GO_register();
        Login();

    }

    public boolean Validate_Login(String input_ID, String input_PW) {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (int i = 0; i <= pref.getInt("LAST_USER_CODE", 0); i++) { // max_FOR 는 마지막 가입한 회원코드.

            if (input_ID.equals("") && input_PW.equals("")){
                check = false;
                break;
            }
            else if (input_ID.equals(pref.getString("ID" + i, "")) && input_PW.equals(pref.getString("PW" + i, ""))) {
                //0번째 회원가입된 아이디, 비밀번호부터 마지막 가입된 회원정보까지 탐색 후 발견시 true 반환.
                check = true;
                editor.putInt("key_user", i);  //로그인 한 회원 구별하기 위해서 유저 코드 저장. // i는 회원 가입 시에 붙인 counting.
                editor.apply();


                break;
            }else{
                check = false;
            }


        }
        return check;
    }




    public void Login() {
        Button btn = findViewById(R.id.btn_login_LOGIN);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            ///이부분 안넣어줬었는데 왜 됬지,.?
                firebaseAuth.signInWithEmailAndPassword(ET_ID.getText().toString(),ET_PW.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()){

                                    //로그인 실패
                                    Toast.makeText(Simple_Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    String input_ID = ET_ID.getText().toString();
                                    String input_PW = ET_PW.getText().toString();
                                    boolean validation = Validate_Login(input_ID, input_PW);


                                    if (validation) {



                                        Intent intent = new Intent(Simple_Login.this, TimeLine_for_everybody.class);
                                        startActivity(intent);
                                        finish();


                                        Toast.makeText(Simple_Login.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(Simple_Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });



            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (requestCode == RQ_CODE_REGI && resultCode == RESULT_OK) {
//            int size = pref.getInt("LAST_USER_CODE",0);
//            editor.putInt("max_FOR",size);
//            editor.apply();
            ID = pref.getString("ID" + pref.getInt("LAST_USER_CODE", 0), ""); // 회원 가입 후 아이디 자동완성
            PW = pref.getString("PW" + pref.getInt("LAST_USER_CODE", 0), ""); // 회원 가입 후 비밀번호 자동완성
            ET_ID = findViewById(R.id.ID_LOGIN);
            ET_PW = findViewById(R.id.PW_LOGIN);
            ET_ID.setText(ID);
            ET_PW.setText(PW);

        }
    }
    void loginEvent(){



    }
    void GO_register() {
        btn_register = findViewById(R.id.btn_register_LOGIN);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Simple_Login.this, Registration.class);
                startActivityForResult(intent, RQ_CODE_REGI);
            }
        });
    }



}