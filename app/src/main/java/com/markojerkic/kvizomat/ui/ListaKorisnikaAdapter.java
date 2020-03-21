package com.markojerkic.kvizomat.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListaKorisnikaAdapter extends BaseAdapter {

    private ArrayList<Korisnik> korisnici;
    private Context context;
    private Korisnik trKor;

    public ListaKorisnikaAdapter(ArrayList<Korisnik> korisnici, Context context, Korisnik trKor) {
        this.korisnici = korisnici;
        this.context = context;
        this.trKor = trKor;
    }

    @Override
    public int getCount() {
        return korisnici.size();
    }

    @Override
    public Object getItem(int position) {
        return korisnici.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.korisnik_jedan, parent, false);
        }

        Korisnik k = (Korisnik) getItem(position);

        ImageView slika = convertView.findViewById(R.id.slika_korisnik_list);
        TextView ime = convertView.findViewById(R.id.ime_korisnika_list);
        TextView email = convertView.findViewById(R.id.email_korisnika_list);
        TextView medjuPrijeateljima = convertView.findViewById(R.id.prijatelj_ili_ne);

        ime.setText(k.getIme());
        Log.d("prijatelj", String.valueOf(trKor.getPrijatelji().contains(k.getUid())));
        if (trKor.getPrijatelji().contains(k.getUid())) {
            medjuPrijeateljima.setText(R.string.moj_prijatelj);
        } else {
            medjuPrijeateljima.setText(R.string.nije_prijatelj);
        }
        email.setText(k.getEmail());

        if (!k.getUri().equals("null") && NetworkConnection.hasConnection(context))
            Picasso.get().load(k.getUri()).into(slika);

        if (k.getUid().equals(trKor.getUid())) {
            convertView.setClickable(false);
            ime.setText("Ja");
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }

        return convertView;

    }
}
