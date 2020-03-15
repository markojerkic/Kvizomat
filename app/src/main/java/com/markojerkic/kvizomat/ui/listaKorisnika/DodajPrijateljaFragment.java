package com.markojerkic.kvizomat.ui.listaKorisnika;

import android.app.Dialog;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.ListAdapter;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DodajPrijateljaFragment extends Fragment {

    FirebaseUser user;

    private DodajPrijateljaViewModel dodajPrijateljaViewModel;

    private ArrayList<Korisnik> korisnici;
    ArrayList<String> korisniciKey;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisnici");

    private ListView listaView;
    private ListAdapter arrayAdapter;
    private Korisnik trKorisnik;
    private String trKorisnikKey;

    private TextView prijateljIliNeText;

    private ArrayList<String> prijatelji;

    private Dialog dialog;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dodajPrijateljaViewModel = ViewModelProviders.of(this).get(DodajPrijateljaViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dodaj_prijatelja, container, false);

        korisnici = new ArrayList<>();
        korisniciKey = new ArrayList<>();
        dialog = new Dialog(getContext());

        final FirebaseUser trUsr = FirebaseAuth.getInstance().getCurrentUser();

        listaView = root.findViewById(R.id.lista_korisnika_view);

        user = FirebaseAuth.getInstance().getCurrentUser();

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik kor = ds.getValue(Korisnik.class);
                    if (kor.getUid().compareTo(trUsr.getUid()) != 0) {
                        korisnici.add(kor);
                        korisniciKey.add(ds.getKey());
                        Log.d("korisnici", kor.getUid());
                    } else {
                        trKorisnik = kor;
                        trKorisnikKey = ds.getKey();
                        prijatelji = trKorisnik.getPrijatelji();

                        arrayAdapter = new ListAdapter(korisnici, getContext(), trKorisnik);
                        listaView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    if (arrayAdapter != null) {
                        Log.d("Prijatelji", "dodan " + kor.getIme());
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Prijatelji", "lista niej gotova " + kor.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        db.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Korisnik kor = dataSnapshot.getValue(Korisnik.class);
//                if (kor.getUid().compareTo(trUsr.getUid()) != 0) {
//                    korisnici.add(kor);
//                    korisniciKey.add(dataSnapshot.getKey());
//                    Log.d("korisnici", kor.getUid());
//                } else {
//                    trKorisnik = kor;
//                    trKorisnikKey = dataSnapshot.getKey();
//                    arrayAdapter = new ListAdapter(korisnici, getContext(), trKorisnik);
//                    listaView.setAdapter(arrayAdapter);
//                    arrayAdapter.notifyDataSetChanged();
//                }
//                if (arrayAdapter != null) {
//                    Log.d("Prijatelji", "dodan " + kor.getIme());
//                    arrayAdapter.notifyDataSetChanged();
//                } else {
//                    Log.d("Prijatelji", "lista niej gotova " + kor.getEmail());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Korisnik izabraniKor = korisnici.get(position);

                Log.d("korisnik", izabraniKor.getIme());
                final View thisV = view;
                 prijateljIliNeText = thisV.findViewById(R.id.prijatelj_ili_ne);

                dialog.setContentView(R.layout.popup_korisnik);

                ImageView slikaKorIzbr = dialog.findViewById(R.id.korisnik_popup_slika);
                TextView imeKor = dialog.findViewById(R.id.korisnik_popup_ime);
                Button dodaj = dialog.findViewById(R.id.dodaj_prijatelja);
                Button odustani = dialog.findViewById(R.id.odustani_prijatelj);

                if (prijatelji.contains(izabraniKor.getUid())) {
                    dodaj.setText("Izbriši prijatelja");
                } else
                    dodaj.setText("Dodaj prijatelja");

                Picasso.get().load(izabraniKor.getUri()).into(slikaKorIzbr);
                imeKor.setText(izabraniKor.getIme());

                dodaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Korisnik", String.valueOf(prijatelji.contains(izabraniKor.getUid())));
                        if (!prijatelji.contains(izabraniKor.getUid())) {
                            prijatelji.add(izabraniKor.getUid());
                            Log.d("Korisnik key", korisniciKey.get(position));

                            db.child(trKorisnikKey).child("prijatelji").setValue(prijatelji);
                            prijateljIliNeText.setText("Moj prijatelj");
                            Toast.makeText(getContext(), izabraniKor.getIme() +
                                    " dodan u prijatelje", Toast.LENGTH_SHORT).show();

                        } else {
                            prijatelji.remove(izabraniKor.getUid());
                            db.child(trKorisnikKey).child("prijatelji").setValue(prijatelji);
                            prijateljIliNeText.setText("Ne poznajem ga");
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

                dialog.show();

            }
        });
        return root;
    }
}
