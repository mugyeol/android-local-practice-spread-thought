package com.example.htpad.spread_thoughts_proj1.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.htpad.spread_thoughts_proj1.MainActivity;
import com.example.htpad.spread_thoughts_proj1.R;
import com.example.htpad.spread_thoughts_proj1.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
public void onMessageReceived(RemoteMessage remoteMessage) {

String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//받는쪽

    // Check if message contains a data payload.


        SharedPreferences pref_user= getSharedPreferences("USER_INFO",MODE_PRIVATE);
        Boolean isinMessage = pref_user.getBoolean("checkLocation"+uid,false);

        if (remoteMessage.getData().size() > 0) {
        String title= remoteMessage.getData().get("title").toString();
        String text = remoteMessage.getData().get("text").toString();
        String destinationaUid = remoteMessage.getData().get("destinationUid").toString();//message액티비티에서 보낸 상대방.

        Log.d("확인","destinationUid"+destinationaUid);


        //현재 로그인 된 유저와 메세지 수신자 uid 가 같을 때와 수신자가 메세지 액티비티에 없을 때만 노티 보내기.
        if (uid.equals(destinationaUid) && !isinMessage) {
            sendNotification(title, text);
        }


    }

}

    private void sendNotification(String title, String text) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_channel_ID);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)

                        .setContentTitle(title)
                        .setContentText(text)


                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
