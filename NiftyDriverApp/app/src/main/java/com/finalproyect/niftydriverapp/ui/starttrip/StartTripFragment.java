package com.finalproyect.niftydriverapp.ui.starttrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.finalproyect.niftydriverapp.databinding.FragmentStarttripBinding;

public class StartTripFragment extends Fragment {

    private FragmentStarttripBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StartTripViewModel startTripViewModel =
                new ViewModelProvider(this).get(StartTripViewModel.class);

        binding = FragmentStarttripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textStartTrip;
        startTripViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}