package com.markojerkic.kvizomat.ui.kviz;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markojerkic.kvizomat.R;

import java.text.DecimalFormat;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Kviz extends AppCompatActivity {

    private TextView mPitanje;
    private TextView mTocniOdgovoriText;
    private TextView bodoviText;
    private Button mOdgovorA;
    private Button mOdgovorB;
    private Button mOdgovorC;
    private Button mOdgovorD;
    private Button[] listaTipki;
    private int brojTocnihOdgovora = 0;
    private float bodovi = 0.f;

    private long backPressed;

    private DecimalFormat decimalFormat;

    private DatabaseReference dbKorisnici = FirebaseDatabase.getInstance().getReference("korisnici");

    private KvizInformacije mInfo;

    private Pitanje trenutnoPitanje;
    private Korisnik mKorisnik;
    private String mKorisnikKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kviz);

        // Pronađi komponente kviza
        mPitanje = findViewById(R.id.pitanje);
        mTocniOdgovoriText = findViewById(R.id.broj_tocnih_odgovora);
        bodoviText = findViewById(R.id.bodovi_trenutni);
        mOdgovorA = findViewById(R.id.pitanje_odgovor_a);
        mOdgovorB = findViewById(R.id.pitanje_odgovor_b);
        mOdgovorC = findViewById(R.id.pitanje_odgovor_c);
        mOdgovorD = findViewById(R.id.pitanje_odgovor_d);
        listaTipki = new Button[]{mOdgovorA, mOdgovorB, mOdgovorC, mOdgovorD};

        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        mInfo = (KvizInformacije) getIntent().getSerializableExtra("pitanja");
        mKorisnik = (Korisnik) getIntent().getSerializableExtra("korisnik");
        mKorisnikKey = (String) getIntent().getSerializableExtra("korisnikKey");
        trenutnoPitanje = mInfo.getNext();

        postaviPitanja(trenutnoPitanje);
        provjeriKliknutOdgovor(trenutnoPitanje);


    }

    @Override
    public void onBackPressed() {
        int timeInterval = 2000;

        if(backPressed + timeInterval > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "Kliknite nazad dva puta da izdađete",
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    private boolean provjeriKliknutOdgovor(final Pitanje pitanje) {
        mOdgovorA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriTocnoOdgovor(pitanje, mOdgovorA, 1);
            }
        });
        mOdgovorB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriTocnoOdgovor(pitanje, mOdgovorB, 2);

            }
        });
        mOdgovorC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriTocnoOdgovor(pitanje, mOdgovorC, 3);

            }
        });
        mOdgovorD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriTocnoOdgovor(pitanje, mOdgovorD, 4);
            }
        });
        return false;
    }

    private void provjeriTocnoOdgovor(Pitanje pitanje, Button tipka, int tipkaOdgovorBroj) {
        if (pitanje.getTocanOdgovor() == tipkaOdgovorBroj) {
            postaviTocno(pitanje, tipka);
            brojTocnihOdgovora++;
        } else {
            postaviNetocno(pitanje, tipka);
        }
        mTocniOdgovoriText.setText("Točni odgovori: " +
                brojTocnihOdgovora + "/" + mInfo.getIterator());
        if (mInfo.getIterator() % 3 == 0) {
            bodovi += ((float) brojTocnihOdgovora / (float) mInfo.getIterator())
                    * (float) pitanje.getTezinaPitanja();
            bodoviText.setText("Bodovi: " + decimalFormat.format(bodovi));
        }
        for (Button button: listaTipki) {
            button.setClickable(false);
        }
        Log.e("kviz", "sljedeće pitanje se postavlja, iterator " + mInfo.getIterator());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                postaviSljedecePitanje();
            }
        },1000);
    }

    private void postaviSljedecePitanje() {
        if (mInfo.getIterator() < mInfo.brojPitanja()) {
            trenutnoPitanje = mInfo.getNext();
            postaviPitanja(trenutnoPitanje);
            for (Button tipka: listaTipki) {
                tipka.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tipka.setClickable(true);
            }
            Log.e("kviz", "sljedeće pitanje se postavlja, iterator " + mInfo.getIterator());
            provjeriKliknutOdgovor(trenutnoPitanje);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    zatvoriAktivnost();
                }
            }, 1000);
        }
    }

    private void zatvoriAktivnost() {
        Toast.makeText(getApplicationContext(), "Doviđenja", Toast.LENGTH_SHORT).show();
        mKorisnik.setBodovi(mKorisnik.getBodovi() + bodovi);
        dbKorisnici.child(mKorisnikKey).child("bodovi").setValue(mKorisnik.getBodovi() + bodovi);
        this.finish();
    }

    private void postaviTocno(Pitanje pitanje, Button tipka) {
        tipka.setBackgroundColor(getResources().getColor(R.color.zeleo_tocno));
        Toast.makeText(getApplicationContext(), "Točan odgovor!", Toast.LENGTH_SHORT).show();
    }

    private void postaviNetocno(Pitanje pitanje, Button netocnaTipka) {
        netocnaTipka.setBackgroundColor(getResources().getColor(R.color.crveno_netocno));
        switch (pitanje.getTocanOdgovor()) {
            case 1:
                mOdgovorA.setBackgroundColor(getResources().getColor(R.color.zeleo_tocno));
                Log.e("kviz", "tocan odgovor a");
                break;
            case 2:
                mOdgovorB.setBackgroundColor(getResources().getColor(R.color.zeleo_tocno));
                Log.e("kviz", "tocan odgovor a");
                break;
            case 3:
                mOdgovorC.setBackgroundColor(getResources().getColor(R.color.zeleo_tocno));
                Log.e("kviz", "tocan odgovor a");
                break;
            case 4:
                mOdgovorD.setBackgroundColor(getResources().getColor(R.color.zeleo_tocno));
                Log.e("kviz", "tocan odgovor a");
                break;
            default:
                break;
        }
        Toast.makeText(getApplicationContext(), "Netočno :( (:", Toast.LENGTH_SHORT).show();
    }

    private void postaviPitanja(Pitanje pitanje) {
        mPitanje.setText(pitanje.getPitanje());
        mOdgovorA.setText(pitanje.getOdgovorA());
        mOdgovorB.setText(pitanje.getOdgovorB());
        mOdgovorC.setText(pitanje.getOdgovorC());
        mOdgovorD.setText(pitanje.getOdgovorD());
    }
}
