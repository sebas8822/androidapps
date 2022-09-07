package com.finalproyect.niftydriverapp.ui.tripView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TripViewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TripViewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Trip View fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}