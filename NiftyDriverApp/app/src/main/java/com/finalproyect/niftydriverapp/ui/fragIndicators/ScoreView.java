package com.finalproyect.niftydriverapp.ui.fragIndicators;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;

import java.util.List;


public class ScoreView extends Fragment {

    private int currentProgress = 0;
    private ProgressBar pg_acceleration, pg_braking, pg_cornering, pg_speed;
    private TextView tv_proAcceleration, tv_proBraking, tv_proCornering, tv_proSpeed;
    private long userId;

    public long getUserId() {
        return userId;
    }

    SharedPreferences sp;//Init sharepreferences for user

    SharedPreferences.Editor editor;

    public void setUserId(long userId) {
        this.userId = userId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_view, container, false);
        // Inflate the layout for this fragment


        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId", 0);
        setUserId(userId);

        pg_acceleration = (ProgressBar) view.findViewById(R.id.pg_acceleration);
        pg_braking = (ProgressBar) view.findViewById(R.id.pg_braking);
        pg_cornering = (ProgressBar) view.findViewById(R.id.pg_cornering);
        pg_speed = (ProgressBar) view.findViewById(R.id.pg_speed);

        tv_proAcceleration = (TextView) view.findViewById(R.id.tv_proAcceleration);
        tv_proBraking = (TextView) view.findViewById(R.id.tv_proBraking);
        tv_proCornering = (TextView) view.findViewById(R.id.tv_proCornering);
        tv_proSpeed = (TextView) view.findViewById(R.id.tv_proSpeed);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean acceleration;
        boolean braking;
        boolean left;
        boolean right;
        boolean speed;
        int count;
        int count2 = 0;
        int accCount = 0;
        int brakingCount = 0;
        int LeftCount = 0;
        int RightCount = 0;

        float totalAccCount = 0;

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);
        List<Trip> trips = dao.getAllTripsByUser(userId);

        for (Trip trip : trips) {

            List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(trip.getTripId());
            count = fusionSensors.size();
            Log.d("onResumeScoreview", "count" + count);
            for (FusionSensor fusionSensor : fusionSensors) {

                acceleration = fusionSensor.isHardAcc();
                braking = fusionSensor.isHardDes();
                left = fusionSensor.isSharpLeft();
                right = fusionSensor.isSharpRight();

                if (acceleration == true) {
                    accCount++;
                }
                Log.d("onResumeScoreview", "accCount" + accCount);
                if (braking == true) {
                    brakingCount++;
                }
                Log.d("onResumeScoreview", "brakingCount" + brakingCount);
                if (left == true) {
                    LeftCount++;
                }
                Log.d("onResumeScoreview", "LeftCount" + LeftCount);
                if (right == true) {
                    RightCount++;
                }
                Log.d("onResumeScoreview", "RightCount" + RightCount);


            }


        }


        if (trips.size() > 0 && accCount > 0 || brakingCount > 0 || LeftCount > 0 || RightCount > 0) {
            pg_acceleration.setProgress(100 - 5 * (accCount / trips.size()));// require the sum of sussion sensor  average
            pg_braking.setProgress(100 - 5 * (brakingCount / trips.size()));
            pg_cornering.setProgress(100 - 5 * (LeftCount / trips.size()) - 5 * (RightCount / trips.size()));
            pg_speed.setProgress(dao.getAverageSpeedByUser(userId));
            tv_proAcceleration.setText(String.valueOf(100 - 5 * (accCount / trips.size())));
            tv_proBraking.setText(String.valueOf(100 - 5 * (brakingCount / trips.size())));
            tv_proCornering.setText(String.valueOf(100 - 5 * (LeftCount / trips.size()) - 3 * (RightCount / trips.size())));
            ;
            tv_proSpeed.setText(String.valueOf(dao.getAverageSpeedByUser(userId)));
            ;


        } else {
            pg_acceleration.setProgress(100);// require the sum of sussion sensor  average
            pg_braking.setProgress(100);
            pg_cornering.setProgress(100);
            pg_speed.setProgress(dao.getAverageSpeedByUser(userId));
            tv_proAcceleration.setText("100");
            tv_proBraking.setText("100");
            tv_proCornering.setText("100");
            tv_proSpeed.setText("100");

        }

    }
}