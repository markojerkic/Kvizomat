package com.markojerkic.kvizomat.ui.listaKorisnika;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DodajPrijateljaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DodajPrijateljaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}