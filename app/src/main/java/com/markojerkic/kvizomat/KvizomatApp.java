package com.markojerkic.kvizomat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;

public class KvizomatApp extends Application {

    private FirebaseUser mUser;
    private DatabaseReference korisniciReference;
    private Korisnik upKor;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        korisniciReference = FirebaseDatabase.getInstance().getReference("korisniciOnline");
        if (mUser == null)
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            setKorisnik();
            korisniciReference.child(mUser.getUid()).child("online").onDisconnect().setValue(false);
        }
    }
    public void setKorisnik() {
        DatabaseReference trKorRef = korisniciReference.child(mUser.getUid());
        trKorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Korisnik k = dataSnapshot.getValue(Korisnik.class);
                Log.d("Test s korisnikom", k.getIme());
                upKor = k;

                korisniciReference.child(upKor.getUid()).child("online").setValue(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
