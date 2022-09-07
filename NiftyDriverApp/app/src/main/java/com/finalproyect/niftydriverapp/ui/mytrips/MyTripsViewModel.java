package com.finalproyect.niftydriverapp.ui.mytrips;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyTripsViewModel extends ViewModel {
    private final MutableLiveData<String> m2Text;

    public MyTripsViewModel() {
        m2Text = new MutableLiveData<>();
        m2Text.setValue("This is My Trips fragment");
    }

    public LiveData<String> getText() {
        return m2Text;
    }
}