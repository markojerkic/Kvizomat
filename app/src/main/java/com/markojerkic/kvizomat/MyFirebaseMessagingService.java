package com.markojerkic.kvizomat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d("Test", remoteMessage.getNotification().getBody());

        KvizomatApp app = (KvizomatApp) getApplication();

        Map<String, String> messageData = remoteMessage.getData();
        String s =  messageData.toString();
        String izazivac = remoteMessage.getData().get("izazivatelj");
        String mojUid = remoteMessage.getData().get("protivnik");
        String kviz = remoteMessage.getData().get("kviz");
        obavijestPosalji(kviz, izazivac, remoteMessage);
        Log.d("izazov", kviz);
    }

    private void obavijestPosalji(String k, String izazivac, RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("izazivac", izazivac);
        intent.putExtra("kviz", k);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification obavijest = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentIntent(pi)
                .build();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
        nm.notify(123, obavijest);
//        super.onremoteMessageReceived(remoteremoteMessage);
    }

}
