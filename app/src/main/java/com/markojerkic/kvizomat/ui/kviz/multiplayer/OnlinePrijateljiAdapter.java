package com.markojerkic.kvizomat.ui.kviz.multiplayer;

import android.content.Context;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OnlinePrijateljiAdapter extends BaseAdapter {

    private ArrayList<Korisnik> prijatelji;
    private Context context;
    private Korisnik trKor;

    private TextView ime;
    private TextView bodovi;
    private TextView online;
    private ImageView slika;

    private DecimalFormat decimalFormat;

    public OnlinePrijateljiAdapter(ArrayList<Korisnik> prijatelji, Context context, Korisnik korisnik) {
        this.context = context;
        this.prijatelji = prijatelji;
        this.trKor = korisnik;

        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
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
                    .inflate(R.layout.prijatelji_online_jedan_korisnik, parent, false);
        }

        Korisnik prijatelj = prijatelji.get(position);

        ime = convertView.findViewById(R.id.ime_prijatelja_online);
        bodovi = convertView.findViewById(R.id.bodovi_prijatelja_online);
        online = convertView.findViewById(R.id.online_ili_ne_jedan_prijatelj);
        slika = convertView.findViewById(R.id.slika_prijatelja_online);


        if (!prijatelj.getUri().equals("null") && NetworkConnection.hasConnection(context))
            Picasso.get().load(prijatelj.getUri()).into(slika);

        ime.setText(prijatelj.getIme());
        bodovi.setText(decimalFormat.format(prijatelj.getBodovi()));
        if (prijatelj.isOnline())
            online.setText("na vezi");


        return convertView;
    }
}
