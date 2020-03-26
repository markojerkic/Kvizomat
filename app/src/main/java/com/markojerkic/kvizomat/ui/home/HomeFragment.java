package com.markojerkic.kvizomat.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.markojerkic.kvizomat.FirebaseKorisnikCallback;
import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.markojerkic.kvizomat.ui.kviz.KvizActivity;
import com.markojerkic.kvizomat.ui.kviz.KvizInformacije;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.MultiplayerKviz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final int BROJ_PITANJA_PO_KATEGORIJI = 3;
    private ArrayList<Pitanje> mListaPitanja;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRefPitanja;
    private DatabaseReference dbKorisnici;

    private FirebaseFunctions mFunction = FirebaseFunctions.getInstance();


    private HomeViewModel homeViewModel;
    private Button mSoloIgra;
    private Button mIgraProtivPrijatelja;
    private TextView mBrojBodovaUkupni;
    private Dialog upisiInfoDialog;

    private KvizomatApp app;

    private FirebaseUser mTrenutniUser;
    private Korisnik mTrenutniKorisnik;
    private String korisnikKey;
    private DecimalFormat decimalFormat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        app = (KvizomatApp) getContext().getApplicationContext();

//        mRefPitanja = firebaseDatabase.getReference("pitanja");
//        dbKorisnici = firebaseDatabase.getReference("korisniciOnline");

        // Set MainActivity main buttons upon entering
        mSoloIgra = root.findViewById(R.id.last_man_button);
        mIgraProtivPrijatelja = root.findViewById(R.id.friendly_quitz_button);
        mBrojBodovaUkupni = root.findViewById(R.id.ukupan_br_bodova);


//        mFunction.getHttpsCallable("sendNotification")
//                .call(mFirebaseUser.getProviderId())
//                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
//            @Override
//            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
//                Log.d("Test", "Testic");
//            }
//        }).isComplete();

        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);;

        // Database listener
        mListaPitanja = new ArrayList<>();

//        mRefPitanja.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    Pitanje pitanje = ds.getValue(Pitanje.class);
//                    mListaPitanja.add(pitanje);
//                }
//                setOnClick();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        findKorisnik();
        setOnClick();

        return root;
    }

    public Task<HttpsCallableResult> getCloudPitanja() {
        final boolean[] done = {false};
            Task<HttpsCallableResult> task = mFunction.getHttpsCallable("nasumicnaPitanja ")
                    .call();
            return task;
    }

    public void setOnClick() {
        mSoloIgra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent pitanjeActivity = new Intent(getActivity(), KvizActivity.class);
                final ArrayList<Pitanje> pitanja = new ArrayList<>();
                if (NetworkConnection.hasConnection(getContext())) {

                    Task<HttpsCallableResult> task = getCloudPitanja();

                    task.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                        @Override
                        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                            ArrayList<Map<String, Object>> o = (ArrayList<Map<String, Object>>) task.getResult().getData();
                            for (Map<String, Object> ob : o) {
                                pitanja.add(new Pitanje(ob));
                                Log.d("Cloud fun", String.valueOf(pitanja.size()));
                            }
                            pitanjeActivity.putExtra("pitanja", new KvizInformacije(pitanja));
                            pitanjeActivity.putExtra("korisnik", mTrenutniKorisnik);
                            pitanjeActivity.putExtra("korisnikKey", korisnikKey);
                            startActivity(pitanjeActivity);
                            Toast.makeText(getActivity(), "Idemo na pitanje!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (!NetworkConnection.hasConnection(getContext()))
                        mListaPitanja = app.getListaPitanja();
                    randomPitanja(BROJ_PITANJA_PO_KATEGORIJI, pitanja);
                    pitanjeActivity.putExtra("pitanja", new KvizInformacije(pitanja));
                    pitanjeActivity.putExtra("korisnik", mTrenutniKorisnik);
                    pitanjeActivity.putExtra("korisnikKey", korisnikKey);
                    startActivity(pitanjeActivity);
                    Toast.makeText(getActivity(), "Idemo na pitanje!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIgraProtivPrijatelja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog uskoro = new Dialog(getContext());
                uskoro.setContentView(R.layout.uskoro_stize);
                uskoro.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                uskoro.show();

                Intent postaviPitanjeActivity = new Intent(getActivity(), MultiplayerKviz.class);
                startActivity(postaviPitanjeActivity);
                Toast.makeText(getActivity(), "Iskušaj se protiv svojih prijatelja!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTrenutniUser == null) {
            mTrenutniUser = app.getTrenutniUser();
            if (mTrenutniUser != null)
                findKorisnik();
        }
    }

    private void findKorisnik() {
        mTrenutniKorisnik = app.getTrenutniKorisnik();
        if (mTrenutniKorisnik == null) {
            app.setKorisnik(new FirebaseKorisnikCallback() {
                @Override
                public void onCallback(Korisnik korisnik) {
                    mTrenutniKorisnik = korisnik;
                    mTrenutniKorisnik.setOnline(true);
                    float bodovi = mTrenutniKorisnik.getBodovi();
                    mBrojBodovaUkupni.setText("Tvoji bodovi: " + decimalFormat.format(bodovi));
                }
            });
        } else {
            float bodovi = mTrenutniKorisnik.getBodovi();
            mBrojBodovaUkupni.setText("Tvoji bodovi: " + decimalFormat.format(bodovi));
        }
    }

    private ArrayList<Pitanje> randomPitanja (int brojPitanjaPoKat, ArrayList<Pitanje> pitanjaRez) {
        Random random = new Random();
        ArrayList<Pitanje> raz1 = new ArrayList<>();
        ArrayList<Pitanje> raz2 = new ArrayList<>();
        ArrayList<Pitanje> raz3 = new ArrayList<>();
        ArrayList<Pitanje> raz4 = new ArrayList<>();
        ArrayList<ArrayList<Pitanje>> kat = new ArrayList<>();
        kat.add(raz1);kat.add(raz2);kat.add(raz3); kat.add(raz4);

        for (Pitanje p: mListaPitanja) {
            switch (p.getTezinaPitanja()) {
                case 1:
                    raz1.add(p);
                    break;
                case 2:
                    raz2.add(p);
                    break;
                case 3:
                    raz3.add(p);
                    break;
                case 4:
                    raz4.add(p);
                    break;
                default:
                    raz4.add(p);
                    break;
            }
        }
        for (int i = 0; i < kat.size(); i++) {
            for (int j = 0; j < brojPitanjaPoKat; j++) {
                int id = random.nextInt(kat.get(i).size());
                while (!provjeriDodajPitanje(pitanjaRez, kat.get(i).get(id))){
                    id = random.nextInt(kat.get(i).size());
                }
                pitanjaRez.add(kat.get(i).get(id));
            }
        }
        return pitanjaRez;
    }
    
    private boolean provjeriDodajPitanje(ArrayList<Pitanje> pitanjaRez, Pitanje trPit) {
        if (!pitanjaRez.contains(trPit)) {
            return true;
        }
        return false;
    }
}
