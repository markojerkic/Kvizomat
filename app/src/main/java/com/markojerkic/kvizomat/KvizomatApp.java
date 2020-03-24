package com.markojerkic.kvizomat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class KvizomatApp extends Application {

    private static final int RC_SIGN_IN = 555;
    private DatabaseReference korisniciReference;
    private DatabaseReference pitanjaReference;
    private DatabaseReference tokenReference;
    private ArrayList<Pitanje> listaPitanja;

    public static boolean spremno = false;

    private Korisnik trenutniKorisnik;
    private FirebaseUser trenutniUser;
    private ArrayList<Korisnik> listaKorisnika;
    private ArrayList<Korisnik> listaPrijatelja;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        korisniciReference = FirebaseDatabase.getInstance().getReference("korisniciOnline");
        pitanjaReference = FirebaseDatabase.getInstance().getReference("pitanja");
        tokenReference = FirebaseDatabase.getInstance().getReference("korisniciToken");
        korisniciReference.keepSynced(true);
        pitanjaReference.keepSynced(true);
        tokenReference.keepSynced(true);

        if (trenutniUser == null && FirebaseAuth.getInstance().getCurrentUser() != null)
            setTrenutniUser();
        else if (trenutniUser != null) {
            setKorisnik(new FirebaseKorisnikCallback() {
                @Override
                public void onCallback(Korisnik korisnik) {
                    trenutniKorisnik = korisnik;
                }
            });
        }

        if (!NetworkConnection.hasConnection(this))
            setListaPitanja();

    }

    public void setBodovi(float bodovi) {
        if (trenutniKorisnik != null) {
            korisniciReference.child(trenutniKorisnik.getUid()).child("bodovi").setValue(bodovi);
        }
    }

    public void setTrenutniUser() {
        trenutniUser = FirebaseAuth.getInstance().getCurrentUser();
        setKorisnik(new FirebaseKorisnikCallback() {
            @Override
            public void onCallback(Korisnik korisnik) {
                trenutniKorisnik = korisnik;
            }
        });
    }

    public void setTrenutniKorisnik(Korisnik trenutniKorisnik) {
        this.trenutniKorisnik = trenutniKorisnik;

        korisniciReference.child(trenutniKorisnik.getUid()).setValue(trenutniKorisnik);
    }

    public ArrayList<Korisnik> getListaKorisnika() {
        return listaKorisnika;
    }

    public ArrayList<Korisnik> getListaPrijatelja() {
        return listaPrijatelja;
    }

    public DatabaseReference getKorisniciReference() {
        return korisniciReference;
    }

    public FirebaseUser getTrenutniUser() {

        while (trenutniUser == null) {
            Log.d("Korisnik", "trenutni user još nije spreman");
        }
        Log.d("Korisnik", "trenutni user je spreman");
        return trenutniUser;
    }

    public ArrayList<Pitanje> getListaPitanja() {
        return listaPitanja;
    }

    public void setListaPitanja() {
        listaPitanja = new ArrayList<>();
        pitanjaReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Pitanje pitanje = ds.getValue(Pitanje.class);
                    listaPitanja.add(pitanje);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Korisnik getTrenutniKorisnik() {
        return trenutniKorisnik;
    }

    public void napraviListuPrijatelja() {
        listaPrijatelja = new ArrayList<>();
        for (Korisnik kP: listaKorisnika) {
            Log.d("Korisnik", kP.getIme());
            if (trenutniKorisnik.getPrijatelji().contains(kP.getUid())) {
                listaPrijatelja.add(kP);
            }
        }
        Collections.sort(listaPrijatelja, new Comparator<Korisnik>() {
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

//        rangListaPrijateljaAdapter = new RangListaPrijateljaAdapter(listaPrijatelja,
//                getContext(), trKorisnik);
//        if (!svi) {
//            listaView.setAdapter(rangListaPrijateljaAdapter);
//            rangListaPrijateljaAdapter.notifyDataSetChanged();
//        }
    }

    public void setListaKorisnika() {
        listaKorisnika = new ArrayList<>();
        korisniciReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik korisnikChild = ds.getValue(Korisnik.class);
                    if (!korisnikChild.getUid().equals(trenutniKorisnik.getUid())) {
                        listaKorisnika.add(korisnikChild);
                    } else {

                    }
                }
                napraviListuPrijatelja();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setKorisnik(final FirebaseKorisnikCallback callback) {
        korisniciReference.child(trenutniUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Korisnik k = dataSnapshot.getValue(Korisnik.class);
                Log.d("Test s korisnikom", k.getIme());
                trenutniKorisnik = k;

                korisniciReference.child(trenutniKorisnik.getUid()).child("online").setValue(true);
                setListaKorisnika();
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        String token = task.getResult().getToken();

                        tokenReference.child(trenutniKorisnik.getUid()).setValue(token);
                    }
                });

                korisniciReference.child(trenutniUser.getUid()).child("online").setValue(true);
                korisniciReference.child(trenutniUser.getUid()).child("online").onDisconnect().setValue(false);

                callback.onCallback(trenutniKorisnik);
                spremno = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getTokenReference() {
        return tokenReference;
    }
}
