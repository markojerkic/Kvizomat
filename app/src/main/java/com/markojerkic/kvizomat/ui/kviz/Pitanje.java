package com.markojerkic.kvizomat.ui.kviz;

import java.io.Serializable;

public class Pitanje implements Serializable {
    private String mPitanje;
    private String mOdgovorA;
    private String mOdgovorB;
    private String mOdgovorC;
    private String mOdgovorD;
    private String mRazneInfomacije;
    private int mTezinaPitanja;
    private int mTocanOdgovor;

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
}
