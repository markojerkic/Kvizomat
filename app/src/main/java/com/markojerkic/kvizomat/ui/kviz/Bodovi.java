package com.markojerkic.kvizomat.ui.kviz;

import java.util.ArrayList;

public class Bodovi {

    public static float izracunajBodove(ArrayList<Pitanje> pitanja, ArrayList<Integer> odgovori) {
        float rez = 0;
        for (int i = 0; i < 4; i++) {
            int br = 0;
            for (int j = 0; j < 3; j++) {
                if (pitanja.get(i +j).getTocanOdgovor() == odgovori.get(i + j))
                    br ++;
            }
            rez += ((float) br /  4f)*((float) i+1f) * 0.7f;
        }
        return rez;
    }
}
