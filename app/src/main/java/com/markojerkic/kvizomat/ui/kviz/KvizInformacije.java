package com.markojerkic.kvizomat.ui.kviz;

import com.markojerkic.kvizomat.ui.kviz.multiplayer.Kviz;

import java.io.Serializable;
import java.util.ArrayList;

public class KvizInformacije implements Serializable {
    private ArrayList<Pitanje> mListaPitanja;
    private int iterator = 0;
    private boolean online;
    private String key;
    private Kviz kviz;

    public KvizInformacije(ArrayList<Pitanje> pitanja) {
        this.mListaPitanja = pitanja;
        this.online = false;
        this.key = "-1";
    }

    public KvizInformacije(Kviz kviz, boolean online, String key) {
        this.kviz = kviz;
        this.mListaPitanja = kviz.getPitanja();
        this.online = online;
        this.key = key;
    }

    public KvizInformacije() {}

    public ArrayList<Pitanje> getListaPitanja() {
        return mListaPitanja;
    }

    public Pitanje getNext() {
        Pitanje rez = mListaPitanja.get(iterator);
        iterator++;
        return rez;
    }

    public int brojPitanja() {return mListaPitanja.size();}

    public int getIterator() {return iterator;}

    public boolean isOnline() {
        return online;
    }

    public String getKey() {
        return key;
    }

    public Kviz getKviz() {
        return kviz;
    }

    public void setKviz(Kviz kviz) {
        this.kviz = kviz;
    }
}
