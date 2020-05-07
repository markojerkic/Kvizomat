package com.markojerkic.kvizomat.ui.kviz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.markojerkic.kvizomat.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RezultatKvizaActivity extends AppCompatActivity {
    private ArrayList<Pitanje> pitanja;
    private boolean[] tocno;
    private ArrayList<Integer> odgovoriKorisnika, odgovoriIzaz;
    private LinearLayout linearLayout;
    private float trBod, izBod;
    private TextView trBodoviText, izBodoviText;
    private DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezultat_kviza);

        trBodoviText = findViewById(R.id.rezultat_tv_bodovi);
        izBodoviText = findViewById(R.id.rezultat_pr_bodovi);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(3);

        pitanja = (ArrayList<Pitanje>) getIntent().getSerializableExtra("pitanja");
        odgovoriKorisnika = (ArrayList<Integer>) getIntent().getSerializableExtra("odgovoriKorisnika");
        odgovoriIzaz = (ArrayList<Integer>) getIntent().getSerializableExtra("odgovoriIzaz");

        if (odgovoriKorisnika != null) {
            trBod = izrBod(odgovoriKorisnika);
            trBodoviText.setText(String.format("Vaši bodovi:\n%s", df.format(trBod)));
        }
        if (odgovoriIzaz != null) {
            izBod = izrBod(odgovoriIzaz);
            izBodoviText.setText(String.format("Bodovi protivnika:\n%s", df.format(izBod)));
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = findViewById(R.id.rezultat_kviz_linear_layout);

        for (int i = 0; i < pitanja.size(); i++) {
            prikaziPitanje(pitanja.get(i), odgovoriKorisnika.get(i), i, inflater);
        }
    }

    private float izrBod(ArrayList<Integer> odg) {
        return Bodovi.izracunajBodove(pitanja, odg);
    }

    private boolean tocno(Pitanje pitanje, int odgovor) {
        return pitanje.getTocanOdgovor() == odgovor;
    }

    private void prikaziPitanje(Pitanje pitanje, int odgKor, int i, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.kviz_rezultat_jedan, null);
        TextView pitanjeText = v.findViewById(R.id.rezultat_pitanje);
        TextView tocanOdgovor = v.findViewById(R.id.tocan_odgovor_rezultat);
        TextView odgovorTrKorTekst = v.findViewById(R.id.rezultat_tocno_boolean);
        TextView odgovorProtivnikaTekst = v.findViewById(R.id.rezultat_protivnik);

        pitanjeText.setText(pitanje.getPitanje());
        tocanOdgovor.setText(pitanje.tocnoString());
        String odgovorKorisnika = odrediOdgovor(pitanje, odgKor);
        String tocnoOdgovoreno = "";
        if (tocno(pitanje, odgKor)) {
            odgovorTrKorTekst.setTextColor(getResources().getColor(R.color.zeleo_tocno));
        } else {
            odgovorTrKorTekst.setTextColor(getResources().getColor(R.color.crveno_netocno));
        }

        if (odgovoriIzaz != null) {
            int odgProtivnik = odgovoriIzaz.get(i);
            odgovorProtivnikaTekst.setText(odrediOdgovor(pitanje, odgProtivnik));
            if (tocno(pitanje, odgProtivnik)) {
                odgovorProtivnikaTekst.setTextColor(getResources().getColor(R.color.zeleo_tocno));
            }else {
                odgovorProtivnikaTekst.setTextColor(getResources().getColor(R.color.crveno_netocno));
            }
        } else {
            odgovorProtivnikaTekst.setText("Nije odgovoreno");
        }

        tocnoOdgovoreno = odgovorKorisnika;

        odgovorTrKorTekst.setText(tocnoOdgovoreno);

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
