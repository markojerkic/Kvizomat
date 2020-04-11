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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.markojerkic.kvizomat.FirebaseKorisnikCallback;
import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlinePrijatelji#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlinePrijatelji extends Fragment {

    private ListView listView;
    private OnlinePrijateljiAdapter adapter;
    private Context context;
    private FirebaseFunctions functions;

    private KvizomatApp app;
    private ArrayList<Korisnik> prijatelji;
    private ArrayList<Korisnik> korisnici;
    private Korisnik trenutniKorisnik;
    private Dialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;


    public OnlinePrijatelji(Context context) {
        // Required empty public constructor
        this.context = context;
        app = (KvizomatApp) context.getApplicationContext();
        app.getTrenutniKorisnik(new FirebaseKorisnikCallback() {
            @Override
            public void onCallback(Korisnik korisnik) {
                trenutniKorisnik = korisnik;
            }
        });
        korisnici = app.getListaKorisnika();
        prijatelji = app.getListaPrijatelja();
        functions = FirebaseFunctions.getInstance();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_korisnik);
    }

    public static OnlinePrijatelji newInstance(Context context) {
        OnlinePrijatelji fragment = new OnlinePrijatelji(context);
        return fragment;
    }

    public void updatePrijatelji() {
        app.updateOnlinePrijatelji(new FirebaseKorisnikCallback() {
            @Override
            public void onCallback(Korisnik korisnik) {
                for (Korisnik k: prijatelji) {
                    if (k.getUid().equals(korisnik.getUid())) {
                        k.setOnline(korisnik.isOnline());
                        k.setBodovi(korisnik.getBodovi());
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                }
            }
        });
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
        adapter = new OnlinePrijateljiAdapter(prijatelji, context, trenutniKorisnik);
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
                        updatePrijatelji();
                        dialog.cancel();
                    }
                });
                igrajPopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!izabraniPrijatelj.isOnline()) {
                            Toast.makeText(context, "Prijatelj trenutno nije na vezi",
                                    Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else {
                            izazovi(izabraniPrijatelj.getUid());
                            dialog.cancel();
                        }
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
            }
        });

        return view;
    }

    private void izazovi(String protivnik) {
        Map<String, Object> data = new HashMap<>();
        data.put("protivnik", protivnik);
        data.put("mojUid", trenutniKorisnik.getUid());

        functions.getHttpsCallable("sendSpecific").call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                Toast.makeText(context, "Zahtjev poslan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
