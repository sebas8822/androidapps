package com.finalproyect.niftydriverapp.ui.fragIndicators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.tripView.TripViewFragment;

import java.util.List;

public class ScoreViewTripView  extends Fragment {



    private ProgressBar pg_accelerationTripView, pg_brakingTripView, pg_corneringTripView, pg_speedTripView;
    private TextView tv_proAccelerationTripView, tv_proBrakingTripView, tv_proCorneringTripView, tv_proSpeedTripView;
    private ImageButton bt_infoAccelerationTripView,bt_infoBrakingTripView,bt_infoCorneringTripView,bt_infoSpeedTripView;
    private long userId;
    private int position;

    Trip lastTrip;

    public void setPosition(int position) {
        this.position = position;
    }

    SharedPreferences sp;//Init sharepreferences for user

    SharedPreferences.Editor editor;

    public void setUserId(long userId) {
        this.userId = userId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_view_trip_view, container, false);





        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId", 0);
        int position = sp.getInt("position", 0);
        setUserId(userId);
        setPosition(position);



        pg_accelerationTripView = (ProgressBar) view.findViewById(R.id.pg_accelerationTripView);
        pg_brakingTripView = (ProgressBar) view.findViewById(R.id.pg_brakingTripView);
        pg_corneringTripView = (ProgressBar) view.findViewById(R.id.pg_corneringTripView);
        pg_speedTripView = (ProgressBar) view.findViewById(R.id.pg_speedTripView);

        tv_proAccelerationTripView = (TextView) view.findViewById(R.id.tv_proAccelerationTripView);
        tv_proBrakingTripView = (TextView) view.findViewById(R.id.tv_proBrakingTripView);
        tv_proCorneringTripView = (TextView) view.findViewById(R.id.tv_proCorneringTripView);
        tv_proSpeedTripView = (TextView) view.findViewById(R.id.tv_proSpeedTripView);

        bt_infoAccelerationTripView = (ImageButton) view.findViewById(R.id.bt_infoAccelerationTripView);
        bt_infoBrakingTripView = (ImageButton) view.findViewById(R.id.bt_infoBrakingTripView);
        bt_infoCorneringTripView = (ImageButton) view.findViewById(R.id.bt_infoCorneringTripView);
        bt_infoSpeedTripView = (ImageButton) view.findViewById(R.id.bt_infoSpeedTripView);



        bt_infoAccelerationTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Acceleration");
                builder.setMessage("Please anticipate where you are going to stop an press the brake soft with enough distance from another car");
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        bt_infoBrakingTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Braking");
                builder.setMessage("Please anticipate where you are going to stop an press the brake soft with enough distance from another car");
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        bt_infoCorneringTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Cornering");
                builder.setMessage("Please anticipate where you are going to stop an press the brake soft with enough distance from another car");
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        bt_infoSpeedTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Speed");
                builder.setMessage("Please anticipate where you are going to stop an press the brake soft with enough distance from another car");
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });




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
        if(position>0) {
            AppDatabase db = AppDatabase.getDbInstance(getContext());
            DAO dao = db.driverDao();
            List<Trip> tripList = dao.getAllTripsByUser(userId);
            lastTrip = tripList.get(position);


            List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(lastTrip.getTripId());
            count = fusionSensors.size();
            Log.d("onResumeSTripView", "count" + count);
            for (FusionSensor fusionSensor : fusionSensors) {

                acceleration = fusionSensor.isHardAcc();
                braking = fusionSensor.isHardDes();
                left = fusionSensor.isSharpLeft();
                right = fusionSensor.isSharpRight();

                if (acceleration == true) {
                    accCount++;
                }

                if (braking == true) {
                    brakingCount++;
                }

                if (left == true) {
                    LeftCount++;
                }

                if (right == true) {
                    RightCount++;
                }


            }

            Log.d("onResumeSTripView", "accCount" + accCount);
            Log.d("onResumeSTripView", "brakingCount" + brakingCount);
            Log.d("onResumeSTripView", "LeftCount" + LeftCount);
            Log.d("onResumeSTripView", "RightCount" + RightCount);


            pg_accelerationTripView.setProgress(100 - 5 * accCount);// require the sum of sussion sensor  average
            pg_brakingTripView.setProgress(100 - 5 * brakingCount);
            pg_corneringTripView.setProgress(100 - (5 * LeftCount) - (5 * RightCount));
            pg_speedTripView.setProgress((int) lastTrip.getAveSpeed());
            tv_proAccelerationTripView.setText(String.valueOf(100 - 5 * (accCount)));
            tv_proBrakingTripView.setText(String.valueOf(100 - 5 * (brakingCount)));
            tv_proCorneringTripView.setText(String.valueOf(100 - 5 * (LeftCount) - 5 * (RightCount)));

            tv_proSpeedTripView.setText(String.valueOf((int) lastTrip.getAveSpeed()));
            Toast.makeText(getContext(), "Current score" + lastTrip.getScoreTrip(), Toast.LENGTH_LONG).show();

        }else{

            pg_accelerationTripView.setProgress(100);// require the sum of sussion sensor  average
            pg_brakingTripView.setProgress(100);
            pg_corneringTripView.setProgress(100 );
            pg_speedTripView.setProgress(0);
            tv_proAccelerationTripView.setText(String.valueOf(100));
            tv_proBrakingTripView.setText(String.valueOf(100));
            tv_proCorneringTripView.setText(String.valueOf(100 ));

            tv_proSpeedTripView.setText(String.valueOf(0));


        }




    }


}
