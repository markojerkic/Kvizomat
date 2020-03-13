package com.markojerkic.kvizomat.ui.kviz;

import java.io.Serializable;

public class Korisnik implements Serializable {
    private String ime, email, uid, uri;

    public Korisnik() {}

    public Korisnik(String ime, String email, String uri, String uid) {
        this.ime = ime;
        this.email = email;
        this.uri = uri;
        this.uid = uid;
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
}
