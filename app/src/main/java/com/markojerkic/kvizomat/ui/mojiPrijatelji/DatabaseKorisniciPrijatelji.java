package com.markojerkic.kvizomat.ui.mojiPrijatelji;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseKorisniciPrijatelji {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisniciOnline");

}
