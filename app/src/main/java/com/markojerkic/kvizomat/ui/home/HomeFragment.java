package com.markojerkic.kvizomat.ui.home;

import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.PostaviPitanje;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.markojerkic.kvizomat.ui.kviz.KvizActivity;
import com.markojerkic.kvizomat.ui.kviz.KvizInformacije;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final int BROJ_PITANJA_PO_KATEGORIJI = 3;
    private ArrayList<Pitanje> mListaPitanja;

    private DatabaseReference mRefPitanja = FirebaseDatabase.getInstance().getReference("pitanja");
    private DatabaseReference dbKorisnici = FirebaseDatabase.getInstance().getReference("korisnici");
    private FirebaseFunctions mFunction = FirebaseFunctions.getInstance();

    private HomeViewModel homeViewModel;
    private Button mLastManButton;
    private Button mCategoryButton;
    private TextView mBrojBodovaUkupni;
    private Dialog upisiInfoDialog;

    private FirebaseUser mFirebaseUser;
    private Korisnik mKorisnik;
    private String korisnikKey;
    private DecimalFormat decimalFormat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        // Set MainActivity main buttons upon entering
        mLastManButton = root.findViewById(R.id.last_man_button);
        mCategoryButton = root.findViewById(R.id.friendly_quitz_button);
        mBrojBodovaUkupni = root.findViewById(R.id.ukupan_br_bodova);

//        upisiInfoDialog = new Dialog(getContext());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            Log.d("Korisnik", mFirebaseUser.getDisplayName());

            findKorisnik();
        }
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

        mRefPitanja.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Pitanje pitanje = ds.getValue(Pitanje.class);
                    mListaPitanja.add(pitanje);
                }
                setOnClick();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    public Task<HttpsCallableResult> getCloudPitanja() {
        final boolean[] done = {false};
            Task<HttpsCallableResult> task = mFunction.getHttpsCallable("nasumicnaPitanja ")
                    .call();
            return task;
    }

    public void setOnClick() {
        mLastManButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent pitanjeActivity = new Intent(getActivity(), KvizActivity.class);
                final ArrayList<Pitanje> pitanja = new ArrayList<>();
                Task<HttpsCallableResult> task = getCloudPitanja();

                task.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        ArrayList<Map<String, Object>> o =  (ArrayList<Map<String, Object>>) task.getResult().getData();
                        for (Map<String, Object> ob: o) {
                            pitanja.add(new Pitanje(ob));
                            Log.d("Cloud fun", String.valueOf(pitanja.size()));
                        }
                        pitanjeActivity.putExtra("pitanja", new KvizInformacije(pitanja));
                        pitanjeActivity.putExtra("korisnik", mKorisnik);
                        pitanjeActivity.putExtra("korisnikKey", korisnikKey);
                        startActivity(pitanjeActivity);
                        Toast.makeText(getActivity(), "Idemo na pitanje!!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postaviPitanjeActivity = new Intent(getActivity(), PostaviPitanje.class);
                startActivity(postaviPitanjeActivity);
                Toast.makeText(getActivity(), "Ajmo napraviti par pitanja!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseUser != null) {
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            findKorisnik();
        }
    }

    private void findKorisnik() {
        dbKorisnici.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik k = ds.getValue(Korisnik.class);
                    if (k.getUid().equals(mFirebaseUser.getUid())) {
                        mKorisnik = k;
                        korisnikKey = ds.getKey();
                        mBrojBodovaUkupni.setText("Tvoji bodovi: " + decimalFormat.format(mKorisnik.getBodovi()));

                    }
//                    if (mKorisnik.getIme() == null && mFirebaseUser.getUid() == null) {
//
//                        upisiInfoDialog.setContentView(R.layout.upisi_informacije);
//                        final EditText editText = upisiInfoDialog.findViewById(R.id.upisi_ime);
//                        Button upisiTipka = upisiInfoDialog.findViewById(R.id.upisi_ime_tipka);
//
//                        upisiTipka.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (editText.getText().toString() == null) {
//                                    Toast.makeText(getContext(),
//                                            "Upisite informacije", Toast.LENGTH_LONG).show();
//                                } else {
//                                    mKorisnik.setIme(editText.getText().toString());
//                                    dbKorisnici.child(korisnikKey).setValue(mKorisnik);
//
//                                }
//                                upisiInfoDialog.cancel();
//                            }
//                        });
//                        upisiInfoDialog.show();
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Pitanje> randomPitanja (int brojPitanja) {
        Random random = new Random();
        ArrayList<Pitanje> pitanjaRez = new ArrayList<>();
        ArrayList<Pitanje> raz1 = new ArrayList<>();
        ArrayList<Pitanje> raz2 = new ArrayList<>();
        ArrayList<Pitanje> raz3 = new ArrayList<>();
        ArrayList<Pitanje> raz4 = new ArrayList<>();
        int razina = 1;
        int br = 0;
        for (Pitanje p: mListaPitanja) {
            switch (p.getTezinaPitanja()) {
                case 1:
                    raz1.add(p); break;
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
                    raz1.add(p);
                    break;
            }
        }
        while (pitanjaRez.size() < brojPitanja*4) {
            int idPitanja;
            Pitanje trPit;
            switch (razina) {
                case 1:
                    idPitanja = random.nextInt(raz1.size());
                    trPit = raz1.get(idPitanja);
                    if (provjeriDodajPitanje(pitanjaRez, trPit)) {
                        br++;
                        if (br == BROJ_PITANJA_PO_KATEGORIJI)
                            razina++;
                    br %= BROJ_PITANJA_PO_KATEGORIJI;                        
                    }
                    break;
                case 2:
                    idPitanja = random.nextInt(raz2.size());
                    trPit = raz2.get(idPitanja);
                    if (provjeriDodajPitanje(pitanjaRez, trPit)) {
                        br++;
                        if (br == BROJ_PITANJA_PO_KATEGORIJI)
                            razina++;
                        br %= BROJ_PITANJA_PO_KATEGORIJI;
                    }
                    break;
                case 3:
                    idPitanja = random.nextInt(raz3.size());
                    trPit = raz3.get(idPitanja);
                    if (provjeriDodajPitanje(pitanjaRez, trPit)) {
                        br++;
                        if (br == BROJ_PITANJA_PO_KATEGORIJI)
                            razina++;
                        br %= BROJ_PITANJA_PO_KATEGORIJI;
                    }
                    break;
                case 4:
                    idPitanja = random.nextInt(raz4.size());
                    trPit = raz4.get(idPitanja);
                    if (provjeriDodajPitanje(pitanjaRez, trPit)) {
                        br++;
                        if (br == BROJ_PITANJA_PO_KATEGORIJI)
                            razina++;
                        br %= BROJ_PITANJA_PO_KATEGORIJI;
                    }
                    break;
                default:
                    break;
            }
        }
        return pitanjaRez;
    }
    
    private boolean provjeriDodajPitanje(ArrayList<Pitanje> pitanjaRez, Pitanje trPit) {
        if (!pitanjaRez.contains(trPit)) {
            pitanjaRez.add(trPit);
            return true;
        }
        return false;
    }
}
