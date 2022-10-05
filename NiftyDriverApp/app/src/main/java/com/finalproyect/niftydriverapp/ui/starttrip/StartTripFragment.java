package com.finalproyect.niftydriverapp.ui.starttrip;

import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartTripFragment extends Fragment implements SensorEventListener {


    private TextView tvSpeed, tvUnit, tvLat, tvLon, tvAccuracy, tvHeading, tvMaxSpeed,
            tv_Xaxis, tv_Yaxis, tv_pith, tv_yaw, tv_distance, tv_title2, tv_title1, textView9, textView8,
            tv_totalHours, tv_total_Current, tv_aveSpeed, tv_finalScore, tv_safeAccel,
            tv_safeDesa, tv_safeLeft, tv_safeRight, tv_hardAccel, tv_hardDes, tv_sharpLeft,
            tv_sharpRight, tv_fusionDB, tv_tripsDB, tv_threshold_Y, tv_threshold_X, tv_threshold_P, tv_threshold_R, tv_mode;


    private Button bt_startTrip, bt_resetFusionDatabase, bt_UP_threshold, bt_DOWN_threshold, bt_OPEN_threshold, bt_CLOSE_threshold, bt_Reset_Thresholds, bt_mode_left, bt_mode_right;
    private Switch switch_DEV;
    private ImageView image_location;

    private static final String unit = "km/h";
    private int unitType;
    private NotificationCompat.Builder mbuilder;
    private NotificationManager mnotice;
    private double maxSpeed = -100.0;
    private StartTripFragment activity;
    private Context context;
    private SharedPreferences prefs;

    private SensorManager mSensorManager = null;


    long previousTime = 0;
    private Timer fuseTimer = new Timer();
    public static final int TIME_CONSTANT = 10;

    public void setUserid(long userid) {
        this.userId = userid;
    }

    private long userId;

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    private long tripID;

    //Init Sharepreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    AppDatabase db = AppDatabase.getDbInstance(getContext());
    DAO dao = db.driverDao();

    View view;

    int mode;

    boolean switchDevState;

    public void setSwitchDevState(boolean switchDevState) {
        this.switchDevState = switchDevState;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sp = getContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();

        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userid = sp.getLong("userId", 0);
        boolean switchDevState = sp.getBoolean("switchDevState", false);
        setUserid(userid);
        setSwitchDevState(switchDevState);

        view = inflater.inflate(R.layout.start_trip_test_fragment_dev, container, false);


        image_location = (ImageView) view.findViewById(R.id.image_location);


        tvSpeed = (TextView) view.findViewById(R.id.tvSpeed);
        tvMaxSpeed = (TextView) view.findViewById(R.id.tvMaxSpeed);
        tvUnit = (TextView) view.findViewById(R.id.tvUnitc);
        tvLat = (TextView) view.findViewById(R.id.tvLat);
        tvLon = (TextView) view.findViewById(R.id.tvLon);
        tv_title2 = (TextView) view.findViewById(R.id.tv_title2);
        tv_title1 = (TextView) view.findViewById(R.id.tv_title1);
        textView9 = (TextView) view.findViewById(R.id.textView9);
        textView8 = (TextView) view.findViewById(R.id.textView8);
        tvAccuracy = (TextView) view.findViewById(R.id.tvAccuracy);
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);
        tv_Xaxis = (TextView) view.findViewById(R.id.tv_Xaxis);
        tv_Yaxis = (TextView) view.findViewById(R.id.tv_Yaxis);

        tv_pith = (TextView) view.findViewById(R.id.tv_pith);
        tv_yaw = (TextView) view.findViewById(R.id.tv_yaw);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_total_Current = (TextView) view.findViewById(R.id.tv_total_Current);
        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);
        tv_aveSpeed = (TextView) view.findViewById(R.id.tv_aveSpeed);
        tv_finalScore = (TextView) view.findViewById(R.id.tv_finalScore);
        tv_safeAccel = (TextView) view.findViewById(R.id.tv_safeAccel);
        tv_safeDesa = (TextView) view.findViewById(R.id.tv_safeDesa);
        tv_safeLeft = (TextView) view.findViewById(R.id.tv_safeLeft);
        tv_safeRight = (TextView) view.findViewById(R.id.tv_safeRight);
        tv_hardAccel = (TextView) view.findViewById(R.id.tv_hardAccel);
        tv_hardDes = (TextView) view.findViewById(R.id.tv_hardDes);
        tv_sharpLeft = (TextView) view.findViewById(R.id.tv_sharpLeft);
        tv_sharpRight = (TextView) view.findViewById(R.id.tv_sharpRight);


        tv_fusionDB = (TextView) view.findViewById(R.id.tv_fusionDB);
        tv_fusionDB.setText(String.valueOf(dao.getAllFusionSensor().size()));

        tv_tripsDB = (TextView) view.findViewById(R.id.tv_tripsDB);
        tv_tripsDB.setText(String.valueOf(dao.getAllTripsByUser(userId).size()));

        //Thresholds
        tv_threshold_Y = (TextView) view.findViewById(R.id.tv_threshold_Y);
        tv_threshold_X = (TextView) view.findViewById(R.id.tv_threshold_X);
        tv_threshold_P = (TextView) view.findViewById(R.id.tv_threshold_P);
        tv_threshold_R = (TextView) view.findViewById(R.id.tv_threshold_R);
        tv_mode = (TextView) view.findViewById(R.id.tv_mode);


        //Buttons
        bt_Reset_Thresholds = (Button) view.findViewById(R.id.bt_Reset_Thresholds);
        bt_UP_threshold = (Button) view.findViewById(R.id.bt_UP_threshold);
        bt_DOWN_threshold = (Button) view.findViewById(R.id.bt_DOWN_threshold);
        bt_OPEN_threshold = (Button) view.findViewById(R.id.bt_OPEN_threshold);
        bt_CLOSE_threshold = (Button) view.findViewById(R.id.bt_CLOSE_threshold);
        bt_resetFusionDatabase = (Button) view.findViewById(R.id.bt_resetFusionDatabase);
        bt_startTrip = (Button) view.findViewById(R.id.bt_startTrip);
        bt_mode_left = (Button) view.findViewById(R.id.bt_mode_left);
        bt_mode_right = (Button) view.findViewById(R.id.bt_mode_right);


        thresholds();
        setVisibility(switchDevState);


        bt_Reset_Thresholds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetThresholds();
            }
        });


        bt_UP_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdUPDOWN = thresholdUPDOWN + .1;

                thresholds();
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " Y1: " + Y1 + " Y2: " + Y2);
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " X1: " + X1 + " X2: " + X2);
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " P1: " + P1 + " P2: " + P2);
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " R1: " + R1 + " R2: " + R2);
            }
        });


        bt_DOWN_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdUPDOWN = thresholdUPDOWN - .1;

                thresholds();
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + "Y1: " + Y1 + "Y2: " + Y2);
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + " X1: " + X1 + " X2: " + X2);
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + " P1: " + P1 + " P2: " + P2);
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + " R1: " + R1 + " R2: " + R2);
            }
        });


        bt_OPEN_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdOPENCLOSE = thresholdOPENCLOSE + .1;

                thresholds();
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " Y1: " + Y1 + " Y2: " + Y2);
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " X1: " + X1 + " X2: " + X2);
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " P1: " + P1 + " P2: " + P2);
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " R1: " + R1 + " R2: " + R2);
            }
        });


        bt_CLOSE_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdOPENCLOSE = thresholdOPENCLOSE - .1;

                thresholds();
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + "Y1: " + Y1 + "Y2: " + Y2);
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + " X1: " + X1 + " X2: " + X2);
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + " P1: " + P1 + " P2: " + P2);
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + " R1: " + R1 + " R2: " + R2);
            }
        });


        bt_resetFusionDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dao.deleteAllFusionSensor();
                dao.deleteAllTrip();
                tv_fusionDB.setText(String.valueOf(dao.getAllFusionSensor().size()));
                tv_tripsDB.setText(String.valueOf(dao.getAllTripsByUser(userId).size()));


            }
        });


        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip();
            }
        });


        //Devop State
        switch_DEV = (Switch) view.findViewById(R.id.switch_DEV);
        switch_DEV.setChecked(switchDevState);

        switch_DEV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {

                    switch_DEV.setText("DEV");
                    setVisibility(true);
                    editor.putBoolean("switchDevState", true);

                    editor.commit();


                } else {

                    switch_DEV.setText("USER");
                    setVisibility(false);
                    editor.putBoolean("switchDevState", false);

                    editor.commit();

                }

            }
        });

        tv_mode.setText(String.valueOf(mode) + " Both");

        bt_mode_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode <= 2 && mode > 0) {
                    mode--;
                    setTextMode();
                } else {
                    Toast.makeText(getContext(), "no more mode available", Toast.LENGTH_LONG).show();
                }

            }
        });

        bt_mode_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode < 2 && mode >= 0) {
                    mode++;
                    setTextMode();
                } else {
                    Toast.makeText(getContext(), "no more mode available", Toast.LENGTH_LONG).show();
                }

            }
        });


        //FOR SAVE REFERENCES
        previousTime = System.currentTimeMillis();


        //SET UP UNITS PREFERENCES - can be change in the future

        tvUnit.setText(unit);


        // check if the services are available ASK TO THE USER TO ENABLE
        if (!this.isLocationEnabled(getContext())) {


            //show dialog if Location Services is not enabled


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("GPS not found");  // GPS not found
            builder.setMessage("This app requires GPS or Location Service.\\n\\nWould you like to enable Location Service now?"); // Want to enable?
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(intent);
                }
            });

            //if no - bring user to selecting Static Location Activity
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Please enable Location-based service / GPS", Toast.LENGTH_LONG).show();


                }


            });
            builder.create().show();
        }


        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //keep the screen on
        // getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        Xseries.setColor(Color.MAGENTA);
        graph.addSeries(Xseries);
        Yseries.setColor(Color.GREEN);
        graph.addSeries(Yseries);
        Pseries.setColor(Color.YELLOW);
        graph.addSeries(Pseries);
        Rseries.setColor(Color.WHITE);
        graph.addSeries(Rseries);

        //Thresholds
        yUPsafe1.setColor(Color.RED);
        graph.addSeries(yUPsafe1);
        yUPsafe2.setColor(Color.RED);
        graph.addSeries(yUPsafe2);
        yDownsafe1.setColor(Color.RED);
        graph.addSeries(yDownsafe1);
        yDownsafe2.setColor(Color.RED);
        graph.addSeries(yDownsafe2);
        XUPsafe1.setColor(Color.RED);
        graph.addSeries(XUPsafe1);
        XUPsafe2.setColor(Color.RED);
        graph.addSeries(XUPsafe2);
        XDownsafe1.setColor(Color.RED);
        graph.addSeries(XDownsafe1);
        XDownsafe2.setColor(Color.RED);
        graph.addSeries(XDownsafe2);
        yUPHard.setColor(Color.RED);
        graph.addSeries(yUPHard);
        yDownHard.setColor(Color.RED);
        graph.addSeries(yDownHard);
        XUPHard.setColor(Color.RED);
        graph.addSeries(XUPHard);
        XDownHard.setColor(Color.RED);
        graph.addSeries(XDownHard);


        // computing sensor values
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f;
        gyroMatrix[1] = 0.0f;
        gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f;
        gyroMatrix[4] = 1.0f;
        gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f;
        gyroMatrix[7] = 0.0f;
        gyroMatrix[8] = 1.0f;


        /**Init Sensors*/// get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        initListeners();
        // wait for one second until gyroscope and magnetometer/accelerometer
        // data is initialised then scedule the complementary filter task
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                2000, TIME_CONSTANT);


        //resetting the sensor values every 30 sec
        fuseTimer.scheduleAtFixedRate(new ResetSensorValues(), 1000, 30000);


        new SpeedTask(getActivity()).execute("string");

        return view;
    }

    private void setTextMode() {
        if (mode == 0) {
            tv_mode.setText(String.valueOf(mode) + " Both");
        } else if (mode == 1) {
            tv_mode.setText(String.valueOf(mode) + " X&Y");
        } else {
            tv_mode.setText(String.valueOf(mode) + " P&R");
        }


    }

    private void setVisibility(boolean state) {


        if (state == true) {
            tv_distance.setTextSize(10);
            tv_totalHours.setTextSize(10);
            tv_aveSpeed.setTextSize(10);
            tvMaxSpeed.setTextSize(10);
            bt_UP_threshold.setVisibility(View.VISIBLE);
            tv_title2.setVisibility(View.VISIBLE);
            tv_Xaxis.setVisibility(View.VISIBLE);
            tv_Yaxis.setVisibility(View.VISIBLE);
            tv_pith.setVisibility(View.VISIBLE);
            tv_yaw.setVisibility(View.VISIBLE);


            tvLat.setVisibility(View.VISIBLE);
            tvLon.setVisibility(View.VISIBLE);
            tv_total_Current.setVisibility(View.VISIBLE);
            tvHeading.setVisibility(View.VISIBLE);
            tvAccuracy.setVisibility(View.VISIBLE);
            bt_UP_threshold.setVisibility(View.VISIBLE);
            bt_CLOSE_threshold.setVisibility(View.VISIBLE);
            bt_DOWN_threshold.setVisibility(View.VISIBLE);
            bt_OPEN_threshold.setVisibility(View.VISIBLE);
            tv_threshold_P.setVisibility(View.VISIBLE);
            tv_threshold_R.setVisibility(View.VISIBLE);
            tv_threshold_Y.setVisibility(View.VISIBLE);
            tv_threshold_X.setVisibility(View.VISIBLE);
            bt_Reset_Thresholds.setVisibility(View.VISIBLE);
            textView9.setVisibility(View.VISIBLE);
            textView8.setVisibility(View.VISIBLE);
            tv_safeAccel.setVisibility(View.VISIBLE);
            tv_safeDesa.setVisibility(View.VISIBLE);
            tv_safeLeft.setVisibility(View.VISIBLE);
            tv_safeRight.setVisibility(View.VISIBLE);
            tv_hardAccel.setVisibility(View.VISIBLE);
            tv_hardDes.setVisibility(View.VISIBLE);
            tv_sharpLeft.setVisibility(View.VISIBLE);
            tv_sharpRight.setVisibility(View.VISIBLE);
            bt_resetFusionDatabase.setVisibility(View.VISIBLE);
            bt_mode_left.setVisibility(View.VISIBLE);
            bt_mode_right.setVisibility(View.VISIBLE);
            tv_mode.setVisibility(View.VISIBLE);
            tv_tripsDB.setVisibility(View.VISIBLE);
            tv_fusionDB.setVisibility(View.VISIBLE);


        } else {
            tv_distance.setTextSize(20);
            tv_totalHours.setTextSize(20);
            tv_aveSpeed.setTextSize(20);
            tvMaxSpeed.setTextSize(20);
            bt_UP_threshold.setVisibility(View.GONE);
            tv_title2.setVisibility(View.GONE);
            tv_Xaxis.setVisibility(View.GONE);
            tv_Yaxis.setVisibility(View.GONE);
            tv_pith.setVisibility(View.GONE);
            tv_yaw.setVisibility(View.GONE);

            bt_mode_left.setVisibility(View.GONE);
            bt_mode_right.setVisibility(View.GONE);
            tv_mode.setVisibility(View.GONE);
            tvLat.setVisibility(View.GONE);
            tvLon.setVisibility(View.GONE);
            tv_total_Current.setVisibility(View.GONE);
            tvHeading.setVisibility(View.GONE);
            tvAccuracy.setVisibility(View.GONE);
            bt_UP_threshold.setVisibility(View.GONE);
            bt_CLOSE_threshold.setVisibility(View.GONE);
            bt_DOWN_threshold.setVisibility(View.GONE);
            bt_OPEN_threshold.setVisibility(View.GONE);
            tv_threshold_P.setVisibility(View.GONE);
            tv_threshold_R.setVisibility(View.GONE);
            tv_threshold_Y.setVisibility(View.GONE);
            tv_threshold_X.setVisibility(View.GONE);
            bt_Reset_Thresholds.setVisibility(View.GONE);
            textView9.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
            tv_safeAccel.setVisibility(View.GONE);
            tv_safeDesa.setVisibility(View.GONE);
            tv_safeLeft.setVisibility(View.GONE);
            tv_safeRight.setVisibility(View.GONE);
            tv_hardAccel.setVisibility(View.GONE);
            tv_hardDes.setVisibility(View.GONE);
            tv_sharpLeft.setVisibility(View.GONE);
            tv_sharpRight.setVisibility(View.GONE);
            bt_resetFusionDatabase.setVisibility(View.GONE);
            tv_tripsDB.setVisibility(View.GONE);
            tv_fusionDB.setVisibility(View.GONE);


        }


    }


    Location previousLocation = null;
    float distanceTraveled = 0;
    double lat;
    double lon;
    float currentSpeed;
    boolean statusLocation = false;

    Location location1;

    //THIS CLASS PROVIDE THE VARIABLES SPEED lONGITUD AND lATITUD
    private class SpeedTask extends AsyncTask<String, Void, String> {
        Context context;
        float speed = 0.0f;

        double distance;
        LocationManager locationManager;

        public SpeedTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


            return null;

        }

        protected void onPostExecute(String result) {
            tvUnit.setText(unit);
            LocationListener listener = new LocationListener() {
                float filtSpeed;
                float localspeed;


                @Override
                public void onLocationChanged(Location location) {
                    location1 = location;
                    speed = location.getSpeed();
                    statusLocation = true;
                    image_location.setImageResource(R.drawable.ic_baseline_gps_fixed_24);


                    //multiplaer to show the units standar Km/h
                    float multiplier = 3.6f;


                    if (maxSpeed < speed) {
                        maxSpeed = speed;
                    }


                    localspeed = speed * multiplier;
                    /******************************Speed*******************************************/
                    filtSpeed = filter(filtSpeed, localspeed, 2);

                    currentSpeed = filtSpeed;
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(0);


                    if (previousLocation != null) {
                        distanceTraveled += location.distanceTo(previousLocation);//in meters

                    }
                    previousLocation = location;

                    lon = location.getLongitude();
                    lat = location.getLatitude();

                    Log.d("onChange", "Speed " + localspeed + "latitude: " + lat + " longitude: " + lon + " Distance: " + distanceTraveled / 1000);
                    tvSpeed.setText(numberFormat.format(filtSpeed));
                    /************************Distance and MAX speed*******************************/
                    tv_distance.setText("Distance: " + numberFormat.format(+distanceTraveled / 1000.0) + " km");
                    tvMaxSpeed.setText("Max Speed: " + numberFormat.format(maxSpeed * multiplier));


                    if (location.hasAltitude()) {
                        tvAccuracy.setText("AC: " + numberFormat.format(location.getAccuracy()) + " m");
                    } else {
                        tvAccuracy.setText("NIL");
                    }

                    numberFormat.setMaximumFractionDigits(0);

                    // Direction like a compass direction
                    if (location.hasBearing()) {

                        double bearing = location.getBearing();
                        String strBearing = "NIL";
                        if (bearing < 20.0) {
                            strBearing = "North";
                        } else if (bearing < 65.0) {
                            strBearing = "North-East";
                        } else if (bearing < 110.0) {
                            strBearing = "East";
                        } else if (bearing < 155.0) {
                            strBearing = "South-East";
                        } else if (bearing < 200.0) {
                            strBearing = "South";
                        } else if (bearing < 250.0) {
                            strBearing = "South-West";
                        } else if (bearing < 290.0) {
                            strBearing = "West";
                        } else if (bearing < 345.0) {
                            strBearing = "North-West";
                        } else if (bearing < 361.0) {
                            strBearing = "North";
                        }

                        tvHeading.setText(strBearing);
                    } else {
                        tvHeading.setText("NIL");
                    }

                    // setting format latitud and log
                    NumberFormat nf = NumberFormat.getInstance();

                    nf.setMaximumFractionDigits(4);

                    /**Latitud and Longitud*******************************************************/
                    tvLat.setText(nf.format(location.getLatitude()));
                    tvLon.setText(nf.format(location.getLongitude()));
                    // save values just when I starttrip
                    if (!startTripState == false) {
                        Log.d("saveFusionOnLoc", "stateTripState" + startTripState + "coor: " + lat + "," + lon + "tripID" + tripID);
                        saveFusionSensor();
                    }


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderEnabled(String provider) {
                    tvSpeed.setText("STDBY");
                    tvMaxSpeed.setText("NIL");
                    tvLat.setText("LATITUDE");
                    tvLon.setText("LONGITUDE");
                    tvHeading.setText("HEADING");
                    tvAccuracy.setText("ACCURACY");

                }

                @Override
                public void onProviderDisabled(String provider) {
                    tvSpeed.setText("NOFIX");
                    tvMaxSpeed.setText("NOGPS");
                    tvLat.setText("LATITUDE");
                    tvLon.setText("LONGITUDE");
                    tvHeading.setText("HEADING");
                    tvAccuracy.setText("ACCURACY");


                }

            };


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }


        }

        /**
         * Simple recursive filter
         *
         * @param prev Previous value of filter
         * @param curr New input value into filter
         * @return New filtered value
         */
        private float filter(final float prev, final float curr, final int ratio) {
            // If first time through, initialise digital filter with current values
            if (Float.isNaN(prev))
                return curr;
            // If current value is invalid, return previous filtered value
            if (Float.isNaN(curr))
                return prev;
            // Calculate new filtered value
            return (float) (curr / ratio + prev * (1.0 - 1.0 / ratio));
        }


    }

    // VALIDATOR TO CHECK IF GPS IS ENABLE
    private boolean isLocationEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /*************************************FUSIONSENSORS**********************************************/

    // initializing the sensors
    public void initListeners() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    float[] mGravity;
    float[] mMagneticField;
    float xAccelerometer;
    float yAccelerometer;
    float zAccelerometer;
    private float[] accel = new float[3];
    private float[] magnet = new float[3];

    //variable for view port
    private Viewport viewport;
    LineGraphSeries<DataPoint> Xseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Yseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Pseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Rseries = new LineGraphSeries<DataPoint>();

    LineGraphSeries<DataPoint> yUPsafe1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> yUPsafe2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> yDownsafe1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> yDownsafe2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XUPsafe1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XUPsafe2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XDownsafe1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XDownsafe2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> yUPHard = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> yDownHard = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XUPHard = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> XDownHard = new LineGraphSeries<DataPoint>();


    private int pointsPlotted = 10;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (!startTripState == false) {
            //updateValues();
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    mGravity = event.values;
                    xAccelerometer = event.values[0];
                    yAccelerometer = event.values[1];
                    zAccelerometer = event.values[2];
                    calibrateAccelerometer();
                    // copy new accelerometer data into accel array
                    // then calculate new orientation
                    System.arraycopy(event.values, 0, accel, 0, 3);
                    calculateAccMagOrientation();
//                checkReadings();
//                printArray(accel);
//                Log.d("onSensor","mGravity"+ mGravity);
//                Log.d("onSensor","xAccelerometer"+ xAccelerometer);
//                Log.d("onSensor","yAccelerometer"+ yAccelerometer);
//                Log.d("onSensor","zAccelerometer"+ zAccelerometer);


                    break;

                case Sensor.TYPE_GYROSCOPE:
                    // process gyro data
                    gyroFunction(event);

                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    // copy new magnetometer data into magnet array
                    mMagneticField = event.values;
                    System.arraycopy(event.values, 0, magnet, 0, 3);
                    break;
            }


            computeQuaternion();
            //drivingAnalysisWithRoll(xAccCalibrated,newPitchOut,yAccCalibrated,newRollOut);
            //drawGraph(xAccCalibrated, newPitchOut, yAccCalibrated,newRollOut);
            //drivingAnalysisWithYaw(xAccCalibrated,newPitchOut,yAccCalibrated,newYawOut);
            //drawGraph(xAccCalibrated, newPitchOut, yAccCalibrated,newRollOut);
            drawGraphSpecific();

            switch (mode) {
                case 0:
                    drivingAnalysisBoth();
                    break;
                case 1:
                    drivingAnalysisAccelerometer();
                    break;
                case 2:
                    drivingAnalysisPitchRoll();
                    break;


            }


            /**No necesesary just o display the current time runing*/
            long currentTime = System.currentTimeMillis();
            int totalMinutes = (int) (((currentTime - previousTime) / (1000 * 60)) % 60);
            int hours = (int) (((currentTime - previousTime) / (1000 * 60 * 60)) % 24);
            int seconds = (int) ((currentTime - previousTime) / 1000) % 60;
            tv_total_Current.setText(String.valueOf(totalMinutes) + " Min " + String.valueOf(seconds) + " sec");
        }


    }


    public void drawGraphSpecific() {


        tv_Xaxis.setBackgroundColor(Color.MAGENTA);
        tv_Xaxis.setText("XA: " + String.valueOf(xAccelerometer));
        tv_Yaxis.setBackgroundColor(Color.GREEN);
        tv_Yaxis.setText("YA: " + String.valueOf(yAccelerometer));
//        tv_XaxisCali.setBackgroundColor(Color.MAGENTA);
//        tv_XaxisCali.setText("XAccelCAL: "+String.valueOf(xAccCalibrated));
//        tv_YaxisCali.setBackgroundColor(Color.GRAY);
//        tv_YaxisCali.setText("YAccelCAL: "+String.valueOf(yAccCalibrated));
        tv_pith.setBackgroundColor(Color.YELLOW);
        tv_pith.setText("P: " + String.valueOf(newPitchOut));
        tv_yaw.setBackgroundColor(Color.WHITE);
        tv_yaw.setText("R: " + String.valueOf(newRollOut));


        viewport.setMaxX(pointsPlotted);
        viewport.setMinX(pointsPlotted - 100);// set to show the last 100 points

        //update the graph
        pointsPlotted++;

        // to not saturate the memory we have to limit the points to keep it into the memory

        if (pointsPlotted > 1000) {
            pointsPlotted = 1; // reset the variable
            Xseries.resetData(new DataPoint[]{new DataPoint(1, 0)});
            Yseries.resetData(new DataPoint[]{new DataPoint(1, 0)});
            Pseries.resetData(new DataPoint[]{new DataPoint(1, 0)});
            Rseries.resetData(new DataPoint[]{new DataPoint(1, 0)});

            yUPsafe1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            yUPsafe2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            yDownsafe1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            yDownsafe2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XUPsafe1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XUPsafe2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XDownsafe1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XDownsafe2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            yUPHard.resetData(new DataPoint[]{new DataPoint(1, 0)});
            yDownHard.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XUPHard.resetData(new DataPoint[]{new DataPoint(1, 0)});
            XDownHard.resetData(new DataPoint[]{new DataPoint(1, 0)});


        }


        Xseries.appendData(new DataPoint(pointsPlotted, xAccCalibrated), true, pointsPlotted);
        Yseries.appendData(new DataPoint(pointsPlotted, yAccCalibrated), true, pointsPlotted);
        Pseries.appendData(new DataPoint(pointsPlotted, newPitchOut), true, pointsPlotted);
        Rseries.appendData(new DataPoint(pointsPlotted, newRollOut), true, pointsPlotted);


        yUPsafe1.appendData(new DataPoint(pointsPlotted, Y1), true, pointsPlotted);
        yUPsafe2.appendData(new DataPoint(pointsPlotted, Y2), true, pointsPlotted);
        yDownsafe1.appendData(new DataPoint(pointsPlotted, -Y1), true, pointsPlotted);
        yDownsafe2.appendData(new DataPoint(pointsPlotted, -Y2), true, pointsPlotted);
        XUPsafe1.appendData(new DataPoint(pointsPlotted, -X1), true, pointsPlotted);
        XUPsafe2.appendData(new DataPoint(pointsPlotted, -X2), true, pointsPlotted);
        XDownsafe1.appendData(new DataPoint(pointsPlotted, X1), true, pointsPlotted);
        XDownsafe2.appendData(new DataPoint(pointsPlotted, X2), true, pointsPlotted);
        yUPHard.appendData(new DataPoint(pointsPlotted, -0.12), true, pointsPlotted);
        yDownHard.appendData(new DataPoint(pointsPlotted, 0.12), true, pointsPlotted);
        XUPHard.appendData(new DataPoint(pointsPlotted, 0.30), true, pointsPlotted);
        XDownHard.appendData(new DataPoint(pointsPlotted, -0.30), true, pointsPlotted);


    }

    float xPreviousAcc;
    float yPreviousAcc;
    float zPreviousAcc;
    private boolean mInitialized = false;
    float xAccCalibrated = 0f;
    float yAccCalibrated = 0f;
    float zAccCalibrated = 0f;

    // getting accelerometer values and calibrating the accelerometer
    private void calibrateAccelerometer() {
        if (!mInitialized) {
            xPreviousAcc = xAccelerometer;
            yPreviousAcc = yAccelerometer;
            zPreviousAcc = zAccelerometer;
            mInitialized = true;
        } else {
            xAccCalibrated = (xPreviousAcc - xAccelerometer);
            yAccCalibrated = (yPreviousAcc - yAccelerometer);
            zAccCalibrated = (zPreviousAcc - zAccelerometer);
            xPreviousAcc = xAccelerometer;
            yPreviousAcc = yAccelerometer;
            zPreviousAcc = zAccelerometer;
            Log.d("calibrateAccelerometer", "xAccCalibrated" + xAccCalibrated);
            Log.d("calibrateAccelerometer", "yAccCalibrated" + yAccCalibrated);
        }
    }

    boolean writeCheck = false;
    Boolean yAccChange = false;
    Boolean xAccChange = false;
    int count = 1;
    int overYaw = 0;
    int overPitch = 0;
    //counter for quaternion
    int overYawQ = 0;
    int overPitchQ = 0;


    //variables for acceleromneter calibration
    private float[] rotationMatrix = new float[9];
    private float[] accMagOrientation = new float[3];

    //The low-pass filtering of the noisy accelerometer/magnetometer signal accMagOrientation  are
    // orientation angles averaged over time within a constant time window.
    // accelerometer
    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
            //printArray("rotationMatrix",rotationMatrix);
            //printArray("accMagOrientation",accMagOrientation);
            //now after rotationMatrix and it can be defined the position of the phone
        }
    }

    //Gyro

    private boolean initState = true;
    private float[] gyroMatrix = new float[9];
    private float[] gyro = new float[3];
    /**
     * i thing this give the pitch roll and yaw
     */
    private float timestamp;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float[] gyroOrientation = new float[3];

    public void gyroFunction(SensorEvent event) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;

        // initialisation of the gyroscope based rotation matrix
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            System.arraycopy(event.values, 0, gyro, 0, 3);
            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (displayPitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (displayRoll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (displayRoll, displayPitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    public static final float EPSILON = 0.000000001f;

    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }


    float mPitch, mRoll, mYaw;
    Float newPitchOutQ = 0f;
    Float newRollOutQ = 0f;
    Float newYawOutQ = 0f;
    Float getPitchQ = 0f;
    Float getRollQ = 0f;
    Float getYawQ = 0f;

    // computing quaternion values
    private void computeQuaternion() {
        float R[] = new float[9];
        float I[] = new float[9];
        if (mMagneticField != null && mGravity != null) {
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mMagneticField);
            if (success) {
                float[] mOrientation = new float[3];
                float[] mQuaternion = new float[4];
                SensorManager.getOrientation(R, mOrientation);

                SensorManager.getQuaternionFromVector(mQuaternion, mOrientation);

                mYaw = mQuaternion[1]; // orientation contains: azimuth(yaw), pitch and Roll
                mPitch = mQuaternion[2];
                mRoll = mQuaternion[3];

                newPitchOutQ = getPitchQ - mPitch;
                newRollOutQ = getRollQ - mRoll;
                newYawOutQ = getYawQ - mYaw;

                getPitchQ = mPitch;
                getRollQ = mRoll;
                getYawQ = mYaw;

                Log.d("oncomputeQuaternion", "getPitchQ" + getPitchQ);
                Log.d("oncomputeQuaternion", "getRollQ" + getRollQ);
                Log.d("oncomputeQuaternion", "getYawQ" + getYawQ);


            }
        }
    }


    private float[] fusedOrientation = new float[3];
    float pitchOut, rollOut, yawOut;
    Float getPitch = 0f;
    Float getRoll = 0f;
    Float getYaw = 0f;
    // normal - sensor fusion, Q - denotes quaternion
    Float newPitchOut = 0f;
    Float newRollOut = 0f;
    Float newYawOut = 0f;
    float filter_coefficient = 0.45f;// works well

    // sensor fusion values are computed at every 10 sec as initialized earlier
    private class calculateFusedOrientationTask extends TimerTask {
        //float filter_coefficient = 0.50f;

        float oneMinusCoeff = 1.0f - filter_coefficient;

        public void run() {
            // Azimuth
            if (gyroOrientation[0] < -0.5 * Math.PI && accMagOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (filter_coefficient * (gyroOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[0]);
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (accMagOrientation[0] < -0.5 * Math.PI && gyroOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (filter_coefficient * gyroOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + 2.0 * Math.PI));
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                fusedOrientation[0] = filter_coefficient * gyroOrientation[0] + oneMinusCoeff * accMagOrientation[0];

            // Pitch
            if (gyroOrientation[1] < -0.5 * Math.PI && accMagOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (filter_coefficient * (gyroOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[1]);
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (accMagOrientation[1] < -0.5 * Math.PI && gyroOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (filter_coefficient * gyroOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + 2.0 * Math.PI));
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                fusedOrientation[1] = filter_coefficient * gyroOrientation[1] + oneMinusCoeff * accMagOrientation[1];

            // Roll
            if (gyroOrientation[2] < -0.5 * Math.PI && accMagOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (filter_coefficient * (gyroOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[2]);
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (accMagOrientation[2] < -0.5 * Math.PI && gyroOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (filter_coefficient * gyroOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + 2.0 * Math.PI));
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
            } else
                fusedOrientation[2] = filter_coefficient * gyroOrientation[2] + oneMinusCoeff * accMagOrientation[2];

            // Overwrite gyro matrix and orientation with fused orientation to comensate gyro drift
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);

            pitchOut = fusedOrientation[1];
            rollOut = fusedOrientation[2];
            yawOut = fusedOrientation[0];

            // present instance values
            newPitchOut = getPitch - pitchOut;
            newRollOut = getRoll - rollOut;
            newYawOut = getYaw - yawOut;

            // saving values for calibration
            getPitch = pitchOut;
            getRoll = rollOut;
            getYaw = yawOut;
        }
    }


    // final pitch and yaw values
    int finalOverYaw = 0;
    int finalOverPitch = 0;// for 30 sec sensor values reset
    int getFinalOverYaw = 0;
    int getFinalOverPitch = 0;
    int getFinalOverX = 0;
    int getFinalOverY = 0;
    //counter for accelerometer reading
    int overX = 0;
    int overY = 0;

    // sensor values computed for the last 30 sec
    private class ResetSensorValues extends TimerTask {

        @Override
        public void run() {
            finalOverYaw = finalOverYaw - getFinalOverYaw;
            finalOverPitch = finalOverPitch - getFinalOverPitch;
            overX = overX - getFinalOverX;
            overY = overY - getFinalOverY;

            getFinalOverPitch = finalOverPitch;
            getFinalOverYaw = finalOverYaw;
            getFinalOverX = overX;
            getFinalOverY = overY;
            Log.i("ResetSensorValues", "final Pitch : " + finalOverPitch);
            Log.i("ResetSensorValues", "final Yaw : " + finalOverYaw);
            Log.i("ResetSensorValues", "final overX : " + overX);
            Log.i("ResetSensorValues", "final overY : " + overY);
        }
    }


    int finaScoreCounter = 0;
    int SAC = 0;
    int SDC = 0;
    int HAC = 0;
    int HDC = 0;
    int SLC = 0;
    int SRC = 0;
    int SHLC = 0;
    int SHRC = 0;

    boolean SA = false;
    boolean SD = false;
    boolean HA = false;
    boolean HD = false;
    boolean SL = false;
    boolean SR = false;
    boolean SHL = false;
    boolean SHR = false;


    double thresholdUPDOWN = 0;
    double thresholdOPENCLOSE = 0;
    double Y1 = 0;
    double Y2 = 0;
    double X1 = 0;
    double X2 = 0;
    double P1 = 0;
    double P2 = 0;
    double R1 = 0;
    double R2 = 0;


    float scoreTrip = 100;
    float finalScoreTrip;

    public void thresholds() {
        Y1 = 1.3 + thresholdUPDOWN - thresholdOPENCLOSE;
        Y2 = 2.5 + thresholdUPDOWN + thresholdOPENCLOSE;
        X1 = 1.8 + thresholdUPDOWN - thresholdOPENCLOSE;
        X2 = 3.0 + thresholdUPDOWN + thresholdOPENCLOSE;
        P1 = 0.08 + thresholdUPDOWN / 10 - thresholdOPENCLOSE / 10;
        P2 = 0.12 + thresholdUPDOWN / 10 + thresholdOPENCLOSE / 10;
        R1 = 0.10 + thresholdUPDOWN / 10 - thresholdOPENCLOSE / 10;
        R2 = 0.30 + thresholdUPDOWN / 10 + thresholdOPENCLOSE / 10;

        tv_threshold_P.setText("P:" + String.format("%.2f", P1) + "-" + String.format("%.2f", P2));
        tv_threshold_R.setText("R:" + String.format("%.2f", R1) + "-" + String.format("%.2f", R2));
        tv_threshold_Y.setText("Y:" + String.format("%.2f", Y1) + "-" + String.format("%.2f", Y2));
        tv_threshold_X.setText("X:" + String.format("%.2f", X1) + "-" + String.format("%.2f", X2));


    }

    private void ResetThresholds() {
        thresholdUPDOWN = 0;
        thresholdOPENCLOSE = 0;

        Y1 = 1.3 + thresholdUPDOWN - thresholdOPENCLOSE;
        Y2 = 2.5 + thresholdUPDOWN + thresholdOPENCLOSE;
        X1 = 1.8 + thresholdUPDOWN - thresholdOPENCLOSE;
        X2 = 3.0 + thresholdUPDOWN + thresholdOPENCLOSE;
        P1 = 0.08 + thresholdUPDOWN / 10 - thresholdOPENCLOSE / 10;
        P2 = 0.12 + thresholdUPDOWN / 10 + thresholdOPENCLOSE / 10;
        R1 = 0.10 + thresholdUPDOWN / 10 - thresholdOPENCLOSE / 10;
        R2 = 0.30 + thresholdUPDOWN / 10 + thresholdOPENCLOSE / 10;

        tv_threshold_P.setText("P:" + String.format("%.2f", P1) + "-" + String.format("%.2f", P2));
        tv_threshold_R.setText("R:" + String.format("%.2f", R1) + "-" + String.format("%.2f", R2));
        tv_threshold_Y.setText("Y:" + String.format("%.2f", Y1) + "-" + String.format("%.2f", Y2));
        tv_threshold_X.setText("X:" + String.format("%.2f", X1) + "-" + String.format("%.2f", X2));


    }


    public void drivingAnalysisBoth() {

        if (startTripState == true) {

            //Safe Driving
            if (yAccCalibrated > Y1 && yAccCalibrated < Y2) {
                if (newPitchOut < -P1 && newPitchOut > -P2) {
                    SAC++;
                    SA = true;
                    Log.d("drivingAnalysis", "SAC: " + SAC + " yAc: " + yAccCalibrated + " Pi: " + newPitchOut);
                }
            }
            if (yAccCalibrated < -Y1 && yAccCalibrated > -Y2) {
                if (newPitchOut > P1 && newPitchOut < P2) {
                    SDC++;
                    SD = true;
                    Log.d("drivingAnalysis", "SDC: " + SDC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
                }
            }
            if (xAccCalibrated < -X1 && xAccCalibrated > -X2) {
                if (newRollOut > R1 && newRollOut < R2) {
                    SLC++;
                    SL = true;
                    Log.d("drivingAnalysis", "SLC: " + SLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
                }
            }
            if (xAccCalibrated > X1 && xAccCalibrated < X2) {
                if (newRollOut < -R1 && newRollOut > -R2) {
                    SRC++;
                    SR = true;
                    Log.d("drivingAnalysis", "SRC: " + SRC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
                }
            }
            //Hard Driving
            if (yAccCalibrated > Y2) {
                if (newPitchOut < -P2) {
                    HAC++;
                    HA = true;
                    Log.d("drivingAnalysis", "HAC: " + HAC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
                }
            }


            if (yAccCalibrated < -Y2) {
                if (newPitchOut > P2) {
                    HDC++;
                    HD = true;
                    Log.d("drivingAnalysis", "HDC: " + HDC + " yAc: " + yAccCalibrated + "Pi: " + newPitchOut);
                }
            }
            if (xAccCalibrated < -X2) {
                if (newRollOut > R2) {
                    SHLC++;
                    SHL = true;
                    Log.d("drivingAnalysis", "SHLC: " + SHLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
                }
            }
            if (xAccCalibrated > X2) {
                if (newRollOut < -R2) {
                    SHRC++;
                    SHR = true;
                    Log.d("drivingAnalysis", "SHRC: " + SHRC + " XAc: " + xAccCalibrated + " RO: " + newRollOut);
                }
            }

            // to create the the google maps marks
            if (SA == true || SD == true || HA == true || HD == true || SL == true || SR == true || SHL == true || SHR == true) {
                saveFusionSensor();
                SA = false;
                SD = false;
                HA = false;
                HD = false;
                SL = false;
                SR = false;
                SHL = false;
                SHR = false;
            }

        }

        //tv_finalScore.setText("FSC: "+finaScoreCounter);
        tv_safeAccel.setText("SAC: " + SAC);
        tv_safeDesa.setText("SDC: " + SDC);
        tv_safeLeft.setText("SLC: " + SLC);
        tv_safeRight.setText("SRC: " + SRC);
        tv_hardAccel.setText(" HAC: " + HAC);
        tv_hardDes.setText(" HDC: " + HDC);
        tv_sharpLeft.setText(" SHLC: " + SHLC);
        tv_sharpRight.setText(" SHRC: " + SHRC);


    }

    public void drivingAnalysisAccelerometer() {

        if (startTripState == true) {

            //Safe Driving
            if (yAccCalibrated > Y1 && yAccCalibrated < Y2) {
                SAC++;
                SA = true;
                Log.d("drivingAnalysis", "SAC: " + SAC + " yAc: " + yAccCalibrated + " Pi: " + newPitchOut);
            }
            if (yAccCalibrated < -Y1 && yAccCalibrated > -Y2) {
                SDC++;
                SD = true;
                Log.d("drivingAnalysis", "SDC: " + SDC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
            }
            if (xAccCalibrated < -X1 && xAccCalibrated > -X2) {
                SLC++;
                SL = true;
                Log.d("drivingAnalysis", "SLC: " + SLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }
            if (xAccCalibrated > X1 && xAccCalibrated < X2) {
                SRC++;
                SR = true;
                Log.d("drivingAnalysis", "SRC: " + SRC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }
            //Hard Driving
            if (yAccCalibrated > Y2) {
                HAC++;
                HA = true;
                Log.d("drivingAnalysis", "HAC: " + HAC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
            }


            if (yAccCalibrated < -Y2) {
                HDC++;
                HD = true;
                Log.d("drivingAnalysis", "HDC: " + HDC + " yAc: " + yAccCalibrated + "Pi: " + newPitchOut);
            }
            if (xAccCalibrated < -X2) {
                SHLC++;
                SHL = true;
                Log.d("drivingAnalysis", "SHLC: " + SHLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }
            if (xAccCalibrated > X2) {
                SHRC++;
                SHR = true;
                Log.d("drivingAnalysis", "SHRC: " + SHRC + " XAc: " + xAccCalibrated + " RO: " + newRollOut);
            }

            // to create the the google maps marks
            if (SA == true || SD == true || HA == true || HD == true || SL == true || SR == true || SHL == true || SHR == true) {
                saveFusionSensor();
                SA = false;
                SD = false;
                HA = false;
                HD = false;
                SL = false;
                SR = false;
                SHL = false;
                SHR = false;
            }

        }

        //tv_finalScore.setText("FSC: "+finaScoreCounter);
        tv_safeAccel.setText("SAC: " + SAC);
        tv_safeDesa.setText("SDC: " + SDC);
        tv_safeLeft.setText("SLC: " + SLC);
        tv_safeRight.setText("SRC: " + SRC);
        tv_hardAccel.setText(" HAC: " + HAC);
        tv_hardDes.setText(" HDC: " + HDC);
        tv_sharpLeft.setText(" SHLC: " + SHLC);
        tv_sharpRight.setText(" SHRC: " + SHRC);


    }

    public void drivingAnalysisPitchRoll() {

        if (startTripState == true) {

            //Safe Driving

            if (newPitchOut < -P1 && newPitchOut > -P2) {
                SAC++;
                SA = true;
                Log.d("drivingAnalysis", "SAC: " + SAC + " yAc: " + yAccCalibrated + " Pi: " + newPitchOut);
            }


            if (newPitchOut > P1 && newPitchOut < P2) {
                SDC++;
                SD = true;
                Log.d("drivingAnalysis", "SDC: " + SDC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
            }


            if (newRollOut > R1 && newRollOut < R2) {
                SLC++;
                SL = true;
                Log.d("drivingAnalysis", "SLC: " + SLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }


            if (newRollOut < -R1 && newRollOut > -R2) {
                SRC++;
                SR = true;
                Log.d("drivingAnalysis", "SRC: " + SRC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }

            //Hard Driving

            if (newPitchOut < -P2) {
                HAC++;
                HA = true;
                Log.d("drivingAnalysis", "HAC: " + HAC + " yAc: " + yAccCalibrated + " Pi:" + newPitchOut);
            }


            if (newPitchOut > P2) {
                HDC++;
                HD = true;
                Log.d("drivingAnalysis", "HDC: " + HDC + " yAc: " + yAccCalibrated + "Pi: " + newPitchOut);
            }


            if (newRollOut > R2) {
                SHLC++;
                SHL = true;
                Log.d("drivingAnalysis", "SHLC: " + SHLC + " xAc: " + xAccCalibrated + " RO: " + newRollOut);
            }


            if (newRollOut < -R2) {
                SHRC++;
                SHR = true;
                Log.d("drivingAnalysis", "SHRC: " + SHRC + " XAc: " + xAccCalibrated + " RO: " + newRollOut);
            }


            // to create the the google maps marks
            if (SA == true || SD == true || HA == true || HD == true || SL == true || SR == true || SHL == true || SHR == true) {
                saveFusionSensor();
                SA = false;
                SD = false;
                HA = false;
                HD = false;
                SL = false;
                SR = false;
                SHL = false;
                SHR = false;
            }

        }

        //tv_finalScore.setText("FSC: "+finaScoreCounter);
        tv_safeAccel.setText("SAC: " + SAC);
        tv_safeDesa.setText("SDC: " + SDC);
        tv_safeLeft.setText("SLC: " + SLC);
        tv_safeRight.setText("SRC: " + SRC);
        tv_hardAccel.setText(" HAC: " + HAC);
        tv_hardDes.setText(" HDC: " + HDC);
        tv_sharpLeft.setText(" SHLC: " + SHLC);
        tv_sharpRight.setText(" SHRC: " + SHRC);


    }


    public void printArray(String name, float[] array) {

        for (int v = 0; v < accel.length; v++) {
            Log.d("printarray", "pos " + v + " " + name + ": " + array[v]);
        }

    }


    boolean startTripState = false;
    long EndTime, StartTime;
    long timeTrip;
    double elapse;
    double aveSpeedKM;

    Timer t = new Timer();


    public void startTrip() {
        //----------//get location of the destination ----------------------------------------------

        Log.d("StartTrip", "statusLocation" + statusLocation);
        if (!statusLocation == false) {

            if (startTripState == false) {
                Trip trip = new Trip();
                //DURING THE TRIP
                // during the start of a trip, values are initialized
                // change the button to display "End" to end the trip
                startTripState = true;

                bt_startTrip.setText("End Trip");
                bt_startTrip.setBackgroundColor(getResources().getColor(R.color.StateButton));
                previousLocation = null;
                StartTime = System.currentTimeMillis();
                finalScoreTrip = 0;

                if (lat == 0.0 && lon == 0.0) {
                    Log.d("locationExeption", "**********its happening***************" + "lat: " + lat + "Lon" + lon);
                    Toast.makeText(getContext(), "Please wait while trip is creating", Toast.LENGTH_LONG).show();
                    /**this while is to solve the problem of 0.0 coordenates I can not explaind why is producing 0.0 I guess that inside the function reset the coordinates
                     every time that change coordinates*/
                    do {
                        Log.d("locationExeption", "inside do while save Start" + "lat: " + lat + "Lon" + lon);

                        lon = location1.getLongitude();
                        lat = location1.getLatitude();

                    } while (lat == 0.0 && lon == 0.0);

                }


                saveTripInitial(userId, trip);
                //trip.getTripId();
                Log.d("saveTripInitial", "TripID" + trip.getTripId());


                // making changes to the UI
                Log.d("Score", "finalScoreTrip" + finalScoreTrip);
            } else {
                // END OF THE TRIP
                startTripState = false;
                // Time,Score,AverageScore, is compute after finish trip
                bt_startTrip.setText("Start Trip");
                bt_startTrip.setBackgroundColor(getResources().getColor(R.color.blue_sky_500));
                /*****************************calculating time*********************************/
                EndTime = System.currentTimeMillis();
                timeTrip = EndTime - StartTime;
                double distanceKM = distanceTraveled / 1000.0;// it is show in real-Time
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String time = simpleDateFormat.format(new Date(timeTrip));
                elapse = (double) (timeTrip / 1000) / 60;// this is the measure i need
                float totalTimeTraveledMin = (float) (((EndTime - StartTime) / (1000 * 60)) % 60);
                int hours = (int) (((EndTime - StartTime) / (1000 * 60 * 60)) % 24);
                int seconds = (int) ((EndTime - StartTime) / 1000) % 60;

                //tv_totalHours.setText(String.valueOf(totalTimeTraveledMin)+" Minutes "+String.valueOf(seconds)+" seconds");
                tv_totalHours.setText("Total Time Trip: " + String.format("%.2f", elapse) + "Min");

                float reductionFactor = 5 * HAC + 5 * HDC + 5 * SHLC + 5 * SHRC;
                finalScoreTrip = scoreTrip - reductionFactor;
                if (finalScoreTrip < 0) {
                    finalScoreTrip = 0;
                }
                Log.d("Score", "reductionFactor" + reductionFactor);
                Log.d("Score", "finalScoreTrip" + finalScoreTrip);
                tv_finalScore.setText(String.valueOf((int) finalScoreTrip));
                SAC = 0;
                SDC = 0;
                HAC = 0;
                HDC = 0;
                SLC = 0;
                SRC = 0;
                SHLC = 0;
                SHRC = 0;


                aveSpeedKM = distanceKM / totalTimeTraveledMin;

                if (Double.isInfinite(aveSpeedKM)) {
                    aveSpeedKM = 0;
                }

                tv_aveSpeed.setText("Avg Speed: "+ String.format("%.1f", aveSpeedKM));

                if (lat == 0.0 && lon == 0.0) {
                    Log.d("locationExeption", "**********its happening***************" + "lat: " + lat + "Lon" + lon);
                    Toast.makeText(getContext(), "Please wait while is Saving ......", Toast.LENGTH_LONG).show();
                    /**this while is to solve the problem of 0.0 coordinates I can not explained why is producing 0.0 I guess that inside the function reset the coordinates
                     every time that change coordinates*/
                    do {
                        Log.d("locationExeption", "inside do while save end" + "lat: " + lat + "Lon" + lon);

                        lon = location1.getLongitude();
                        lat = location1.getLatitude();

                    } while (lat == 0.0 && lon == 0.0);

                }


                saveTripEnd();


                /**SAVE VALUES fuction*///Save values
                //getting average score of the trip
                //scoreArrayList = new ScoreArrayList(scoreList);
                //avgScore = scoreArrayList.getAverage();
                //double result = Math.round(avgScore * 100) / 100.0;

                Toast.makeText(getContext(), "Trip End final Trip Score: ", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getContext(), "Location is not still enable. Please wait", Toast.LENGTH_LONG).show();
        }

    }


    public void saveTripInitial(long userId, Trip trip) {
        trip.setUserCreatorId(userId);
        trip.setStartLocationLAT(lat);
        trip.setStartLocationLON(lon);

        trip.setStartLocationName(getAddressLocationName(lat, lon));
        trip.setStartDate(StartTime);
        trip.setStartTime(StartTime);
        dao.insertTrip(trip);
        List<Trip> tripList = dao.getAllTripsByUser(userId);
        Trip lastTrip = tripList.get(dao.getAllTripsByUser(userId).size() - 1);

        setTripID(lastTrip.getTripId());
        Log.d("SaveTripInitial", "tripID" + lastTrip.getTripId());


    }

    public void saveTripEnd() {


        Trip trip = dao.getTripById(tripID);
        //bring 0 require the last number list could be de las
        trip.setEndLocationLAT(lat);
        trip.setEndLocationLON(lon);

        trip.setEndLocationName(getAddressLocationName(lat, lon));
        trip.setKilometers((float) (distanceTraveled / 1000.0)); // save it in Km/s
        trip.setTimeTrip(elapse);
        trip.setScoreTrip(finalScoreTrip);
        trip.setEndDate(EndTime);
        trip.setEndTime(EndTime);
        trip.setAveSpeed(1 + aveSpeedKM * 100);
        dao.updateTrip(trip);
        lat = 0;
        lon = 0;
        distanceTraveled = 0;
        elapse = 0;
        finalScoreTrip = 0;
        EndTime = 0;
        aveSpeedKM = 0;

    }

    public void saveFusionSensor() {


        FusionSensor fusionSensor = new FusionSensor();

        fusionSensor.setTripCreatorId(tripID);
        fusionSensor.setxAcc(xAccCalibrated);
        fusionSensor.setyAcc(yAccCalibrated);
        //fusionSensor.setzAcc();
        fusionSensor.setPitch(newPitchOut);
        fusionSensor.setYaw(newYawOut);
        fusionSensor.setCarSpeed(currentSpeed);
        //fusionSensor.setGoogleCurSpeed();
        fusionSensor.setCurLocationLAT(lat);
        fusionSensor.setCurLocationLON(lon);
        //fusionSensor.setValSpeed();
        fusionSensor.setSafeAcc(SA);
        fusionSensor.setSafeDes(SD);
        fusionSensor.setSafeLeft(SL);
        fusionSensor.setSafeRight(SR);
        fusionSensor.setHardAcc(HA);
        fusionSensor.setHardDes(HD);
        fusionSensor.setSharpLeft(SHL);
        fusionSensor.setSharpRight(SHR);


        //user.setPicture;
        dao.insertFusionSensor(fusionSensor);

    }

    Geocoder geocoder;

    private List<Address> findGeocoder(Double lat, Double lon) {

        final int maxResults = 5;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, maxResults);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    private String getAddressLocationName(Double lat, Double lon) {

        geocoder = new Geocoder(getContext());
        List<Address> geoResult = findGeocoder(lat, lon);

        Address thisAddress = geoResult.get(0);
        return String.valueOf(thisAddress.getAddressLine(0));

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
