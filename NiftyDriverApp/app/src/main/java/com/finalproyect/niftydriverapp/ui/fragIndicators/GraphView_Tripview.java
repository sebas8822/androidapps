package com.finalproyect.niftydriverapp.ui.fragIndicators;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;


public class GraphView_Tripview extends Fragment {
    //variable for view port

    AppDatabase db = AppDatabase.getDbInstance(getContext());
    DAO dao = db.driverDao();

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

    //variable for view port
    private Viewport viewport;
    BarGraphSeries<DataPoint> Acceleration = new BarGraphSeries<DataPoint>();
    BarGraphSeries<DataPoint> Braking = new BarGraphSeries<DataPoint>();
    BarGraphSeries<DataPoint> Cornering = new BarGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Speeding = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Scoring = new LineGraphSeries<DataPoint>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_tripview, container, false);


        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId", 0);
        int position = sp.getInt("position", 0);
        setUserId(userId);
        setPosition(position);

        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graphview_tripView);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(false);
        viewport.setMaxY(110);
        viewport.setXAxisBoundsManual(false);
        viewport.setYAxisBoundsManual(true);

//        Acceleration.setBackgroundColor(Color.GREEN);
//        Acceleration.setDrawBackground(true);

        Acceleration.setColor(Color.GREEN);
        graph.addSeries(Acceleration);
        // draw values on top
        Acceleration.setDrawValuesOnTop(true);
        Acceleration.setValuesOnTopColor(Color.RED);


        Braking.setColor(Color.RED);
        graph.addSeries(Braking);
        Braking.setDrawValuesOnTop(true);
        Braking.setValuesOnTopColor(Color.RED);

        Cornering.setColor(Color.WHITE);
        graph.addSeries(Cornering);
        // draw values on top
        Cornering.setDrawValuesOnTop(true);
        Cornering.setValuesOnTopColor(Color.RED);


        Speeding.setColor(Color.YELLOW);
        graph.addSeries(Speeding);
        Scoring.setColor(Color.MAGENTA);
        graph.addSeries(Scoring);


        addValuesGraph();


        return view;
    }

    public void addValuesGraph() {
        Log.d("GrapViewProfile", "addValuesGraph start");
        boolean acceleration;
        boolean braking;
        boolean left;
        boolean right;

        int count;
        int i = 0;
        int accCount = 0;
        int brakingCount = 0;
        int LeftCount = 0;
        int RightCount = 0;


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




//            Braking.appendData(new DataPoint(i + 1, desace), true, trips.size());
//            Cornering.appendData(new DataPoint(i + 1, corne), true, trips.size());
            Speeding.appendData(new DataPoint(i + 1, (int)fusionSensor.getCarSpeed()), true, fusionSensors.size());
            //Scoring.appendData(new DataPoint(i + 1, trip.getScoreTrip()), true, trips.size());

            Log.d("GrapViewProfileACCEL", " i: " + i + " Accel: " + accCount);
            i++;

        }
        Acceleration.appendData(new DataPoint( 1, accCount*5), true, 1);
        Braking.appendData(new DataPoint( 2, brakingCount*5), true, 2);
        Cornering.appendData(new DataPoint( 3, LeftCount*5+RightCount*5), true, 3);





    }


}