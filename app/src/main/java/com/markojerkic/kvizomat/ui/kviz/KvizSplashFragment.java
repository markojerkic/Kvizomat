package com.markojerkic.kvizomat.ui.kviz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KvizSplashFragment extends AppCompatActivity {

    private KvizInformacije mInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mInfo = (KvizInformacije) getIntent().getSerializableExtra("pitanja");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent pitanjeActivity = new Intent(getApplicationContext(), Kviz.class);

                pitanjeActivity.putExtra("pitanja", mInfo);
                startActivity(pitanjeActivity);
            }
        }, 1000);
    }
}
