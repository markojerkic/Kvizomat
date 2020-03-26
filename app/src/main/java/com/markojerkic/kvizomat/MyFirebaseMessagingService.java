package com.markojerkic.kvizomat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("Test", remoteMessage.getNotification().getBody());
//        super.onMessageReceived(remoteMessage);
        sendMyNotification(remoteMessage);
    }

    private void sendMyNotification(RemoteMessage message) {

        KvizomatApp app = (KvizomatApp) getApplication();
        Korisnik izazivač = app.findPrijatelj(message.getData().get("mojUid"));

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 5,
                i, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification obavijest = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentText("Korisnik " + izazivač.getIme() + " vas izaziva na dvoboj")
                .setContentTitle("Korisnik " + izazivač.getIme() + " vas izaziva na dvoboj")
                .setContentIntent(pi)
                .build();

//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
        nm.notify(0, obavijest);
    }

}
