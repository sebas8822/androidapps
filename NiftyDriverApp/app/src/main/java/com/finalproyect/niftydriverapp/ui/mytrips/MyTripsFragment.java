package com.finalproyect.niftydriverapp.ui.mytrips;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.databinding.FragmentMyTripsBinding;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;

import java.util.List;


public class MyTripsFragment extends Fragment {

    private TripListAdapter tripListAdapter;

    private TextView tv_mainScore, tv_totalTrips, tv_totalKilometres, tv_totalHours;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        //Main score

        tv_mainScore = (TextView) view.findViewById(R.id.tv_mainScore);
        tv_mainScore.setText(ProfileFragment.mainScore());

        // Number of trips

        tv_totalTrips = (TextView) view.findViewById(R.id.tv_totalTrips);
        tv_totalTrips.setText(ProfileFragment.numTrips());

        //Number Kilometers

        tv_totalKilometres = (TextView) view.findViewById(R.id.tv_totalKilometres);
        tv_totalKilometres.setText(ProfileFragment.numKilometres());

        // Total Hours

        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);
        tv_totalHours.setText(ProfileFragment.totalTripHours());



        initRecyclerview(view);
        loadTrip();

        return view;
    }
    private void initRecyclerview(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tripListAdapter = new TripListAdapter(getContext());
        recyclerView.setAdapter(tripListAdapter);
    }


    private void loadTrip() {
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<Trip> tripList = db.driverDao().getAllTrips(1);
        tripListAdapter.setTripList(tripList);


    }


}