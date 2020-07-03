package com.markojerkic.kvizomat.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.markojerkic.kvizomat.FirebaseKorisnikCallback;
import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.ProvjeraVeze;
import com.markojerkic.kvizomat.ui.kviz.Bodovi;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.markojerkic.kvizomat.ui.kviz.KvizActivity;
import com.markojerkic.kvizomat.ui.kviz.KvizInformacije;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;
import com.markojerkic.kvizomat.ui.kviz.RezultatKvizaActivity;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.Kviz;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.ListaKvizovaCallback;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.MultiplayerKviz;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final int BROJ_PITANJA_PO_KATEGORIJI = 3;
    private static final int BROJ_KVIZOVA_PRIKAZATI = 10;
    private ArrayList<Pitanje> mListaPitanja;

    private HomeViewModel homeViewModel;
    private Button mSoloIgra;
    private Button mIgraProtivPrijatelja;
    private TextView mBrojBodovaUkupni;
    private LinearLayout mHomeLinearLayout;

    private KvizomatApp app;

    private FirebaseUser mTrenutniUser;
    private Korisnik mTrenutniKorisnik;
    private String korisnikKey;
    private DecimalFormat decimalFormat;
    private ArrayList<Kviz> listaKvizova;
    private ArrayList<View> kvizoviView;
    private Kviz zadnjiKviz;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        app = (KvizomatApp) getContext().getApplicationContext();

        // Set MainActivity main buttons upon entering
        mSoloIgra = root.findViewById(R.id.last_man_button);
        mIgraProtivPrijatelja = root.findViewById(R.id.friendly_quitz_button);
        mBrojBodovaUkupni = root.findViewById(R.id.ukupan_br_bodova);
        mHomeLinearLayout = root.findViewById(R.id.home_linear_layout);

        decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        mListaPitanja = new ArrayList<>();
        setOnClick();

        return root;
    }



    public void setOnClick() {
        mSoloIgra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bodovi.pokreniSoloKviz(getContext(), mTrenutniKorisnik, app);
            }
        });

        mIgraProtivPrijatelja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        } else {
            getNoviKvizovi();
        }
    }

    private void findKorisnik() {
        app.getTrenutniKorisnik(new FirebaseKorisnikCallback() {
            @Override
            public void onCallback(Korisnik korisnik) {
                mTrenutniKorisnik = korisnik;
                getKvizovi();
            }
        });
        if (mTrenutniKorisnik == null) {
            app.setKorisnik(new FirebaseKorisnikCallback() {
                @Override
                public void onCallback(Korisnik korisnik) {
                    mTrenutniKorisnik = korisnik;
                    mTrenutniKorisnik.setOnline(true);
                    float bodovi = mTrenutniKorisnik.getBodovi();
                    mBrojBodovaUkupni.setText("Tvoji bodovi: " + decimalFormat.format(bodovi));
                }
            }, false);
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
        return !pitanjaRez.contains(trPit);
    }
    private void getNoviKvizovi() {
        app.napraviNovuListuKvizova(new ListaKvizovaCallback() {
            @Override
            public void onListaGotova(ArrayList<Kviz> lKviz) {
                listaKvizova = lKviz;
                Log.d("kvizovi", listaKvizova.toString());
                mHomeLinearLayout.removeAllViews();
                prikaziKvizove(BROJ_KVIZOVA_PRIKAZATI);
            }
        });
    }

    private void getKvizovi() {
        app.napraviListuKvizova(new ListaKvizovaCallback() {
            @Override
            public void onListaGotova(ArrayList<Kviz> lKviz) {
                listaKvizova = lKviz;
                Log.d("kvizovi", listaKvizova.toString());
                prikaziKvizove(BROJ_KVIZOVA_PRIKAZATI);
            }
        });
    }

    private void prikaziKvizove(int brojKvizovaPrikazati) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        kvizoviView = new ArrayList<>();
        if (listaKvizova.size() >= 1) {
            View onlineKvizoviNaslov = inflater.inflate(R.layout.naslov_online_kvizovi, null);
            mHomeLinearLayout.addView(onlineKvizoviNaslov);
            int br = Math.min(listaKvizova.size(), brojKvizovaPrikazati);
            for (int i = listaKvizova.size()-1; i > listaKvizova.size() - br; i--) {
                inflateOnlineKviz(kvizoviView, listaKvizova.get(i), inflater);
            }
        }
    }

    private void inflateOnlineKviz(ArrayList<View> listaKvizView, Kviz kviz, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.online_kviz_view, null);
        Korisnik izaz = app.findKorisnik(kviz.getIzazivacUid());

        TextView ime = v.findViewById(R.id.ime_korisnika);
        try {
            ime.setText(izaz.getIme());

            TextView bodovi = v.findViewById(R.id.bodovi_online_tekst);
            bodovi.setText("Bodove treba zaraditi :)");

            TextView rijeseno = v.findViewById(R.id.zapoceo_kviz_tekst);

            ImageView slika = v.findViewById(R.id.slika_korisnika);
            if (ProvjeraVeze.provjeriSlika(izaz, getContext()))
                Picasso.get().load(izaz.getUri()).into(slika);

            listaKvizView.add(v);

            setOnClickOnlineKviz(v, kviz);

            if (kviz.getOdgovoriTrKor() != null) {
                bodovi.setText("Vaši bodovi: " + decimalFormat.format(kviz.getBodTr()));
            }

            if (kviz.getOdgovoriIzazivac() != null && kviz.getOdgovoriTrKor() != null) {
                if (kviz.getBodTr() > kviz.getBodIza())
                    rijeseno.setText("Pobijedili ste :)");
                else
                    rijeseno.setText("Izgubili ste :(");
            } else if (kviz.getOdgovoriTrKor() != null && kviz.getOdgovoriIzazivac() == null)
                rijeseno.setText("Protivnik još nije riješio kviz");
            else if (kviz.getOdgovoriTrKor() == null && kviz.getOdgovoriIzazivac() != null)
                rijeseno.setText("Niste riješili kviz");
            else
                rijeseno.setText("Nitko od vas dvoje nije riješio kviz");

            mHomeLinearLayout.addView(v);
        } catch (Exception e) {
            if (izaz != null)
                Log.d("Iznimka", izaz.toString());
        }

    }

    private void setOnClickOnlineKviz(View v, final Kviz kviz) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kviz.getOdgovoriTrKor() == null) {
                    final Intent pitanjeActivity = new Intent(getActivity(), KvizActivity.class);

                    Toast.makeText(getActivity(), R.string.postavljanje_pitanja, Toast.LENGTH_SHORT).show();
                    if (NetworkConnection.hasConnection(getContext())) {
                        zadnjiKviz = kviz;
                        pitanjeActivity.putExtra("pitanja", new KvizInformacije(kviz, true, kviz.getKey()));
                        pitanjeActivity.putExtra("korisnik", mTrenutniKorisnik);
                        pitanjeActivity.putExtra("korisnikKey", korisnikKey);
                        pitanjeActivity.putExtra("kviz", kviz);
                        pitanjeActivity.putExtra("online", true);
                        startActivity(pitanjeActivity);
                        Toast.makeText(getActivity(), R.string.ulazak_u_igru_toast, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.ulazak_u_igru_toast, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Već ste riješili kviz", Toast.LENGTH_SHORT).show();
                    Intent rez = new Intent(getActivity(), RezultatKvizaActivity.class);
                    rez.putExtra("pitanja", kviz.getPitanja());
                    rez.putExtra("odgovoriKorisnika", kviz.getOdgovoriTrKor());
                    rez.putExtra("odgovoriIzaz", kviz.getOdgovoriIzazivac());
                    rez.putExtra("online", true);
                    startActivity(rez);
                }
            }
        });
    }
}
