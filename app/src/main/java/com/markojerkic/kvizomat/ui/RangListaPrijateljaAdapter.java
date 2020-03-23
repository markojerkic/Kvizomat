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

public class RangListaPrijateljaAdapter extends BaseAdapter {

    private ArrayList<Korisnik> prijatelji;
    private Context context;
    private Korisnik trKor;

    public RangListaPrijateljaAdapter(ArrayList<Korisnik> prijatelji, Context context, Korisnik korisnik) {
        this.context = context;
        this.prijatelji = prijatelji;
        this.trKor = korisnik;
    }

    @Override
    public int getCount() {
        return prijatelji.size();
    }

    @Override
    public Object getItem(int position) {
        return prijatelji.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.korisnik_leaderboard, parent, false);
        }

        Korisnik k = (Korisnik) getItem(position);

        ImageView slika = convertView.findViewById(R.id.slika_korisnik_list_leaderboard);
        TextView ime = convertView.findViewById(R.id.ime_korisnika_list_leaderboard);
        TextView email = convertView.findViewById(R.id.email_korisnika_list_leaderboard);
        TextView brojBodova = convertView.findViewById(R.id.broj_bodova_rang);
        TextView rang = convertView.findViewById(R.id.leaderboard_rang);

        ime.setText(k.getIme());
        rang.setText("#" + String.valueOf(position+1));
        Log.d("prijatelj", String.valueOf(trKor.getPrijatelji().contains(k.getUid())));

        brojBodova.setText("Broj bodova: " + k.getBodovi());

        email.setText(k.getEmail());
        if (!k.getUri().equals("null") && NetworkConnection.hasConnection(context))
            Picasso.get().load(k.getUri()).into(slika);

        if (k.getUid().equals(trKor.getUid())) {
//            convertView.setClickable(true);
            ime.setText("Ja");
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
//            convertView.setClickable(true);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return convertView;
    }
}
