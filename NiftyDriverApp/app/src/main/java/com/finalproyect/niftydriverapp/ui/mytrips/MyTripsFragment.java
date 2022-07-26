package com.finalproyect.niftydriverapp.ui.mytrips;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.RecyclerViewInterface;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;
import com.finalproyect.niftydriverapp.ui.tripView.TripViewFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MyTripsFragment extends Fragment implements RecyclerViewInterface {

    private TripListAdapter tripListAdapter;

    public void setUserid(long userId) {
        this.userId = userId;
    }

    private long userId;

    SharedPreferences sp;//Init sharepreferences for user
    SharedPreferences.Editor editor;

    AppDatabase db = AppDatabase.getDbInstance(getContext());
    DAO dao = db.driverDao();


    private TextView tv_mainScore, tv_totalTrips, tv_totalKilometres, tv_totalHours, tv_titleHours, tv_dateFirstTrip, tv_lastTrip;


    // for save preferences like user id and user state means open session
    @Override
    public void onAttach(@NonNull Context context) {
        sp = context.getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit(); // init sharedPreferences
        super.onAttach(context);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        Log.d("ActivityLyfeCycle", "onCreate");
        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId", 0);
        setUserid(userId);

        //Main score
        tv_mainScore = (TextView) view.findViewById(R.id.tv_mainScore);
        // Number of trips
        tv_totalTrips = (TextView) view.findViewById(R.id.tv_totalTrips);
        //Number Kilometers
        tv_totalKilometres = (TextView) view.findViewById(R.id.tv_totalKilometres);
        // Total Hours
        tv_titleHours = (TextView) view.findViewById(R.id.tv_titleHours);
        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);
        //Date first and last date trip
        tv_dateFirstTrip = (TextView) view.findViewById(R.id.tv_dateFirstTrip);
        tv_lastTrip = (TextView) view.findViewById(R.id.tv_lastTrip);
        data();

        initRecyclerview(view);
        loadTrip();

        return view;
    }

    public void data() {
        tv_mainScore.setText(ProfileFragment.mainScore(userId));
        tv_totalTrips.setText(ProfileFragment.numTrips(userId));
        tv_totalKilometres.setText(ProfileFragment.numKilometres(userId));
        float totalTime = ProfileFragment.totalTripHours(userId);

        if (totalTime > 60) {
            tv_titleHours.setText("Hours");
            totalTime = totalTime / 60;
        }

        tv_totalHours.setText(String.format("%.1f", totalTime));
        tv_dateFirstTrip.setText(getDateFirstTrip());
        tv_lastTrip.setText(getDateLastTrip());
    }

    private void initRecyclerview(View view) {
        Log.d("ActivityLyfeCycle", "init RecyclerView");
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tripListAdapter = new TripListAdapter(this, getContext());
        recyclerView.setAdapter(tripListAdapter);
    }


    private void loadTrip() {
        Log.d("ActivityLyfeCycle", "loadTrip");
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<Trip> tripList = db.driverDao().getAllTripsByUser(userId);
        Log.d("ActivityLyfeCycle", "loadTrip");
        tripListAdapter.setTripList(tripList);
        Log.d("ActivityLyfeCycle", "loadTrip");


    }

    private String getDateFirstTrip() {

        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<Trip> tripList = db.driverDao().getAllTripsByUser(userId);
        Date startDate = null;

        if (!tripList.isEmpty()) {
            startDate = new Date(tripList.get(0).getStartDate());
        } else {
            startDate = new Date(System.currentTimeMillis());
        }


        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
        return df.format(startDate);


    }

    public String getDateLastTrip() {
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        List<Trip> tripList = db.driverDao().getAllTripsByUser(userId);
        Date startDate = null;
        if (!tripList.isEmpty()) {
            startDate = new Date(tripList.get(tripList.size() - 1).getStartDate());
        } else {
            startDate = new Date(System.currentTimeMillis());
        }

        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm");

        return df.format(startDate);


    }


    @Override
    public void onItemClick(int position) {
        // .makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
        /**Pass this values to the shared preference*/
        editor.putInt("position", position);
        editor.commit();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new TripViewFragment())
                .commit();


    }

    //Used when is deleted a item in recycle view
    public void refreshData() {
        data();


    }




}