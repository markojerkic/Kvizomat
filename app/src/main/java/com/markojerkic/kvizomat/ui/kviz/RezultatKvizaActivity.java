package com.markojerkic.kvizomat.ui.kviz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.markojerkic.kvizomat.R;

import java.util.ArrayList;

public class RezultatKvizaActivity extends AppCompatActivity {
    private ArrayList<Pitanje> pitanja;
    private boolean[] tocno;
    private ArrayList<Integer> odgovoriKorisnika;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezultat_kviza);

        pitanja = (ArrayList<Pitanje>) getIntent().getSerializableExtra("pitanja");
        tocno = (boolean[]) getIntent().getSerializableExtra("tocno");
        odgovoriKorisnika = (ArrayList<Integer>) getIntent().getSerializableExtra("odgovoriKorisnika");

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = findViewById(R.id.rezultat_kviz_linear_layout);

        for (int i = 0; i < tocno.length; i++) {
            prikaziPitanje(pitanja.get(i), tocno[i], odgovoriKorisnika.get(i), inflater);
        }
    }

    private void prikaziPitanje(Pitanje pitanje, boolean tocno, int brojOdgovora, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.kviz_rezultat_jedan, null);
        TextView pitanjeText = v.findViewById(R.id.rezultat_pitanje);
        TextView tocanOdgovor = v.findViewById(R.id.tocan_odgovor_rezultat);
        TextView tocanBoolean = v.findViewById(R.id.rezultat_tocno_boolean);

        pitanjeText.setText(pitanje.getPitanje());
        tocanOdgovor.setText(pitanje.tocnoString());
        String odgovorKorisnika = odrediOdgovor(pitanje, brojOdgovora);
        String tocnoOdgovoreno = "";
        if (tocno) {
            tocanBoolean.setTextColor(getResources().getColor(R.color.zeleo_tocno));
        } else {
            tocanBoolean.setTextColor(getResources().getColor(R.color.crveno_netocno));
        }

        tocnoOdgovoreno = odgovorKorisnika;

        tocanBoolean.setText(tocnoOdgovoreno);

        linearLayout.addView(v);
    }

    private String odrediOdgovor(Pitanje pitanje, int brojOdgovora) {
        if (brojOdgovora == 1)
            return pitanje.getOdgovorA();
        else if (brojOdgovora == 2)
            return pitanje.getOdgovorB();
        else if (brojOdgovora == 3)
            return pitanje.getOdgovorC();
        return pitanje.getOdgovorD();
    }
}
