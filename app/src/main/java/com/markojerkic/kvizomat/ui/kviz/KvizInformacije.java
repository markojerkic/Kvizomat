package com.markojerkic.kvizomat.ui.kviz;

import java.io.Serializable;
import java.util.ArrayList;

public class KvizInformacije implements Serializable {
    private ArrayList<Pitanje> mListaPitanja;
    private int iterator = 0;

    public KvizInformacije(ArrayList<Pitanje> pitanja) {
        this.mListaPitanja = pitanja;
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
}
