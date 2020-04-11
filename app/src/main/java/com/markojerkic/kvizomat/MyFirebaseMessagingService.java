package com.markojerkic.kvizomat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Test", remoteMessage.getNotification().getBody());

        KvizomatApp app = (KvizomatApp) getApplication();

        Map<String, String> messageData = remoteMessage.getData();
        String s =  messageData.toString();
        Korisnik izazivac = app.findPrijatelj(remoteMessage.getData().get("izazivatelj"));
        String mojUid = remoteMessage.getData().get("protivnik");
        String kviz = remoteMessage.getData().get("kviz");
        Log.d("izazov", mojUid);
        Log.d("izazov", kviz);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("izazov", izazivac.getIme());

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
