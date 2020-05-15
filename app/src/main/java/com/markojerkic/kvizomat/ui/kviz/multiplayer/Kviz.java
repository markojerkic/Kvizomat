package com.markojerkic.kvizomat.ui.kviz.multiplayer;

import com.google.firebase.database.DataSnapshot;
import com.markojerkic.kvizomat.ui.kviz.Bodovi;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Kviz implements Serializable {
    private ArrayList<Pitanje> pitanja;
    private String trKorisnikUid, izazivacUid;
    private ArrayList<Integer> odgovoriTrKor, odgKor1;
    private ArrayList<Integer> odgovoriIzazivac, odgKor2;
    private String key;
    private boolean zamjenjeno = false;
    private float bodTr, bodIza;

    public Kviz() {}

    public Kviz(DataSnapshot ds, String trKorisnikKey) {
        this.trKorisnikUid = (String)  ds.child("kor1").getValue();
        this.izazivacUid = (String)  ds.child("kor2").getValue();

        this.pitanja = new ArrayList<>();

        for(HashMap<String, Object> map: (ArrayList<HashMap<String, Object>>) ds.child("kvizPitanja").child("result").getValue()) {
            int tocno;
            if (map.containsKey("tocanOdgovorInt"))
                tocno = ((Long) map.get("tocanOdgovorInt")).intValue();
            else
                tocno = ((Long) map.get("tocanOdgovor")).intValue();
            Pitanje p = new Pitanje((String) map.get("pitanje"), (String) map.get("odgovorA"), (String) map.get("odgovorB"),
                    (String) map.get("odgovorC"), (String) map.get("odgovorD"), (String) map.get("razneInformacije"),
                    ((Long) map.get("tezinaPitanja")).intValue(),
                    tocno);
            this.pitanja.add(p);
        }

        if (ds.child("odgovoriKor1").exists()) {
            this.odgKor1 = new ArrayList<>();
            this.odgovoriTrKor = this.odgKor1;
            for (Long l: (ArrayList<Long>) ds.child("odgovoriKor1").getValue()) {
                this.odgovoriTrKor.add(l.intValue());
            }
            assert this.odgovoriTrKor != null;
            this.bodTr = izracunajBodove(this.odgovoriTrKor);
        }
        if (ds.child("odgovoriKor2").exists()) {
            this.odgKor2 = new ArrayList<>();
            this.odgovoriIzazivac = this.odgKor2;
            for (Long l: (ArrayList<Long>) ds.child("odgovoriKor2").getValue()) {
                this.odgovoriIzazivac.add(l.intValue());
            }
            assert this.odgovoriIzazivac != null;
            this.bodIza = izracunajBodove(this.odgovoriIzazivac);
        }

        if (!this.trKorisnikUid.equals(trKorisnikKey)) {
            zamjenjeno = true;
            String t = this.trKorisnikUid;
            this.trKorisnikUid = this.izazivacUid;
            this.izazivacUid = t;

            zamijeniOdgovore();
            zamjeniBodove();
        }
    }

    private void zamjeniBodove() {
        float t = this.bodIza;
        this.bodIza = this.bodTr;
        bodTr = t;
    }

    private void zamijeniOdgovore() {
        ArrayList<Integer> l = this.odgovoriTrKor;
        this.odgovoriTrKor = this.odgovoriIzazivac;
        this.odgovoriIzazivac = l;
    }

    public Kviz (String trKorisnikUid, String izazivacUid, ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
        this.trKorisnikUid = trKorisnikUid;
        this.izazivacUid = izazivacUid;
    }

    public Kviz (String trKorisnikUid, ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
        this.trKorisnikUid = trKorisnikUid;
        this.key = "-1";
    }

    public ArrayList<Pitanje> getPitanja() {
        return this.pitanja;
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

    public void setOdgovori(ArrayList<Integer> odgovori) {
        this.odgovoriTrKor = odgovori;

    }

    public ArrayList<Integer> getOdgovoriTrKor() {
        return odgovoriTrKor;
    }

    public ArrayList<Integer> getOdgovoriIzazivac() {
        return odgovoriIzazivac;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (!zamjenjeno) {
            map.put("kor1", getTrKorisnikUid());
            map.put("kor2", getIzazivacUid());
        } else {
            map.put("kor2", getTrKorisnikUid());
            map.put("kor1", getIzazivacUid());
        }
        HashMap<String, Object> t = new HashMap<>();
        t.put("result", this.getPitanja());
        map.put("kvizPitanja", t);
        if (this.odgovoriIzazivac != null && !zamjenjeno)
            map.put("odgovoriKor2", this.odgovoriIzazivac);
        else if (this.odgovoriTrKor != null && zamjenjeno)
            map.put("odgovoriKor2", this.odgovoriTrKor);
        if (this.odgovoriTrKor != null && !zamjenjeno)
            map.put("odgovoriKor1", this.odgovoriTrKor);
        else if (this.odgovoriIzazivac != null && zamjenjeno)
            map.put("odgovoriKor1", this.odgovoriIzazivac);

        return map;
    }

    private float izracunajBodove(ArrayList<Integer> odgovori) {
        float rez = Bodovi.izracunajBodove(pitanja, odgovori);
        return rez;
    }

    public float getBodTr() {
        return bodTr;
    }

    public float getBodIza() {
        return bodIza;
    }
}
