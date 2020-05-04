package com.markojerkic.kvizomat;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

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
import com.markojerkic.kvizomat.ui.kviz.multiplayer.Kviz;
import com.markojerkic.kvizomat.ui.kviz.multiplayer.ListaKvizovaCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class KvizomatApp extends Application {

    private static final int RC_SIGN_IN = 555;
    private DatabaseReference korisniciReference;
    private DatabaseReference pitanjaReference;
    private DatabaseReference tokenReference;
    private DatabaseReference kvizoviReference;
    private ArrayList<Pitanje> listaPitanja;
    private FirebaseDatabase database;

    public static boolean spremno = false;

    private Korisnik trenutniKorisnik;
    private FirebaseUser trenutniUser;
    private ArrayList<Korisnik> listaKorisnika;
    private ArrayList<Korisnik> listaPrijatelja;
    private ArrayList<Kviz> listaKvizova;

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        korisniciReference = database.getReference("korisniciOnline");
        pitanjaReference = database.getReference("pitanja");
        tokenReference = database.getReference("korisniciToken");
        kvizoviReference = database.getReference("onlineKvizovi");
        korisniciReference.keepSynced(true);
        pitanjaReference.keepSynced(true);
        tokenReference.keepSynced(true);
        kvizoviReference.keepSynced(true);

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

    public void setOnline(boolean online) {
        korisniciReference.child(trenutniKorisnik.getUid()).child("online").setValue(online);
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

    public void getTrenutniKorisnik(FirebaseKorisnikCallback callback) {
        if (trenutniKorisnik != null) {
            callback.onCallback(trenutniKorisnik);
        } else {
            setKorisnik(callback);
        }
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

    public void setListaPrijatelja(ArrayList<String> prijatelji) {
        trenutniKorisnik.setPrijatelji(prijatelji);
        korisniciReference.child(trenutniKorisnik.getUid()).child("prijatelji").setValue(prijatelji);
    }

    public void updateOnlinePrijatelji(final FirebaseKorisnikCallback korisnikCallback) {
        for (Korisnik prijatelj: listaPrijatelja) {
            korisniciReference.child(prijatelj.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Korisnik k = dataSnapshot.getValue(Korisnik.class);
                    korisnikCallback.onCallback(k);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public Korisnik findKorisnik(String uid) {
        for (Korisnik k: listaKorisnika) {
            if (k.getUid().equals(uid))
                return k;
        }

        Toast.makeText(getApplicationContext(),
                String.format("Korisnik {} nije pronađen", uid), Toast.LENGTH_SHORT).show();
        return null;
    }

    public Korisnik findPrijatelj(String uid) {
        for (Korisnik k: listaPrijatelja) {
            if (k.getUid().equals(uid))
                return k;
        }

        Toast.makeText(getApplicationContext(),
                "Prijatelj nije pronađen", Toast.LENGTH_SHORT).show();
        return null;
    }

    public void setListaKorisnika() {
        korisniciReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaKorisnika = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik korisnikChild = ds.getValue(Korisnik.class);
                    if (!korisnikChild.getUid().equals(trenutniKorisnik.getUid())) {
                        listaKorisnika.add(korisnikChild);
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
        if (trenutniUser != null) {
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
                    napraviListuKvizova(new ListaKvizovaCallback() {
                        @Override
                        public void onListaGotova(ArrayList<Kviz> lk) {
                            listaKvizova = lk;
                            setListaKorisnika();
                        }
                    });
                    spremno = true;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public DatabaseReference getTokenReference() {
        return tokenReference;
    }



    public void napraviListuKvizova(final ListaKvizovaCallback callback) {
        if (listaKvizova != null) {
            callback.onListaGotova(listaKvizova);
        } else {
            final ArrayList<Kviz> kList = new ArrayList<>();
            kvizoviReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (trenutniKorisnik.getOnlineKvizovi().contains(ds.getKey())) {
                            Kviz k = new Kviz(ds, trenutniKorisnik.getUid());
                            kList.add(k);
                        }
                    }
                    callback.onListaGotova(kList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
