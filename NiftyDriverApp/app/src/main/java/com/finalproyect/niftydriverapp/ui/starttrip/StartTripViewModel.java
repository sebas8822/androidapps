package com.finalproyect.niftydriverapp.ui.starttrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartTripViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StartTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Start Trip fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}