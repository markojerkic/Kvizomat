package com.markojerkic.kvizomat.ui.listaKorisnika;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markojerkic.kvizomat.FirebaseKorisnikCallback;
import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.ListaKorisnikaAdapter;
import com.markojerkic.kvizomat.ui.RangListaPrijateljaAdapter;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DodajPrijateljaFragment extends Fragment {

    FirebaseUser user;

    private DodajPrijateljaViewModel dodajPrijateljaViewModel;

    private ArrayList<Korisnik> korisnici;
    ArrayList<String> korisniciKey;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisniciOnline");

    private ListView listaView;
    private ListaKorisnikaAdapter listaKorisnikaAdapter;
    private RangListaPrijateljaAdapter rangListaPrijateljaAdapter;
    private Korisnik trenutniKorisnik;
    private String trKorisnikKey;

    private TextView korisniciListaTextView;
    private TextView mojiPrijateljiListaTextView;

    private boolean svi = true;
    private FirebaseUser trenutniUser;
    private KvizomatApp app;

    private TextView prijateljIliNeText;

    private ArrayList<String> prijateljiString;
    private ArrayList<Korisnik> prijateljiKorisnik;

    private Dialog dialog;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dodajPrijateljaViewModel = ViewModelProviders.of(this).get(DodajPrijateljaViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dodaj_prijatelja, container, false);

        app = (KvizomatApp) getContext().getApplicationContext();

        korisnici = new ArrayList<>();
        korisniciKey = new ArrayList<>();
        prijateljiKorisnik = new ArrayList<>();
        dialog = new Dialog(getContext());

        trenutniUser = app.getTrenutniUser();

        listaView = root.findViewById(R.id.lista_korisnika_view);
        korisniciListaTextView = root.findViewById(R.id.lista_korisnici_text);
        mojiPrijateljiListaTextView = root.findViewById(R.id.lista_moji_prijatelji_text);
        user = FirebaseAuth.getInstance().getCurrentUser();

        korisniciListaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svi = true;
                korisniciListaTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway_bold));
                mojiPrijateljiListaTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway_medium));
                listaView.setAdapter(listaKorisnikaAdapter);
                listaKorisnikaAdapter.notifyDataSetChanged();
            }
        });
        mojiPrijateljiListaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svi = false;
                napraviListuPrijatelja();
                korisniciListaTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway_medium));
                mojiPrijateljiListaTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.raleway_bold));
                listaView.setAdapter(rangListaPrijateljaAdapter);
                rangListaPrijateljaAdapter.notifyDataSetChanged();
            }
        });

        korisnici = app.getListaKorisnika();
        app.getTrenutniKorisnik(new FirebaseKorisnikCallback() {
            @Override
            public void onCallback(Korisnik korisnik) {
                trenutniKorisnik = korisnik;
                prijateljiString = trenutniKorisnik.getPrijatelji();
            }
        });

        listaKorisnikaAdapter = new ListaKorisnikaAdapter(korisnici, getContext(), trenutniKorisnik);
        listaView.setAdapter(listaKorisnikaAdapter);
        listaKorisnikaAdapter.notifyDataSetChanged();

        /*
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik kor = ds.getValue(Korisnik.class);
                    if (kor.getUid().compareTo(trenutniUser.getUid()) != 0) {
                        korisnici.add(kor);
                        korisniciKey.add(ds.getKey());
                        Log.d("korisnici", kor.getUid());
                    } else {
                        trenutniKorisnik = kor;
                        trKorisnikKey = ds.getKey();
                        prijateljiString = trenutniKorisnik.getPrijatelji();

                        listaKorisnikaAdapter = new ListaKorisnikaAdapter(korisnici, getContext(), trenutniKorisnik);
                        listaView.setAdapter(listaKorisnikaAdapter);
                        listaKorisnikaAdapter.notifyDataSetChanged();
                    }
                    if (listaKorisnikaAdapter != null) {
                        Log.d("Prijatelji", "dodan " + kor.getIme());
                        listaKorisnikaAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Prijatelji", "lista niej gotova " + kor.getEmail());
                    }
                }
                napraviListuPrijatelja();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Korisnik izabraniKor;
                if (svi)
                    izabraniKor = korisnici.get(position);
                else
                    izabraniKor = prijateljiKorisnik.get(position);

                Log.d("korisnik", izabraniKor.getIme());
                final View thisV = view;
                prijateljIliNeText = thisV.findViewById(R.id.prijatelj_ili_ne);

                dialog.setContentView(R.layout.popup_korisnik);

                ImageView slikaKorIzbr = dialog.findViewById(R.id.korisnik_popup_slika);
                TextView imeKor = dialog.findViewById(R.id.korisnik_popup_ime);
                Button dodaj = dialog.findViewById(R.id.dodaj_prijatelja);
                Button odustani = dialog.findViewById(R.id.odustani_prijatelj);

                if (prijateljiString.contains(izabraniKor.getUid())) {
                    dodaj.setText("Izbriši prijatelja");
                } else
                    dodaj.setText("Dodaj prijatelja");

                if (!izabraniKor.getUri().equals("null"))
                    Picasso.get().load(izabraniKor.getUri()).into(slikaKorIzbr);
                imeKor.setText(izabraniKor.getIme());

                dodaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Korisnik", String.valueOf(prijateljiString.contains(izabraniKor.getUid())));
                        if (!prijateljiString.contains(izabraniKor.getUid())) {
                            prijateljiString.add(izabraniKor.getUid());
                            prijateljiKorisnik.add(izabraniKor);

                            app.setListaPrijatelja(prijateljiString);
//                            db.child(trKorisnikKey).child("prijatelji").setValue(prijateljiString);
                            if (svi)
                                prijateljIliNeText.setText("Moj prijatelj");
                            napraviListuPrijatelja();
                            Toast.makeText(getContext(), izabraniKor.getIme() +
                                    " dodan u prijatelje", Toast.LENGTH_SHORT).show();

                        } else {
                            prijateljiString.remove(izabraniKor.getUid());
                            prijateljiKorisnik.remove(izabraniKor);
                            app.setListaPrijatelja(prijateljiString);
                            if (svi)
                                prijateljIliNeText.setText("Ne poznajem ga");
                            napraviListuPrijatelja();
                            Toast.makeText(getContext(), izabraniKor.getIme() +
                                    " izbrisan iz prijatelja", Toast.LENGTH_SHORT).show();
                        }

                        dialog.cancel();
                    }
                });
                odustani.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                if (!izabraniKor.getUid().equals(trenutniKorisnik.getUid())) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.show();
                } else
                    Log.d("Klik", "korisnik sam ja");

            }
        });
        return root;
    }

    public void napraviListuPrijatelja() {
        prijateljiKorisnik = new ArrayList<>();
        prijateljiKorisnik.add(trenutniKorisnik);
        for (Korisnik kP: korisnici) {
            Log.d("Korisnik", kP.getIme());
            if (prijateljiString.contains(kP.getUid())) {
                prijateljiKorisnik.add(kP);
            }
        }
        Collections.sort(prijateljiKorisnik, new Comparator<Korisnik>() {
            @Override
            public int compare(Korisnik o1, Korisnik o2) {
                if (o1.getBodovi() > o2.getBodovi())
                    return -1;
                else if (o1.getBodovi() < o2.getBodovi())
                    return 1;
                else
                    return 0;
            }
        });

        rangListaPrijateljaAdapter = new RangListaPrijateljaAdapter(prijateljiKorisnik,
                    getContext(), trenutniKorisnik);
        if (!svi) {
            listaView.setAdapter(rangListaPrijateljaAdapter);
            rangListaPrijateljaAdapter.notifyDataSetChanged();
        }
    }
}
