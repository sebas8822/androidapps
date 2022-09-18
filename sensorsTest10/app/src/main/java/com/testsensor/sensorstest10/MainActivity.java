package com.testsensor.sensorstest10;


import android.Manifest;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView tvSpeed, tvUnit, tvLat, tvLon, tvAccuracy, tvHeading, tvMaxSpeed, tv_Xaxis, tv_Yaxis, tv_pith,tv_yaw, tv_XaxisCali, tv_YaxisCali,tv_distance,tv_totalHours, tv_total_Current,tv_aveSpeed;
    private Button bt_startTrip;
    private static final String[] unit = {"km/h", "mph", "meter/sec", "knots"};
    private int unitType;
    private NotificationCompat.Builder mbuilder;
    private NotificationManager mnotice;
    private double maxSpeed = -100.0;
    private MainActivity activity;

    private SharedPreferences prefs;

    private SensorManager mSensorManager = null;
    private GraphView grapht;

    long previousTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvMaxSpeed = (TextView) findViewById(R.id.tvMaxSpeed);
        tvUnit = (TextView) findViewById(R.id.tvUnitc);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);
        tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        tv_Xaxis = (TextView) findViewById(R.id.tv_Xaxis);
        tv_Yaxis = (TextView) findViewById(R.id.tv_Yaxis);
        tv_XaxisCali= (TextView) findViewById(R.id.tv_XaxisCali);
        tv_YaxisCali= (TextView) findViewById(R.id.tv_YaxisCali);
        tv_pith= (TextView) findViewById(R.id.tv_pith);
        tv_yaw= (TextView) findViewById(R.id.tv_yaw);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_total_Current = (TextView) findViewById(R.id.tv_total_Current);
        tv_totalHours= (TextView) findViewById(R.id.tv_totalHours);
        tv_aveSpeed = (TextView) findViewById(R.id.tv_aveSpeed);

        bt_startTrip =(Button) findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip();
            }
        });



        activity = this;
        previousTime = System.currentTimeMillis();
        //FOR SAVE REFERENCES
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //SET UP UNITS PREFERENCES
        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        tvUnit.setText(unit[unitType - 1]);


        //SAVE VALUE INTO THE CURRENT ACTIVITY
        if (savedInstanceState != null) {
            maxSpeed = savedInstanceState.getDouble("maxspeed", -100.0);

        }
        // check if the services are available ASK TO THE USER TO ENABLE
        if (!this.isLocationEnabled(this)) {


            //show dialog if Location Services is not enabled


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    Toast.makeText(activity, "Please enable Location-based service / GPS", Toast.LENGTH_LONG).show();


                }


            });
            builder.create().show();
        }


        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        /**Init Graph*/
        grapht = (GraphView) findViewById(R.id.graph);


        // Add graphs set up
        GraphView graph = (GraphView) findViewById(R.id.graph);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        Xseries.setColor(Color.RED);
        graph.addSeries(Xseries);
        Yseries.setColor(Color.GREEN);
        graph.addSeries(Yseries);
        XCseries.setColor(Color.MAGENTA);
        graph.addSeries(XCseries);
        YCseries.setColor(Color.GRAY);
        graph.addSeries(YCseries);
        Pseries.setColor(Color.BLUE);
        graph.addSeries(Pseries);
        Rseries.setColor(Color.CYAN);
        graph.addSeries(Rseries);
        TUPseries.setColor(Color.WHITE);
        graph.addSeries(TUPseries);
        TDOWNseries.setColor(Color.WHITE);
        graph.addSeries(TDOWNseries);








        /**Init Sensors*/// get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();


        new SpeedTask(this).execute("string");
    }

    // save into the the sabe activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putDouble("maxspeed", maxSpeed);
    }

    // THIS BRING DE DATA WHEN THE APPLICATION RUN AGAIN
    protected void onRestoreInstanceState(Bundle bundle) {

        super.onRestoreInstanceState(bundle);

        maxSpeed = bundle.getDouble("maxspeed", -100.0);

    }

    // Speed Longitud and Lantitud
    protected void onResume() {
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        maxSpeed = prefs.getFloat("maxspeed", -100.0f);


        tvUnit.setText(unit[unitType - 1]);

        if (maxSpeed > 0) {

            float multiplier = 3.6f;

            switch (unitType) {
                case 1:
                    multiplier = 3.6f;
                    break;
                case 2:
                    multiplier = 2.25f;
                    break;
                case 3:
                    multiplier = 1.0f;
                    break;

                case 4:
                    multiplier = 1.943856f;
                    break;

            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(0);

            tvMaxSpeed.setText(numberFormat.format(maxSpeed * multiplier));

        }
        //mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //removeNotification();


    }

    // Speed Longitud and Lantitud CHECK MAYBE TO SAVE VARIABLES
    protected void onStop() {
        super.onStop();

        //displayNotification();


    }

    // OPTIONAL TO SAVE VARIABLES OR DO SOMETHING ELSE
    protected void onPause() {
        super.onPause();

        float tempMaxpeed = 0.0f;
        try {

            tempMaxpeed = Float.parseFloat(tvMaxSpeed.getText().toString());


        } catch (java.lang.NumberFormatException nfe) {

            tempMaxpeed = 0.0f;

        }

        prefs.edit().putFloat("maxSpeed", tempMaxpeed);


    }



    Location previousLocation = null;
    float distanceTraveled = 0;
    //THIS CLASS PROVIDE THE VARIABLES SPEED lONGITUD AND lATITUD
    private class SpeedTask extends AsyncTask<String, Void, String> {
        final MainActivity activity;
        float speed = 0.0f;
        double lat;
        double lon;
        double distance;
        LocationManager locationManager;

        public SpeedTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);


            return null;

        }

        protected void onPostExecute(String result) {
            tvUnit.setText(unit[unitType - 1]);
            LocationListener listener = new LocationListener() {
                float filtSpeed;
                float localspeed;




                @Override
                public void onLocationChanged(Location location) {
                    speed = location.getSpeed();



                    //multiplaer to show the units standar Km/h
                    float multiplier = 3.6f;

                    switch (unitType) {
                        case 1:
                            multiplier = 3.6f;
                            break;
                        case 2:
                            multiplier = 2.25f;
                            break;
                        case 3:
                            multiplier = 1.0f;
                            break;

                        case 4:
                            multiplier = 1.943856f;
                            break;

                    }

                    if (maxSpeed < speed) {
                        maxSpeed = speed;
                    }


                    localspeed = speed * multiplier;
                    /******************************Speed*******************************************/
                    filtSpeed = filter(filtSpeed, localspeed, 2);


                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(1);


                    if(previousLocation!=null){
                        distanceTraveled +=location.distanceTo(previousLocation);//in meters

                    }
                    previousLocation =location;

                    lon = location.getLongitude();
                    lat = location.getLatitude();

                    Log.d("onChange", "Speed " + localspeed + "latitude: " + lat + " longitude: " + lon + " Distance: " + distanceTraveled/1000);
                    tvSpeed.setText(numberFormat.format(filtSpeed));
                    /************************Distance and MAX speed*******************************/
                    tv_distance.setText(numberFormat.format(distanceTraveled/1000.0)+"km");
                    tvMaxSpeed.setText(numberFormat.format(maxSpeed * multiplier));


                    if (location.hasAltitude()) {
                        tvAccuracy.setText(numberFormat.format(location.getAccuracy()) + " m");
                    } else {
                        tvAccuracy.setText("NIL");
                    }

                    numberFormat.setMaximumFractionDigits(0);


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


            if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                    !=PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);



            }else {
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
    LineGraphSeries<DataPoint> XCseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> YCseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Pseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> Rseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> TUPseries = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> TDOWNseries = new LineGraphSeries<DataPoint>();
    private int pointsPlotted = 10;

    @Override
    public void onSensorChanged(SensorEvent event) {
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
        drawGraph();

        /**No necesesary just o display the current time runing*/
        long currentTime = System.currentTimeMillis();
        int totalMinutes = (int) (((currentTime-previousTime) / (1000*60)) % 60);
        int hours   = (int) (((currentTime-previousTime) / (1000*60*60)) % 24);
        int seconds = (int) ((currentTime-previousTime) / 1000) % 60 ;
        tv_total_Current.setText(String.valueOf(totalMinutes)+" Minutes "+String.valueOf(seconds)+" seconds");











    }

    public void drawGraph(){
        tv_Xaxis.setBackgroundColor(Color.RED);
        tv_Xaxis.setText("XAccel: "+String.valueOf(xAccelerometer));
        tv_Yaxis.setBackgroundColor(Color.GREEN);
        tv_Yaxis.setText("YAccel: "+String.valueOf(yAccelerometer));
        tv_XaxisCali.setBackgroundColor(Color.MAGENTA);
        tv_XaxisCali.setText("XAccelCAL: "+String.valueOf(xAccCalibrated));
        tv_YaxisCali.setBackgroundColor(Color.GRAY);
        tv_YaxisCali.setText("YAccelCAL: "+String.valueOf(yAccCalibrated));
        tv_pith.setBackgroundColor(Color.BLUE);
        tv_pith.setText("Pitch: "+String.valueOf(getPitchQ));
        tv_yaw.setBackgroundColor(Color.CYAN);
        tv_yaw.setText("yaw: "+String.valueOf(getRollQ));



        viewport.setMaxX(pointsPlotted);
        viewport.setMinX(pointsPlotted - 100);// set to show the last 100 points

        //update the graph
        pointsPlotted++;

        // to not saturate the memory we have to limit the points to keep it into the memory

        if (pointsPlotted > 1000){
            pointsPlotted = 1; // reset the variable
            Xseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            Yseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            XCseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            YCseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            Pseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            Rseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            TUPseries.resetData(new DataPoint[]{new DataPoint(1,0)});
            TDOWNseries.resetData(new DataPoint[]{new DataPoint(1,0)});
        }








        Xseries.appendData( new DataPoint(pointsPlotted,xAccelerometer ),true,pointsPlotted);
        Yseries.appendData( new DataPoint(pointsPlotted,yAccelerometer ),true,pointsPlotted);
        XCseries.appendData( new DataPoint(pointsPlotted,xAccCalibrated ),true,pointsPlotted);
        YCseries.appendData( new DataPoint(pointsPlotted,yAccCalibrated ),true,pointsPlotted);
        Pseries.appendData(new DataPoint(pointsPlotted, getPitchQ), true, pointsPlotted);
        Rseries.appendData(new DataPoint(pointsPlotted, getRollQ), true, pointsPlotted);
        TUPseries.appendData( new DataPoint(pointsPlotted,0.12 ),true,pointsPlotted);
        TDOWNseries.appendData( new DataPoint(pointsPlotted,-0.12 ),true,pointsPlotted);






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
            Log.d("calibrateAccelerometer","xAccCalibrated"+ xAccCalibrated);
            Log.d("calibrateAccelerometer","yAccCalibrated"+ yAccCalibrated);






        }
    }

    //variables for acceleromneter calibration
    private float[] rotationMatrix = new float[9];
    private float[] accMagOrientation = new float[3];


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
    private float[] gyro = new float[3]; /** i thing this give the pitch roll and yaw*/
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

                Log.d("oncomputeQuaternion","getPitchQ"+getPitchQ);
                Log.d("oncomputeQuaternion","getRollQ"+getRollQ);
                Log.d("oncomputeQuaternion","getYawQ"+getYawQ);





            }
        }
    }

   // public void drivingAnalysis()



    public void printArray(String name,float[] array){

        for (int v = 0; v < accel.length; v++) {
            Log.d("printarray","pos "+v+" "+name+": "+array[v]);
        }

    }


    boolean startTripState = false;
    long EndTime, StartTime;

    public void startTrip() {
        //----------//get location of the destination ----------------------------------------------

        if (startTripState == false) {
            //DURING THE TRIP
            // during the start of a trip, values are initialized
            // change the button to display "End" to end the trip
            bt_startTrip.setText("End Trip");
            bt_startTrip.setBackgroundColor(getResources().getColor(R.color.StateButton));
            previousLocation = null;
            StartTime = System.currentTimeMillis();
            //suddenBreaksCount = 0;
            //suddenAcceleration = 0;
            //scoreList.clear();
            startTripState = true;

            //checkReadings();


            //getting the latitude and longitude of the user

            // making changes to the UI

        } else {
            // END OF THE TRIP
            startTripState = false;
            // Time,Score,AverageScore, is compute after finish trip
            bt_startTrip.setText("Start Trip");
            bt_startTrip.setBackgroundColor(getResources().getColor(R.color.blue_sky_500));
            /*****************************calculating time*********************************/
            EndTime = System.currentTimeMillis();
            long timeTrip = EndTime - StartTime;
            double  distanceKM = distanceTraveled / 1000.0;// it is show in real-Time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String time  = simpleDateFormat.format(new Date(timeTrip));
            double elapse = (double)  (timeTrip/1000)/60;// this is the measure i need
            float totalTimeTraveledMin = (float) (((EndTime-StartTime) / (1000*60)) % 60);
            int hours   = (int) (((EndTime-StartTime)  / (1000*60*60)) % 24);
            int seconds = (int) ((EndTime-StartTime)  / 1000) % 60 ;

            //tv_totalHours.setText(String.valueOf(totalTimeTraveledMin)+" Minutes "+String.valueOf(seconds)+" seconds");
            tv_totalHours.setText(time+"  "+Math.round(elapse*100)/100);




            double avgSpeedKM = distanceKM / totalTimeTraveledMin;
            tv_aveSpeed.setText(String.valueOf(avgSpeedKM)+" AveSpeed ");


            /**SAVE VALUES fuction*///Save values
            //getting average score of the trip
            //scoreArrayList = new ScoreArrayList(scoreList);
            //avgScore = scoreArrayList.getAverage();
            //double result = Math.round(avgScore * 100) / 100.0;

            Toast.makeText(getApplicationContext(), "Trip End final Trip Score: ", Toast.LENGTH_LONG).show();

        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}


