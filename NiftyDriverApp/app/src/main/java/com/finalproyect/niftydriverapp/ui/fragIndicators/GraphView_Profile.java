package com.finalproyect.niftydriverapp.ui.fragIndicators;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;


public class GraphView_Profile extends Fragment  {
    //variable for view port

    AppDatabase db = AppDatabase.getDbInstance(getContext());
    DAO dao = db.driverDao();

    SharedPreferences sp;


    public void setUserid(long userid) {
        this.userId = userid;
    }

    private long userId;

    //variable for view port
    private Viewport viewport;
    LineGraphSeries<DataPoint> Acceleration = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Braking = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Cornering = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Speeding = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Scoring = new LineGraphSeries<DataPoint>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_view, container, false);
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userid = sp.getLong("userId", 0);

        setUserid(userid);

        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graphview_Profile);
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
        Braking.setColor(Color.RED);
        graph.addSeries(Braking);
        Cornering.setColor(Color.WHITE);
        graph.addSeries(Cornering);
        Speeding.setColor(Color.YELLOW);
        graph.addSeries(Speeding);
        Scoring.setColor(Color.MAGENTA);
        graph.addSeries(Scoring);





        addValuesGraph();








        return view;
    }

    public void addValuesGraph(){
        Log.d("GrapViewProfile", "addValuesGraph start");
        boolean acceleration;
        boolean braking;
        boolean left;
        boolean right;

        int count;
        int i=0;
        int accCount = 0;
        int brakingCount = 0;
        int LeftCount = 0;
        int RightCount = 0;



        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        List<Trip> trips = dao.getAllTripsByUser(userId);
        Log.d("GrapViewProfile", "tripSize: " + trips.size());
        for (Trip trip : trips) {
            Log.d("GrapViewProfile", "tripScore: " + trip.getScoreTrip());
            List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(trip.getTripId());
            count = fusionSensors.size();
            Log.d("GrapViewProfile", "count" + count);
            for (FusionSensor fusionSensor : fusionSensors) {

                acceleration = fusionSensor.isHardAcc();
                braking = fusionSensor.isHardDes();
                left = fusionSensor.isSharpLeft();
                right = fusionSensor.isSharpRight();

                if (acceleration == true) {
                    accCount++;
                }
                Log.d("GrapViewProfile", "accCount" + accCount);
                if (braking == true) {
                    brakingCount++;
                }
                Log.d("GrapViewProfile", "brakingCount" + brakingCount);
                if (left == true) {
                    LeftCount++;
                }
                Log.d("GrapViewProfile", "LeftCount" + LeftCount);
                if (right == true) {
                    RightCount++;
                }
                Log.d("GrapViewProfile", "RightCount" + RightCount);


            }
            float accel = 100 - 5 * accCount / trips.size();
            float desace = 100 - 5 * brakingCount / trips.size();
            float corne = 100 - 5 * LeftCount / trips.size() - 5 * RightCount / trips.size();


            Acceleration.appendData(new DataPoint(i+1, accel), true, trips.size());
            Braking.appendData(new DataPoint(i+1, desace), true, trips.size());
            Cornering.appendData(new DataPoint(i+1, corne), true, trips.size());
            Speeding.appendData(new DataPoint(i+1, trip.getAveSpeed()), true, trips.size());
            Scoring.appendData(new DataPoint(i+1, trip.getScoreTrip()), true, trips.size());

            Log.d("GrapViewProfileACCEL"," i: "+i+" Accel: "+ accel);
            i++;

        }






    }



}