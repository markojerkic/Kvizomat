package com.markojerkic.kvizomat.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Korisnik> korisnici;
    private Context context;

    ListAdapter(ArrayList<Korisnik> korisnici, Context context) {
        this.korisnici = korisnici;
        this.context = context;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.korisnik_jedan, parent);

        ImageView slika = convertView.findViewById(R.id.slika_korisnik_list);
        TextView ime = convertView.findViewById(R.id.ime_korisnika_list);
        TextView email = convertView.findViewById(R.id.email_korisnika_list);

        ime.setText(korisnici.get(0).getIme());
        email.setText(korisnici.get(0).getEmail());

        return convertView;

    }
}
