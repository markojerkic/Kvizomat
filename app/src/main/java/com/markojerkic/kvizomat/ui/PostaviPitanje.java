package com.markojerkic.kvizomat.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.util.ArrayList;

public class PostaviPitanje extends AppCompatActivity {
    private EditText mPitanje;
    private EditText mA;
    private EditText mB;
    private EditText mC;
    private EditText mD;
    private Button mSpremiPitanje;
    private Button mSljedecePitanje;
    private RadioGroup mTezinaPitanjaRadio;
    private RadioGroup mTocanOdgovorRadio;
    private int tocanOdgovorBroj, tezinaPitanjaBroj;

    private ArrayList<Pitanje> mPitanjaList;

    private int brojPitanja = 0;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("pitanja");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postavi_pitanje);

        mPitanje = findViewById(R.id.postavi_pitanje_edit_text);
        mA = findViewById(R.id.odgovor_a_edit_text);
        mB = findViewById(R.id.odgovor_b_edit_text);
        mC = findViewById(R.id.odgovor_c_edit_text);
        mD = findViewById(R.id.odgovor_d_edit_text);
        mSljedecePitanje = findViewById(R.id.sljedeće_pitanje_uredivanje);
        mSpremiPitanje = findViewById(R.id.stavi_pitanje_baza_pod);
        mTezinaPitanjaRadio = findViewById(R.id.tezina_pitanja_izaberi);
        mTocanOdgovorRadio = findViewById(R.id.tocan_odgovor_radio_group);

        mPitanjaList = new ArrayList<>();

        // Listener koji sluša kad dođu nova pitanja

        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Pitanje pitanje = dataSnapshot.getValue(Pitanje.class);
                mPitanjaList.add(pitanje);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(childListener);

        mTocanOdgovorRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.tocan_odgovor_a:
                        tocanOdgovorBroj = 1;
                        break;
                    case R.id.tocan_odgovor_b:
                        tocanOdgovorBroj = 2;
                        break;
                    case R.id.tocan_odgovor_c:
                        tocanOdgovorBroj = 3;
                        break;
                    case R.id.tocan_odgovor_d:
                        tocanOdgovorBroj = 4;
                        break;
                    default:
                        tocanOdgovorBroj = 3;
                        break;
                }
                Log.e("pitanja", "tocan" + tocanOdgovorBroj);
            }
        });
        mTezinaPitanjaRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.tezina_level_1:
                        tezinaPitanjaBroj = 1;
                        break;
                    case R.id.tezina_level_2:
                        tezinaPitanjaBroj = 2;
                        break;
                    case R.id.tezina_level_3:
                        tezinaPitanjaBroj = 3;
                        break;
                    case R.id.tezina_level_4:
                        tezinaPitanjaBroj = 4;
                        break;
                    default:
                        tezinaPitanjaBroj = 2;
                        break;
                }
                Log.e("pitanja", "tezina" + tezinaPitanjaBroj);
            }
        });

        mSpremiPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pitanje = mPitanje.getText().toString();
                String odgovorA = mA.getText().toString();
                String odgovorB = mB.getText().toString();
                String odgovorC = mC.getText().toString();
                String odgovorD = mD.getText().toString();

                // Ubaci pitanje u bazu podataka
                mRef.push().setValue(new Pitanje(pitanje, odgovorA, odgovorB, odgovorC, odgovorD,
                        "zasad", tezinaPitanjaBroj, tocanOdgovorBroj));
                Toast.makeText(getApplicationContext(), "Pitanje postavljeno!", Toast.LENGTH_SHORT)
                    .show();

            }
        });

        mSljedecePitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPitanjaList.size() > 0) {
                    Pitanje sljPitanje = sljedecePitanje();
                    // Postavi vrijednosti teksta
                    mPitanje.setText(sljPitanje.getPitanje());
                    mA.setText(sljPitanje.getOdgovorA());
                    mB.setText((sljPitanje.getOdgovorB()));
                    mC.setText(sljPitanje.getOdgovorC());
                    mD.setText(sljPitanje.getOdgovorD());

                    switch (tezinaPitanjaBroj) {
                        case 1:
                            mTezinaPitanjaRadio.check(R.id.tezina_level_1);
                            break;
                        case 2:
                            mTezinaPitanjaRadio.check(R.id.tezina_level_2);
                            break;
                        case 3:
                            mTezinaPitanjaRadio.check(R.id.tezina_level_3);
                            break;
                        case 4:
                            mTezinaPitanjaRadio.check(R.id.tezina_level_4);
                            break;
                    }
                    switch (tocanOdgovorBroj) {
                        case 1:
                            mTocanOdgovorRadio.check(R.id.tocan_odgovor_a);
                            break;
                        case 2:
                            mTocanOdgovorRadio.check(R.id.tocan_odgovor_b);
                            break;
                        case 3:
                            mTocanOdgovorRadio.check(R.id.tocan_odgovor_c);
                            break;
                        case 4:
                            mTocanOdgovorRadio.check(R.id.tocan_odgovor_d);
                            break;
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Nema pitanja u bazi podataka!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private Pitanje sljedecePitanje() {
        brojPitanja %= mPitanjaList.size();
        Log.d("Pitanja", mPitanjaList.size() + " " + printajPitanja());
        return mPitanjaList.get(brojPitanja++);
    }

    private String printajPitanja() {
        String rez = "";
        for (int i = 0; i < mPitanjaList.size(); i++){
            rez += mPitanjaList.get(i).getPitanje() + " ";
        }
        return rez;
    }
}
