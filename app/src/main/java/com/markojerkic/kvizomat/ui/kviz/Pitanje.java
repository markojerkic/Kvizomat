package com.markojerkic.kvizomat.ui.kviz;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

public class Pitanje implements Serializable {
    private String mPitanje;
    private String mOdgovorA;
    private String mOdgovorB;
    private String mOdgovorC;
    private String mOdgovorD;
    private String mRazneInfomacije;
    private int mTezinaPitanja;
    private int mTocanOdgovor;

    public Pitanje(Map<String, Object> map) {
        this.mPitanje = (String) map.get("pitanje");
        this.mOdgovorA = (String) map.get("odgovorA");
        this.mOdgovorB = (String) map.get("odgovorB");
        this.mOdgovorC = (String) map.get("odgovorC");
        this.mOdgovorD = (String) map.get("odgovorD");
        this.mRazneInfomacije = (String) map.get("razneInformacije");
        this.mTezinaPitanja = (int) map.get("tezinaPitanja");
        this.mTocanOdgovor = (int) map.get("tocanOdgovor");
    }

    public Pitanje() {}

    public Pitanje(String pitanje, String a, String b, String c, String d, String razneInformacije,
                   int tezina, int tocanOdgovor) {
        this.mPitanje = pitanje;
        this.mOdgovorA = a;
        this.mOdgovorB = b;
        this.mOdgovorC = c;
        this.mOdgovorD = d;
        this.mRazneInfomacije = razneInformacije;
        this.mTezinaPitanja = tezina;
        this.mTocanOdgovor = tocanOdgovor;
    }

    public void setPitanje(String pitanje) {this.mPitanje = pitanje;}
    public String getPitanje() {return this.mPitanje;}

    public void setOdgovorA(String odgovorA) {this.mOdgovorA = odgovorA;}
    public String getOdgovorA() {return this.mOdgovorA;}

    public void setOdgovorB(String odgovorB) {this.mOdgovorB = odgovorB;}
    public String getOdgovorB() {return this.mOdgovorB;}

    public void setOdgovorC(String odgovorC) {this.mOdgovorC = odgovorC;}
    public String getOdgovorC() {return this.mOdgovorC;}

    public void setOdgovorD(String odgovorD) {this.mOdgovorD = odgovorD;}
    public String getOdgovorD() {return this.mOdgovorD;}

    public void setRazneInformacije(String razneInformacije) {this.mRazneInfomacije = razneInformacije;}
    public String getRazneInformacije() {return this.mRazneInfomacije;}

    public void setTocanOdgovor(int tocno) {this.mTocanOdgovor = tocno;}
    public int getTocanOdgovor() {return this.mTocanOdgovor;}

    public void setTezinaPitanja(int tezina) {this.mTezinaPitanja = tezina;}
    public int getTezinaPitanja() {return this.mTezinaPitanja;}

    @Exclude
    public String tocnoString() {
        if (mTocanOdgovor == 1)
            return mOdgovorA;
        else if (mTocanOdgovor == 2)
            return mOdgovorB;
        else if (mTocanOdgovor == 3)
            return mOdgovorC;
        return mOdgovorD;
    }
}
