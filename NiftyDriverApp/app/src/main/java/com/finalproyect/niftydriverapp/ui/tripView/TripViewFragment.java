package com.finalproyect.niftydriverapp.ui.tripView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.finalproyect.niftydriverapp.databinding.FragmentTripviewBinding;

public class TripViewFragment extends Fragment {

    private FragmentTripviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TripViewViewModel tripViewViewModel =
                new ViewModelProvider(this).get(TripViewViewModel.class);

        binding = FragmentTripviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTripView;
        tripViewViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}