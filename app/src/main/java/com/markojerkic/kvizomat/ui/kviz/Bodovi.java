package com.markojerkic.kvizomat.ui.kviz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.markojerkic.kvizomat.KvizomatApp;
import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.Kviz;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.ListaKvizovaCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bodovi {

    public static float izracunajBodove(ArrayList<Pitanje> pitanja, ArrayList<Integer> odgovori) {
        float rez = 0;
        for (int i = 0; i < 4; i++) {
            int br = 0;
            for (int j = 0; j < 3; j++) {
                if (pitanja.get(i +j).getTocanOdgovor() == odgovori.get(i + j))
                    br ++;
            }
            rez += ((float) br /  4f)*((float) i+1f) * 0.7f;
        }
        return rez;
    }

    public static void stvoriOnlineIntent(final Korisnik trenutniKorisnik, final Korisnik protivnik,
                                            final KvizomatApp app, final Context context) {
        FirebaseFunctions functions = app.getmFunction();

        Map<String, Object> data = new HashMap<>();
        data.put("protivnik", protivnik.getUid());
        data.put("mojUid", trenutniKorisnik.getUid());

        functions.getHttpsCallable("sendSpecific").call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                Toast.makeText(context, "Zahtjev poslan, spremamo vam pitanja", Toast.LENGTH_SHORT).show();
                String key = (String) task.getResult().getData();
                izazovi(key, trenutniKorisnik, protivnik, app, context);
                Log.d("Izazov", key);
            }
        });

    }

    private static void izazovi(String key, final Korisnik trenutniKorisnik, final Korisnik protivnik,
                                KvizomatApp app, final Context context) {
        app.pronadiKviz(key, new ListaKvizovaCallback() {
            @Override
            public void onListaGotova(ArrayList<Kviz> listaKvizova) {
                Intent intent = new Intent(context, KvizActivity.class);
                Kviz k = listaKvizova.get(0);
                intent.putExtra("kviz", k);
                intent.putExtra("korisnik", trenutniKorisnik);
                intent.putExtra("online", true);
                context.startActivity(intent);

            }
        });
    }

    public static void otvoriIzazov(Context context, Korisnik trenutniKorisnik, Korisnik protivnik,
                               KvizomatApp app, Kviz kviz) {
        Intent intent = new Intent(context, KvizActivity.class);
        intent.putExtra("kviz", kviz);
        intent.putExtra("korisnik", trenutniKorisnik);
        intent.putExtra("online", true);
        context.startActivity(intent);

    }

    public static void pokreniSoloKviz(final Context context, final Korisnik mTrenutniKorisnik,
                                       KvizomatApp app) {
        final Intent pitanjeActivity = new Intent(context, KvizActivity.class);
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        Toast.makeText(context, R.string.postavljanje_pitanja, Toast.LENGTH_SHORT).show();
        if (NetworkConnection.hasConnection(context)) {

            Task<HttpsCallableResult> task = getCloudPitanja(app);

            task.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    ArrayList<Map<String, Object>> o = (ArrayList<Map<String, Object>>) task.getResult().getData();
                    final ArrayList<Pitanje> pitanjaOnline = new ArrayList<>();
                    for (Map<String, Object> ob : o) {
                        pitanjaOnline.add(new Pitanje(ob));
                        Log.d("Cloud fun", String.valueOf(pitanjaOnline.size()));
                    }
                    pokreniIntentSolo(pitanjeActivity, context, pitanjaOnline, mTrenutniKorisnik);
                }
            });
        } else {
            if (!NetworkConnection.hasConnection(context))
                pitanja = app.getListaPitanja();
            randomPitanja(3, pitanja, app.getListaPitanja());
            pokreniIntentSolo(pitanjeActivity, context, pitanja, mTrenutniKorisnik);
        }

    }

    private static void pokreniIntentSolo(Intent pitanjeActivity, Context context,
                                          ArrayList<Pitanje> pitanja, Korisnik mTrenutniKorisnik) {
        pitanjeActivity.putExtra("pitanja", new KvizInformacije(pitanja));
        pitanjeActivity.putExtra("korisnik", mTrenutniKorisnik);
        pitanjeActivity.putExtra("korisnikKey", mTrenutniKorisnik.getUid());
        pitanjeActivity.putExtra("kviz", new Kviz(mTrenutniKorisnik.getUid(), pitanja));
        pitanjeActivity.putExtra("online", false);
        context.startActivity(pitanjeActivity);
        Toast.makeText(context, R.string.ulazak_u_igru_toast, Toast.LENGTH_SHORT).show();

    }

    private static Task<HttpsCallableResult> getCloudPitanja(KvizomatApp app) {
        FirebaseFunctions mFunction = app.getmFunction();
        final boolean[] done = {false};
        Task<HttpsCallableResult> task = mFunction.getHttpsCallable("nasumicnaPitanja ")
                .call();
        return task;
    }

    private static ArrayList<Pitanje> randomPitanja (int brojPitanjaPoKat, ArrayList<Pitanje> pitanjaRez,
                                                     ArrayList<Pitanje> mListaPitanja) {
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

    private static boolean provjeriDodajPitanje(ArrayList<Pitanje> pitanjaRez, Pitanje trPit) {
        return !pitanjaRez.contains(trPit);
    }
}
