package com.finalproyect.niftydriverapp.ui.mytrips;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.databinding.FragmentMyTripsBinding;


public class MyTripsFragment extends Fragment {

    private FragmentMyTripsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        return view;
    }


}