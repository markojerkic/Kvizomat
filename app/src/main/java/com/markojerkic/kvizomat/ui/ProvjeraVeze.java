package com.markojerkic.kvizomat.ui;

import android.content.Context;

import com.markojerkic.kvizomat.NetworkConnection;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;

public class ProvjeraVeze {

    public static boolean provjeriSlika(Korisnik korisnik, Context context) {
        return !korisnik.getUri().equals("null") && NetworkConnection.hasConnection(context);
    }
}
