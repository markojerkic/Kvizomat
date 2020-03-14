package com.markojerkic.kvizomat.ui.kviz;

import java.io.Serializable;
import java.util.ArrayList;

public class Korisnik implements Serializable {
    private String ime, email, uid, uri;
    private ArrayList<String> prijatelji;
    private float bodovi;

    public Korisnik() {}

    public Korisnik(String ime, String email, String uri, String uid,
                    ArrayList<String> prijatelji, float bodovi) {
        this.ime = ime;
        this.email = email;
        this.uri = uri;
        this.uid = uid;
        this.prijatelji = prijatelji;
        this.bodovi = bodovi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public float getBodovi() {
        return bodovi;
    }

    public void setBodovi(float bodovi) {
        this.bodovi = bodovi;
    }

    public ArrayList<String> getPrijatelji() {
        return prijatelji;
    }

    public void setPrijatelji(ArrayList<String> prijatelji) {
        this.prijatelji = prijatelji;
    }
}
