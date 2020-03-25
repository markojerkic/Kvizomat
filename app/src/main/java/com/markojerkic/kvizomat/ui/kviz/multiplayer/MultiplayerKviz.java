package com.markojerkic.kvizomat.ui.kviz.multiplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.markojerkic.kvizomat.R;

public class MultiplayerKviz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        OnlinePrijatelji onlinePrijateljiFragment = OnlinePrijatelji.newInstance(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_multiplayer, onlinePrijateljiFragment);
        fragmentTransaction.commit();
    }
}
