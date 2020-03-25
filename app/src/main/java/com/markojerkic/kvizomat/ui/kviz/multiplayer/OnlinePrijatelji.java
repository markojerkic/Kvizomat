package com.markojerkic.kvizomat.ui.kviz.multiplayer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlinePrijatelji#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlinePrijatelji extends Fragment {

    private ListView listView;
    private OnlinePrijateljiAdapter adapter;
    private Context context;

    private KvizomatApp app;
    private ArrayList<Korisnik> prijatelji;
    private ArrayList<Korisnik> korisnici;
    private Korisnik trenutniKorisnik;
    private Dialog dialog;


    public OnlinePrijatelji(Context context) {
        // Required empty public constructor
        this.context = context;
        app = (KvizomatApp) context.getApplicationContext();
        trenutniKorisnik = app.getTrenutniKorisnik();
        korisnici = app.getListaKorisnika();
        prijatelji = app.getListaPrijatelja();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_korisnik);
    }

    public static OnlinePrijatelji newInstance(Context context) {
        OnlinePrijatelji fragment = new OnlinePrijatelji(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_prijatelji, container, false);

        listView = view.findViewById(R.id.online_prijatlji_listview_multiplayer);
        adapter = new OnlinePrijateljiAdapter(prijatelji, getContext(), trenutniKorisnik);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View dialogView, int position, long id) {

                ImageView popupSlika = dialog.findViewById(R.id.korisnik_popup_slika);
                TextView popupIme = dialog.findViewById(R.id.korisnik_popup_ime);
                Button igrajPopup = dialog.findViewById(R.id.dodaj_prijatelja);
                Button odustaniPopup = dialog.findViewById(R.id.odustani_prijatelj);

                final Korisnik izabraniPrijatelj = prijatelji.get(position);
                popupIme.setText(izabraniPrijatelj.getIme());
                if (!izabraniPrijatelj.getUri().equals("null") && NetworkConnection.hasConnection(context))
                    Picasso.get().load(izabraniPrijatelj.getUri()).into(popupSlika);
                igrajPopup.setText("Izazovi");
                odustaniPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                igrajPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!izabraniPrijatelj.isOnline())
                            Toast.makeText(context, "Prijatelj trenutno nije na vezi",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
            }
        });

        return view;
    }
}
