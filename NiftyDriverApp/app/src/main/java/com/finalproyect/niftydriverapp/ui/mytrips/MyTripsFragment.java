package com.finalproyect.niftydriverapp.ui.mytrips;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.finalproyect.niftydriverapp.databinding.FragmentMyTripsBinding;


public class MyTripsFragment extends Fragment {

    private FragmentMyTripsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyTripsViewModel myTripsViewModel =
                new ViewModelProvider(this).get(MyTripsViewModel.class);

        binding = FragmentMyTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMyTrips;
        myTripsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}