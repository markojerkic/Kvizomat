package com.markojerkic.kvizomat.ui.mojiPrijatelji;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.ListaKorisnikaAdapter;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MojiPrijateljiFragment extends Fragment {

    private MojiPrijateljiViewModel mojiPrijateljiViewModel;

    FirebaseUser user;
    private ArrayList<Korisnik> korisnici;
    ArrayList<String> korisniciKey;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisnici");

    private ListView listaView;
    private ListaKorisnikaAdapter arrayAdapter;
    private Korisnik trKorisnik;

    private Dialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mojiPrijateljiViewModel =
                ViewModelProviders.of(this).get(MojiPrijateljiViewModel.class);
        View root = inflater.inflate(R.layout.fragment_moji_prijatelji, container, false);
        korisnici = new ArrayList<>();
        korisniciKey = new ArrayList<>();
        dialog = new Dialog(getContext());

        final FirebaseUser trUsr = FirebaseAuth.getInstance().getCurrentUser();

        listaView = root.findViewById(R.id.lista_mojih_prijatelja_list_view);

        user = FirebaseAuth.getInstance().getCurrentUser();

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Korisnik kor = dataSnapshot.getValue(Korisnik.class);
                if (kor.getUid().compareTo(trUsr.getUid()) != 0) {
                    if (!trKorisnik.getPrijatelji().contains(kor.getUid())) {
                        korisnici.add(kor);
                        korisniciKey.add(dataSnapshot.getKey());
                    }
                } else {
                    trKorisnik = kor;
                    arrayAdapter = new ListaKorisnikaAdapter(korisnici, getContext(), trKorisnik);
                    listaView.setAdapter(arrayAdapter);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Korisnik kor = dataSnapshot.getValue(Korisnik.class);
                for (int i = 0; i < korisnici.size(); i++) {
                    Korisnik k = korisnici.get(i);
                    if (k.getUid().equals(kor.getUid())) {
                        korisnici.remove(i);
                        k.setPrijatelji(kor.getPrijatelji());
                        korisnici.add(i, k);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Korisnik izabraniKor = korisnici.get(position);

                Log.d("korisnik", izabraniKor.getIme());

                dialog.setContentView(R.layout.popup_korisnik);

                ImageView slikaKorIzbr = dialog.findViewById(R.id.korisnik_popup_slika);
                TextView imeKor = dialog.findViewById(R.id.korisnik_popup_ime);
                Button dodaj = dialog.findViewById(R.id.dodaj_prijatelja);
                Button odustani = dialog.findViewById(R.id.odustani_prijatelj);

                dodaj.setText("Izbriši");

                Picasso.get().load(izabraniKor.getUri()).into(slikaKorIzbr);
                imeKor.setText(izabraniKor.getIme());

                dodaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> prijatelji = trKorisnik.getPrijatelji();
                        int l = prijatelji.indexOf(prijatelji.get(position));
                        prijatelji.remove(l);
                        db.child(korisniciKey.get(position)).child("prijatelji").setValue(prijatelji);
                        dialog.cancel();
//                        if (!prijatelji.contains(izabraniKor.getUid())) {
//                            prijatelji.add(izabraniKor.getUid());
//                            Log.d("Korisnik", izabraniKor.getIme());
//                            db.child(korisniciKey.get(position)).child("prijatelji").setValue(prijatelji);
//                        }
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
