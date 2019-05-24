package com.example.htpad.spread_thoughts_proj1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
        private static int STOP = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },STOP);

//
//mContext = getApplicationContext();
//        getHashKey(mContext);


    }

//    @Nullable
//
//    public static String getHashKey(Context context) {
//
//        final String TAG = "KeyHash";
//
//        String keyHash = null;
//
//        try {
//
//            PackageInfo info =
//
//                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
//
//
//
//            for (Signature signature : info.signatures) {
//
//                MessageDigest md;
//
//                md = MessageDigest.getInstance("SHA");
//
//                md.update(signature.toByteArray());
//
//                keyHash = new String(Base64.encode(md.digest(), 0));
//
//                Log.d(TAG, keyHash);
//
//            }
//
//        } catch (Exception e) {
//
//            Log.e("name not found", e.toString());
//
//        }
//
//
//
//        if (keyHash != null) {
//
//            return keyHash;
//
//        } else {
//
//            return null;
//
//        }
//
//    }


}
