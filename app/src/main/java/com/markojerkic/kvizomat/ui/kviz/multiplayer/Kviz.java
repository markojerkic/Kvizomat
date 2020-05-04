package com.markojerkic.kvizomat.ui.kviz.multiplayer;

import com.google.firebase.database.DataSnapshot;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.io.Serializable;
import java.util.ArrayList;

public class Kviz implements Serializable {
    private ArrayList<Pitanje> pitanja;
    private String trKorisnikUid, izazivacUid;

    public Kviz() {}

    public Kviz(DataSnapshot ds, String trKorisnikKey) {
        this.trKorisnikUid = (String)  ds.child("kor1").getValue();
        this.izazivacUid = (String)  ds.child("kor2").getValue();
        this.pitanja = (ArrayList<Pitanje>) ds.child("kvizPitanja").child("result").getValue();

        if (!this.trKorisnikUid.equals(trKorisnikKey)) {

            String t = this.trKorisnikUid;
            this.trKorisnikUid = this.izazivacUid;
            this.izazivacUid = t;
        }
    }

    private void getIzazivac(String korisnikKey) {

    }

    public Kviz (String trKorisnikUid, String izazivacUid, ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
        this.trKorisnikUid = trKorisnikUid;
        this.izazivacUid = izazivacUid;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public String getTrKorisnikUid() {
        return trKorisnikUid;
    }

    public void setTrKorisnikUid(String trKorisnikUid) {
        this.trKorisnikUid = trKorisnikUid;
    }

    public String getIzazivacUid() {
        return izazivacUid;
    }

    public void setIzazivacUid(String izazivacUid) {
        this.izazivacUid = izazivacUid;
    }
}
