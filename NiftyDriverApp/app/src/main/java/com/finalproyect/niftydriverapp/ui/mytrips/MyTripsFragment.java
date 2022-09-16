package com.finalproyect.niftydriverapp.ui.mytrips;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;


import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.RecyclerViewInterface;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;
import com.finalproyect.niftydriverapp.ui.tripView.TripViewFragment;

import java.util.List;


public class MyTripsFragment extends Fragment implements RecyclerViewInterface {

    private TripListAdapter tripListAdapter;

    public void setUserid(long userId) {
        this.userId = userId;
    }

    private long userId;

    SharedPreferences sp;//Init sharepreferences for user
    SharedPreferences.Editor editor;





    private TextView tv_mainScore, tv_totalTrips, tv_totalKilometres, tv_totalHours;


    // for save preferences like user id and user state means open session
    @Override
    public void onAttach(@NonNull Context context) {
        sp = context.getSharedPreferences("userProfile",Context.MODE_PRIVATE );
        editor = sp.edit(); // init sharedPreferences
        super.onAttach(context);
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId",0);
        setUserid(userId);

        //Main score

        tv_mainScore = (TextView) view.findViewById(R.id.tv_mainScore);
        tv_mainScore.setText(ProfileFragment.mainScore(userId));

        // Number of trips

        tv_totalTrips = (TextView) view.findViewById(R.id.tv_totalTrips);
        tv_totalTrips.setText(ProfileFragment.numTrips(userId));

        //Number Kilometers

        tv_totalKilometres = (TextView) view.findViewById(R.id.tv_totalKilometres);
        tv_totalKilometres.setText(ProfileFragment.numKilometres(userId));

        // Total Hours

        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);
        tv_totalHours.setText(ProfileFragment.totalTripHours(userId));



        initRecyclerview(view);
        loadTrip();

        return view;
    }
    private void initRecyclerview(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tripListAdapter = new TripListAdapter(this, getContext());
        recyclerView.setAdapter(tripListAdapter);
    }


    private void loadTrip() {
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<Trip> tripList = db.driverDao().getAllTrips(userId);
        tripListAdapter.setTripList(tripList);


    }


    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(),"Item Clicked", Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new TripViewFragment())
                .commit();

        /**Pass this values to the shared preference*/
        editor.putInt("position", position);
        editor.commit();




    }
}