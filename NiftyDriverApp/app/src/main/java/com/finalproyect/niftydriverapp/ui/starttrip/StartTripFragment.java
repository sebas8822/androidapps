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
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartTripFragment extends Fragment implements SensorEventListener {


    private TextView tvSpeed, tvUnit, tvLat, tvLon, tvAccuracy, tvHeading, tvMaxSpeed,
            tv_Xaxis, tv_Yaxis, tv_distance, tv_title2, tv_title1, textView9, textView8,
            tv_totalHours, tv_total_Current, tv_aveSpeed, tv_finalScore, tv_safeAccel,
            tv_safeDesa, tv_safeLeft, tv_safeRight, tv_hardAccel, tv_hardDes, tv_sharpLeft,
            tv_sharpRight, tv_fusionDB, tv_tripsDB, tv_threshold_Y, tv_threshold_X, tv_xCal, tv_yCal;

    private LinearLayout Sensor_layaut, graph_layout, Start_layout, thresholds_layout;


    private Button bt_startTrip, bt_resetFusionDatabase, bt_UP_threshold, bt_DOWN_threshold, bt_OPEN_threshold, bt_CLOSE_threshold, bt_Reset_Thresholds;
    private Switch switch_DEV;
    private ImageView image_location;

    private static final String unit = "km/h";


    private double maxSpeed = -100.0;
    private SensorManager mSensorManager = null;
    long previousTime = 0;
    private Timer fuseTimer = new Timer();
    public static final int TIME_CONSTANT = 10;// to control the task


    // to set up USERID and TRIP databases purposes and control data flow and switch state
    public void setUserid(long userid) {
        this.userId = userid;
    }

    private long userId;

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    private long tripID;

    public void setSwitchDevState(boolean switchDevState) {
        this.switchDevState = switchDevState;
    }

    boolean switchDevState;


    //Init Sharepreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //Init Databases
    AppDatabase db = AppDatabase.getDbInstance(getContext());
    DAO dao = db.driverDao();

    private View view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        //Init shared preferences and save in th elocal variables
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();
        long userid = sp.getLong("userId", 0);
        boolean switchDevState = sp.getBoolean("switchDevState", false);
        setUserid(userid);
        setSwitchDevState(switchDevState);

        // Init fragment
        view = inflater.inflate(R.layout.start_trip_test_fragment_dev, container, false);

        Sensor_layaut = (LinearLayout) view.findViewById(R.id.Sensor_layaut);
        graph_layout = (LinearLayout) view.findViewById(R.id.graph_layout);
        Start_layout = (LinearLayout) view.findViewById(R.id.Start_layout);
        thresholds_layout = (LinearLayout) view.findViewById(R.id.thresholds_layout);

        //varaibles
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


        //Buttons
        bt_Reset_Thresholds = (Button) view.findViewById(R.id.bt_Reset_Thresholds);
        bt_UP_threshold = (Button) view.findViewById(R.id.bt_UP_threshold);
        bt_DOWN_threshold = (Button) view.findViewById(R.id.bt_DOWN_threshold);
        bt_OPEN_threshold = (Button) view.findViewById(R.id.bt_OPEN_threshold);
        bt_CLOSE_threshold = (Button) view.findViewById(R.id.bt_CLOSE_threshold);
        bt_resetFusionDatabase = (Button) view.findViewById(R.id.bt_resetFusionDatabase);
        bt_startTrip = (Button) view.findViewById(R.id.bt_startTrip);


        //Editable Thresholds
        thresholds();
        //Set the current view
        setVisibility(switchDevState);

        //Reset Thresholds
        bt_Reset_Thresholds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetThresholds();
            }
        });

        //Increase Thresholds
        bt_UP_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdUPDOWN = thresholdUPDOWN + .1;
                thresholds();
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " Y1: " + Y1 + " Y2: " + Y2);
                Log.d("thresholds", "UP: " + thresholdUPDOWN + " X1: " + X1 + " X2: " + X2);

            }
        });

        //Reduce Thresholds
        bt_DOWN_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdUPDOWN = thresholdUPDOWN - .1;

                thresholds();
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + "Y1: " + Y1 + "Y2: " + Y2);
                Log.d("thresholds", "DOWN: " + thresholdUPDOWN + " X1: " + X1 + " X2: " + X2);

            }
        });

        //OPEN range Thresholds  more sensible
        bt_OPEN_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdOPENCLOSE = thresholdOPENCLOSE + .1;

                thresholds();
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " Y1: " + Y1 + " Y2: " + Y2);
                Log.d("thresholds", "OPEN: " + thresholdOPENCLOSE + " X1: " + X1 + " X2: " + X2);

            }
        });

        //Close range Thresholds  less sensible
        bt_CLOSE_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thresholdOPENCLOSE = thresholdOPENCLOSE - .1;

                thresholds();
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + "Y1: " + Y1 + "Y2: " + Y2);
                Log.d("thresholds", "CLOSE: " + thresholdOPENCLOSE + " X1: " + X1 + " X2: " + X2);

            }
        });

        //Close range Thresholds  less sensible
        bt_resetFusionDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dao.deleteAllFusionSensor();
                dao.deleteAllTrip();
                tv_fusionDB.setText(String.valueOf(dao.getAllFusionSensor().size()));
                tv_tripsDB.setText(String.valueOf(dao.getAllTripsByUser(userId).size()));


            }
        });

        //Start Trip Function
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip();
            }
        });


        //Developerp State Testing
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


        //FOR SAVE REFERENCES
        previousTime = System.currentTimeMillis();


        //SET UP UNITS PREFERENCES - can be change in the future
        tvUnit.setText(unit);


        // Check if the services are available ASK TO THE USER TO ENABLE
        if (!this.isLocationEnabled(getContext())) {
            //show dialog if Location Services is not enabled
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("GPS not found");  // GPS not found
            builder.setMessage("This app requires GPS or Location Service.\nWould you like to enable Location Service now?"); // Want to enable?
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    getActivity().startActivity(intent);
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
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        GraphView graph2 = (GraphView) view.findViewById(R.id.graph2);
        graph2.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph2.getGridLabelRenderer().setVerticalLabelsVisible(false);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);

        viewport2 = graph2.getViewport();
        viewport2.setScrollable(true);
        viewport2.setXAxisBoundsManual(true);
        viewport2.setYAxisBoundsManual(true);

        viewport2.setBackgroundColor(Color.BLACK);


        target.setColor(Color.YELLOW);
        target.setThickness(50);
        graph2.addSeries(target);
        xcal.setColor(Color.RED);
        xcal.setThickness(30);
        graph2.addSeries(xcal);
        ycal.setColor(Color.GREEN);
        ycal.setThickness(30);
        graph2.addSeries(ycal);


        Xseries.setColor(Color.MAGENTA);
        graph.addSeries(Xseries);
        Yseries.setColor(Color.GREEN);
        graph.addSeries(Yseries);


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


        /**Init Sensors*/// get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        initListenerAccelerometer();


        // analysing behavior every 1 sec
        fuseTimer.scheduleAtFixedRate(new DrivingAnalysisAccelerometer(), 100, 250);


        //resetting the sensor values every 30 sec
        // fuseTimer.scheduleAtFixedRate(new ResetSensorValues(), 1000, 30000);


        new SpeedTask(getActivity()).execute("String");

        return view;
    }


    private void setVisibility(boolean state) {


        if (state == true) {


            tv_distance.setTextSize(10);
            tv_totalHours.setTextSize(10);
            tv_aveSpeed.setTextSize(10);

            tvMaxSpeed.setTextSize(10);
            Sensor_layaut.setVisibility(View.VISIBLE);
            graph_layout.setVisibility(View.VISIBLE);
            Start_layout.setVisibility(View.VISIBLE);
            Start_layout.setOrientation(LinearLayout.HORIZONTAL);
            thresholds_layout.setVisibility(View.VISIBLE);


            bt_UP_threshold.setVisibility(View.VISIBLE);
            tv_title2.setVisibility(View.VISIBLE);
            tv_Xaxis.setVisibility(View.VISIBLE);
            tv_Yaxis.setVisibility(View.VISIBLE);


            tvLat.setVisibility(View.VISIBLE);
            tvLon.setVisibility(View.VISIBLE);
            tv_total_Current.setVisibility(View.VISIBLE);
            tvHeading.setVisibility(View.VISIBLE);
            tvAccuracy.setVisibility(View.VISIBLE);
            bt_UP_threshold.setVisibility(View.VISIBLE);
            bt_CLOSE_threshold.setVisibility(View.VISIBLE);
            bt_DOWN_threshold.setVisibility(View.VISIBLE);
            bt_OPEN_threshold.setVisibility(View.VISIBLE);

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


            tv_tripsDB.setVisibility(View.VISIBLE);
            tv_fusionDB.setVisibility(View.VISIBLE);

            //keep out from dev mode
            bt_resetFusionDatabase.setVisibility(View.GONE);


        } else {


            Sensor_layaut.setVisibility(View.GONE);
            graph_layout.setVisibility(View.GONE);
            thresholds_layout.setVisibility(View.GONE);

            Start_layout.setOrientation(LinearLayout.VERTICAL);


            tv_distance.setTextSize(20);
            tv_totalHours.setTextSize(20);
            tv_aveSpeed.setTextSize(20);
            tvMaxSpeed.setTextSize(20);
            bt_UP_threshold.setVisibility(View.GONE);
            tv_title2.setVisibility(View.GONE);
            tv_Xaxis.setVisibility(View.GONE);
            tv_Yaxis.setVisibility(View.GONE);


            tvLat.setVisibility(View.GONE);
            tvLon.setVisibility(View.GONE);
            tv_total_Current.setVisibility(View.GONE);
            tvHeading.setVisibility(View.GONE);
            tvAccuracy.setVisibility(View.GONE);
            bt_UP_threshold.setVisibility(View.GONE);
            bt_CLOSE_threshold.setVisibility(View.GONE);
            bt_DOWN_threshold.setVisibility(View.GONE);
            bt_OPEN_threshold.setVisibility(View.GONE);

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


    /*************************************GPS**********************************************/

    // make the variables localy
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


        LocationManager locationManager;

        public SpeedTask(Context context) {
            this.context = context;
        }

        //init location service once it is is initialize
        @Override
        protected String doInBackground(String... params) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            return null;
        }

        protected void onPostExecute(String result) {
            tvUnit.setText(unit);
            LocationListener listener = new LocationListener() {
                float filtSpeed;
                float localSpeed;


                @Override
                public void onLocationChanged(Location location) {
                    location1 = location;
                    speed = location.getSpeed();
                    statusLocation = true;
                    image_location.setImageResource(R.drawable.ic_baseline_gps_fixed_24);


                    /******************************Speed and Max speed*****************************************/
                    //Speed  = Distance / time  conversion factor 36000/1000
                    float conveFactorKM = 3.6f;
                    localSpeed = speed * conveFactorKM;


                    //Speed filtered
                    filtSpeed = filter(filtSpeed, localSpeed, 3);

                    currentSpeed = filtSpeed;

                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(0);
                    Log.d("onChange", "Speed " + currentSpeed + "latitude: " + lat + " longitude: " + lon + " Distance: " + distanceTraveled / 1000);
                    tvSpeed.setText(numberFormat.format(currentSpeed));


                    if (maxSpeed < speed) {
                        maxSpeed = speed;
                    }

                    tvMaxSpeed.setText("Max Speed: " + numberFormat.format(maxSpeed * conveFactorKM));

                    /**********************Distance - latitude and longitude***********************/
                    // setting format latitud and log
                    NumberFormat nf = NumberFormat.getInstance();

                    nf.setMaximumFractionDigits(4);
                    // Accumulator dor distance traveled on change location
                    if (previousLocation != null) {
                        distanceTraveled += location.distanceTo(previousLocation);//in meters

                    }
                    previousLocation = location;// location bring al the values of current location
                    Log.d("onChangeTest", "location " + location);
                    lon = location.getLongitude();
                    lat = location.getLatitude();

                    tv_distance.setText("Distance: " + numberFormat.format(+distanceTraveled / 1000.0) + " km");
                    tvLat.setText(nf.format(location.getLatitude()));
                    tvLon.setText(nf.format(location.getLongitude()));

                    /************************Altitud*******************************/

                    if (location.hasAltitude()) {
                        tvAccuracy.setText("AC: " + numberFormat.format(location.getAccuracy()) + " m");
                    } else {
                        tvAccuracy.setText("NIL");
                    }

                    numberFormat.setMaximumFractionDigits(0);

                    // Direction like a compass direction show if is avaialble
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

            //  ask to the system if GPS is available
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
         * https://9to5answer.com/location-getspeed-update
         *
         * @param prev Previous value of filter
         * @param curr New input value into filter
         * @return New filtered value
         */
        private float filter(final float prev, final float curr, final int ratio) {
            // If is used first time, use current
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
    public void initListenerAccelerometer() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);//there is a big difference using a delay fast


    }

    public void initListenerMagnometer() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    float[] mGravity;
    float[] mMagneticField;
    float xAccelerometer;
    float yAccelerometer;
    float zAccelerometer;
    float xAccelerometer2;
    float yAccelerometer2;
    float zAccelerometer2;
    private float[] accel = new float[3];
    private float[] magnet = new float[3];

    //variable for view port
    private Viewport viewport;
    private Viewport viewport2;
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
    LineGraphSeries<DataPoint> pUPHard1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> pDownHard1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> pUPHard2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> pDownHard2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> rUPHard1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> rDownHard1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> rUPHard2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> rDownHard2 = new LineGraphSeries<DataPoint>();

    LineGraphSeries<DataPoint> xcal = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> ycal = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> target = new LineGraphSeries<DataPoint>();

    private int pointsPlotted = 10;
    private int pointsPlotted2 = 10;

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


//                Log.d("onSensor","mGravity"+ mGravity);
//                Log.d("onSensor","xAccelerometer"+ xAccelerometer);
//                Log.d("onSensor","yAccelerometer"+ yAccelerometer);
//                Log.d("onSensor","zAccelerometer"+ zAccelerometer);


                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    // copy new magnetometer data into magnet array
                    mMagneticField = event.values;
                    System.arraycopy(event.values, 0, magnet, 0, 3);
                    break;


            }


            drawGraphSpecific();
            new DrivingAnalysisAccelerometer();
            setCounters();


            /**No necesesary just o display the current time runing*/
            long currentTime = System.currentTimeMillis();
            int totalMinutes = (int) (((currentTime - previousTime) / (1000 * 60)) % 60);
            //int hours = (int) (((currentTime - previousTime) / (1000 * 60 * 60)) % 24);// just in case
            int seconds = (int) ((currentTime - previousTime) / 1000) % 60;
            tv_total_Current.setText(String.valueOf(totalMinutes) + " Min " + String.valueOf(seconds) + " sec");
        } else {


            xAccelerometer2 = event.values[0];
            yAccelerometer2 = event.values[1];
            //calibrateAccelerometer();
            viewport2.setMaxY(9);
            viewport2.setMinY(-9);

            //update the graph
            pointsPlotted2++;

            Log.d("Calibrator", "x: " + xAccelerometer2);
            Log.d("Calibrator", "Y: " + yAccelerometer2);
            if (pointsPlotted2 > 1000) {
                pointsPlotted2 = 1; // reset the variable
                xcal.resetData(new DataPoint[]{new DataPoint(0, 0)});
                ycal.resetData(new DataPoint[]{new DataPoint(0, 0)});
                target.resetData(new DataPoint[]{new DataPoint(0, 1)});
            }
            xcal.appendData(new DataPoint(pointsPlotted2, (int) xAccelerometer2), true, pointsPlotted2);
            ycal.appendData(new DataPoint(pointsPlotted2, (int) yAccelerometer2), true, pointsPlotted2);
            target.appendData(new DataPoint(pointsPlotted2, 0), true, pointsPlotted2);


        }


    }

    /**
     * Need to be checked
     */


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


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /********************************Draw graphs***********************************************/
    // draw the Graph
    public void drawGraphSpecific() {


        tv_Xaxis.setBackgroundColor(Color.MAGENTA);
        tv_Xaxis.setText("XA: " + String.valueOf(xAccelerometer));
        tv_Yaxis.setBackgroundColor(Color.GREEN);
        tv_Yaxis.setText("YA: " + String.valueOf(yAccelerometer));


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
            pUPHard1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            pDownHard1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            pUPHard2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            pDownHard2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            rUPHard1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            rDownHard1.resetData(new DataPoint[]{new DataPoint(1, 0)});
            rUPHard2.resetData(new DataPoint[]{new DataPoint(1, 0)});
            rDownHard2.resetData(new DataPoint[]{new DataPoint(1, 0)});


        }


        plotYandX();


    }

    public void plotYandX() {
        Xseries.appendData(new DataPoint(pointsPlotted, xAccCalibrated), true, pointsPlotted);
        Yseries.appendData(new DataPoint(pointsPlotted, yAccCalibrated), true, pointsPlotted);

        yUPsafe1.appendData(new DataPoint(pointsPlotted, Y1), true, pointsPlotted);
        yUPsafe2.appendData(new DataPoint(pointsPlotted, Y2), true, pointsPlotted);
        yDownsafe1.appendData(new DataPoint(pointsPlotted, -Y1), true, pointsPlotted);
        yDownsafe2.appendData(new DataPoint(pointsPlotted, -Y2), true, pointsPlotted);
        XUPsafe1.appendData(new DataPoint(pointsPlotted, -X1), true, pointsPlotted);
        XUPsafe2.appendData(new DataPoint(pointsPlotted, -X2), true, pointsPlotted);
        XDownsafe1.appendData(new DataPoint(pointsPlotted, X1), true, pointsPlotted);
        XDownsafe2.appendData(new DataPoint(pointsPlotted, X2), true, pointsPlotted);

    }


    /**********************************Algorithm*******************************************************/
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


    float scoreTrip = 100;
    float finalScoreTrip;

    public void thresholds() {
        Y1 = 1.3 + thresholdUPDOWN - thresholdOPENCLOSE;
        Y2 = 2.5 + thresholdUPDOWN + thresholdOPENCLOSE;
        X1 = 1.8 + thresholdUPDOWN - thresholdOPENCLOSE;
        X2 = 3.0 + thresholdUPDOWN + thresholdOPENCLOSE;


        tv_threshold_Y.setText("Y:" + String.format("%.2f", Y1) + "-" + String.format("%.2f", Y2));
        tv_threshold_X.setText("X:" + String.format("%.2f", X1) + "-" + String.format("%.2f", X2));


    }

    private void ResetThresholds() {
        thresholdUPDOWN = 0;
        thresholdOPENCLOSE = 0;
        Y1 = 0;
        Y2 = 0;
        X1 = 0;
        X2 = 0;

        Y1 = 1.3 + thresholdUPDOWN - thresholdOPENCLOSE;
        Y2 = 2.5 + thresholdUPDOWN + thresholdOPENCLOSE;
        X1 = 1.8 + thresholdUPDOWN - thresholdOPENCLOSE;
        X2 = 3.0 + thresholdUPDOWN + thresholdOPENCLOSE;


        tv_threshold_Y.setText("Y:" + String.format("%.2f", Y1) + "-" + String.format("%.2f", Y2));
        tv_threshold_X.setText("X:" + String.format("%.2f", X1) + "-" + String.format("%.2f", X2));


    }


    //Driving behaviour using just acceleromter
    public class DrivingAnalysisAccelerometer extends TimerTask {

        @Override
        public void run() {
            if (startTripState == true) {

                //Safe Driving
                if (yAccCalibrated > Y1 && yAccCalibrated < Y2) {
                    SAC++;
                    SA = true;
                    Log.d("drivingAnalysis", "SAC: " + SAC + " yAc: " + yAccCalibrated);
                }
                if (yAccCalibrated < -Y1 && yAccCalibrated > -Y2) {
                    SDC++;
                    SD = true;
                    Log.d("drivingAnalysis", "SDC: " + SDC + " yAc: " + yAccCalibrated);
                }
                if (xAccCalibrated < -X1 && xAccCalibrated > -X2) {
                    SLC++;
                    SL = true;
                    Log.d("drivingAnalysis", "SLC: " + SLC + " xAc: " + xAccCalibrated);
                }
                if (xAccCalibrated > X1 && xAccCalibrated < X2) {
                    SRC++;
                    SR = true;
                    Log.d("drivingAnalysis", "SRC: " + SRC + " xAc: " + xAccCalibrated);
                }
                //Hard Driving
                if (yAccCalibrated > Y2) {
                    HAC++;
                    HA = true;
                    playSound();
                    Log.d("drivingAnalysis", "HAC: " + HAC + " yAc: " + yAccCalibrated);
                    fuseTimer.scheduleAtFixedRate(new DrivingAnalysisAccelerometer(), 100, 250);

                }


                if (yAccCalibrated < -Y2) {
                    HDC++;
                    HD = true;
                    playSound();
                    Log.d("drivingAnalysis", "HDC: " + HDC + " yAc: " + yAccCalibrated);
                    fuseTimer.scheduleAtFixedRate(new DrivingAnalysisAccelerometer(), 100, 250);
                }
                if (xAccCalibrated < -X2) {
                    SHLC++;
                    SHL = true;
                    playSound();
                    Log.d("drivingAnalysis", "SHLC: " + SHLC + " xAc: " + xAccCalibrated);
                    fuseTimer.scheduleAtFixedRate(new DrivingAnalysisAccelerometer(), 100, 250);
                }
                if (xAccCalibrated > X2) {
                    SHRC++;
                    SHR = true;
                    playSound();
                    Log.d("drivingAnalysis", "SHRC: " + SHRC + " XAc: " + xAccCalibrated);
                    fuseTimer.scheduleAtFixedRate(new DrivingAnalysisAccelerometer(), 100, 250);
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

        }
    }


    //Make a sounf when occur a event
    private void playSound() {
        MediaPlayer player = MediaPlayer.create(getContext(),
                Settings.System.DEFAULT_NOTIFICATION_URI);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });
        player.start();
    }


    public void setCounters() {

        //Set Counters
        tv_safeAccel.setText("SAC: " + SAC);
        tv_safeDesa.setText("SDC: " + SDC);
        tv_safeLeft.setText("SLC: " + SLC);
        tv_safeRight.setText("SRC: " + SRC);
        tv_hardAccel.setText(" HAC: " + HAC);
        tv_hardDes.setText(" HDC: " + HDC);
        tv_sharpLeft.setText(" SHLC: " + SHLC);
        tv_sharpRight.setText(" SHRC: " + SHRC);
    }


    /***********************************Start Trip Fuction ********************************************/


    boolean startTripState = false;
    long EndTime, StartTime;
    long timeTrip;
    double elapse;
    double aveSpeedKM;

    public void startTrip() {
        //----------//get location of the destination ----------------------------------------------

        Log.d("StartTrip", "statusLocation" + statusLocation);
        if (!statusLocation == false) {

            if (startTripState == false) {


                Trip trip = new Trip();
                initListenerMagnometer();


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
                startTripState = true;
                Log.d("saveTripInitial", "TripID" + trip.getTripId());

                Log.d("Score", "finalScoreTrip" + finalScoreTrip);
            } else {
                // END OF THE TRIP

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
                tv_totalHours.setText("Time Trip: " + String.format("%.1f", elapse) + "Min");

                float reductionFactor = 5 * HAC + 5 * HDC + 5 * SHLC + 5 * SHRC;
                finalScoreTrip = scoreTrip - reductionFactor;
                if (finalScoreTrip < 0) {
                    finalScoreTrip = 0;
                }
                Log.d("Score", "reductionFactor" + reductionFactor);
                Log.d("Score", "finalScoreTrip" + finalScoreTrip);
                tv_finalScore.setText(String.valueOf((int) finalScoreTrip));
                //Reset Values
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
                tv_aveSpeed.setText("Avg Speed: " + String.format("%.1f", aveSpeedKM));
                //reset calibrator
                xAccelerometer2 = 0;
                yAccelerometer2 = 0;
                xcal.resetData(new DataPoint[]{new DataPoint(0, 0)});
                ycal.resetData(new DataPoint[]{new DataPoint(0, 0)});
                target.resetData(new DataPoint[]{new DataPoint(0, 0)});
                mSensorManager.unregisterListener(this);
                initListenerAccelerometer();
                saveTripEnd();
                startTripState = false;

            }
        } else {
            Toast.makeText(getContext(), "Location is not still enable. \nPlease wait or go to a open area", Toast.LENGTH_LONG).show();
        }

    }


    /************************************Save recorded data***********************************************/
    public void saveTripInitial(long userId, Trip trip) {
        try {
            trip.setUserCreatorId(userId);
            trip.setStartLocationLAT(lat);
            trip.setStartLocationLON(lon);

            trip.setStartLocationName(getAddressLocationName(lat, lon));
            trip.setStartDate(StartTime);
            trip.setStartTime(StartTime);


            //bring 0 require the last number list could be de las
            trip.setEndLocationLAT(1);
            trip.setEndLocationLON(1);

            trip.setEndLocationName(getAddressLocationName(lat, lon));
            trip.setKilometers(1); // save it in Km/s
            trip.setTimeTrip(1);
            trip.setScoreTrip(33);
            trip.setEndDate(1);
            trip.setEndTime(1);

            trip.setAveSpeed(1);
            dao.insertTrip(trip);


        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong Please start again the trip", Toast.LENGTH_LONG).show();
        }


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

        //Toast.makeText(getContext(), "Something went wrong Please try to end the trip again", Toast.LENGTH_LONG).show();


        lat = 0;
        lon = 0;
        distanceTraveled = 0;
        elapse = 0;
        finalScoreTrip = 0;
        EndTime = 0;
        aveSpeedKM = 0;

        //access control for trip view
        List<Trip> tripList = dao.getAllTripsByUser(userId);
        editor.putInt("position", tripList.size() - 1);
        editor.commit();


    }

    public void saveFusionSensor() {


        FusionSensor fusionSensor = new FusionSensor();

        fusionSensor.setTripCreatorId(tripID);
        fusionSensor.setxAcc(xAccCalibrated);
        fusionSensor.setyAcc(yAccCalibrated);
        //fusionSensor.setzAcc();
        fusionSensor.setPitch(0);
        fusionSensor.setYaw(0);
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

        final int maxResults = 10;
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, lon, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return addresses;
    }

    private String getAddressLocationName(Double lat, Double lon) {

        geocoder = new Geocoder(getContext());
        List<Address> geoResult = findGeocoder(lat, lon);
        Log.d("getAddressLocationName", "geoResult.size()" + geoResult.size());
        if (geoResult.size() == 0) {
            Log.d("getAddressLocationName", "inside if" + geoResult.size());
        }

        Address thisAddress = geoResult.get(0);
        return String.valueOf(thisAddress.getAddressLine(0));

    }


}
