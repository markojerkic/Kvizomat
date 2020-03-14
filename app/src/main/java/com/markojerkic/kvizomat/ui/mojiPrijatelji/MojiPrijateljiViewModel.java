package com.markojerkic.kvizomat.ui.mojiPrijatelji;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MojiPrijateljiViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MojiPrijateljiViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}