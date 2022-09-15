package com.finalproyect.niftydriverapp.ui.starttrip;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

import static java.lang.System.currentTimeMillis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.Sensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StartTripFragment extends Fragment {

    //variables

    double latitude, longitude;

    private Button bt_startTrip;

    public void setUserid(long userid) {
        this.userid = userid;
    }
    private long userid;

    SharedPreferences sp;//Init sharepreferences for user


    /******+***************************************************************************************/
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    /***********************************Graph staff***************************************************/
    // VARIABLES
    private SensorManager mSensorManager;
    private android.hardware.Sensor mAccelerometer;
    private android.hardware.Sensor mGyro;

    // Variables GPS
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    //// Variables Time
    long timeEnd, timeStart;// for start and end time


    // provide Activityreferences
    TextView text_accel, text_prev_acc,text_curr_acc, text_latitud, text_longitud,text_speed;
    private Button bt_ResetTripDatabase;


    //no need it yet
    private double accelerationCurrentValue;
    private double accelerationPreviousValue; // I need it to compare with the current value if the phone move

    //variables to plot the graph
    private int pointsPlotted = 10;
    private int graphIntervalsCounter = 0;
    //variable for view port
    private Viewport viewport;
    //score
    double safeScore = 0;
    double avgScore =0;
    double previousScore = 0;
    List<Double> scoreList;
    //ScoreArrayList scoreArrayList; //class that need to be created

    //Array where i put the variables to point the score


    //Available in the entire application - the realtime chart
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {//new DataPoint(0, 1),
    });
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //new DataPoint(0, 1),
    });

    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //new DataPoint(0, 1),
    });

    // trigger every time when the sensor is change
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // to determine how much the phone is going to move
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];


            /*********************************Start the algorithm**********************************/


            text_accel.setText("X axis = " + x);
            text_prev_acc.setText("Y axis = "+ y);
            text_curr_acc.setText("Z axis = "+ z);


            /** this lines call the object i have them before on create but i nee a contant reding so this test the constant reding */
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            Geolocation gps = new Geolocation(locationManager, getContext());
            text_latitud.setText("Latitud: " + gps.getLatitude() +"Postion: "+ pointsPlotted);
            Log.d("onSensor",String.valueOf(gps.getLatitude()) + "Postion: "+ pointsPlotted);
            text_longitud.setText("longitude: " + gps.getLongitude()+ "Postion: "+ pointsPlotted);
            Log.d("onSensor",String.valueOf(gps.getLongitude()) + "Postion: "+ pointsPlotted);
            text_speed.setText("speed: " + gps.getSpeed()+ "Postion: "+ pointsPlotted);
            Log.d("onSensor",String.valueOf(gps.getSpeed()) + "Postion: "+ pointsPlotted);
            //update the graph
            pointsPlotted++;

            // to not saturate the memory we have to limit the points to keep it into the memory

            if (pointsPlotted > 1000){
                pointsPlotted = 1; // reset the variable
                series.resetData(new DataPoint[]{new DataPoint(1,0)});
                series2.resetData(new DataPoint[]{new DataPoint(1,0)});
                series3.resetData(new DataPoint[]{new DataPoint(1,0)});

            }

            series.appendData( new DataPoint(pointsPlotted,x ),true,pointsPlotted);
            series2.appendData( new DataPoint(pointsPlotted,y),true,pointsPlotted);
            series3.appendData( new DataPoint(pointsPlotted,z),true,pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);// set to show the last 100 points

        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

        }
    };


    /***************************************************************************************/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starttrip, container, false);


        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId",0);
        setUserid(userId);

        /*****************************Sensor****************************************************/
        //init GPS reading latitud, longitud and speed
        /**
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        /***********Check if gps is enable or not********** *//**
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Fuction to enable GPS
            turnOnGPS();

        }else{

            //GPS is already on
            geolocation(view);
        }*/

        //getting the latitude and longitude of the user
        LatLng latLng = new LatLng(latitude, longitude);
        Log.d("NewVariables", "latLng: "+String.valueOf(latLng));





        /***********************************Testing and choise of data*****************************/
        Date currentTime = Calendar.getInstance().getTime(); // date to save
        Log.d("NewVariables ", "currentTime: "+String.valueOf(currentTime));

        timeStart = currentTimeMillis();//get the current time in milliseconds to calculate the diference this give the driving time

        Log.d("NewVariables ", "timeStart: "+String.valueOf(timeStart));



        text_latitud=(TextView) view.findViewById(R.id.text_latitud);
        text_longitud=(TextView) view.findViewById(R.id.text_longitud);
        text_speed=(TextView) view.findViewById(R.id.text_speed);





        /******************************************************************************************/


        // association with the actual id
        text_accel =(TextView) view.findViewById(R.id.text_accel);
        text_prev_acc = view.findViewById(R.id.text_prev_acc);
        text_curr_acc = view.findViewById(R.id.text_curr_acc);





        // initialization Sensor objects
        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);// means get it from service built into the android system
        mAccelerometer = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);// define the default sensor that is looking for "accelerometer"
        initGraph(view);


        /*********************************************************************************/
        // creating list to add score
        scoreList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            getActivity().finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }


        /*********************************UI Functions************************************************/

        bt_startTrip = (Button) view.findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Populate Trip", Toast.LENGTH_LONG).show();
                //populateUserTable();
                populateTripTable(userid);
                populateTripTable(2);
            }
        });



        bt_ResetTripDatabase = (Button) view.findViewById(R.id.bt_ResetTripDatabase);
        bt_ResetTripDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = AppDatabase.getDbInstance(getContext());
                DAO dao = db.driverDao();
                dao.deleteAllTrip();
                Toast.makeText(getContext(), "Delete Trip", Toast.LENGTH_LONG).show();

            }
        });


        /*********************************************************************************/


        return view;
    }


    //Graph Purposes
    public void initGraph(View view){
        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        series.setColor(Color.RED);
        series2.setColor(Color.YELLOW);
        series3.setColor(Color.GREEN);
        graph.addSeries(series);
        graph.addSeries(series2);
        graph.addSeries(series3);
    }
    //Sensor Purposes
    public void onResume() {
        super.onResume();
        // sensor manager is going to use this sensor function
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //Sensor Purposes
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
    //Setup default image for database purpose this fuction should mode to login page to create the user
    private byte[] defaultImage(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.img_1);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] image = byteArray.toByteArray();
        return image;
    }
    //
    public void populateUserTable() {
        String[] num = {"ONE", "DOS", "THREE", "FOUR","FIVE", "SIX","SEVEN", "EIGHT","NINE","TEN"};
        String[] alp = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","E","W","X","Y","Z"};

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserName(num[i]+"Sebastian");
            user.setLastName("Ramirez");
            user.setEmail(alp[i]+num[i]+"8822@hotmail.com");
            user.setPassword("S3b4st1@nR");
            user.setPicture(defaultImage());


            //user.setPicture("@");
            dao.insertUser(user);


        }


    }

    public void populateTripTable(long userId) {
        String[] num = {"ONE", "DOS", "THREE", "FOUR","FIVE", "SIX","SEVEN", "EIGHT","NINE","TEN"};
        String[] alp = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","E","W","X","Y","Z"};

        int Max = 100;
        int Min = 0;

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        locationManager =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Geolocation gps = new Geolocation(locationManager, getContext());


        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();


            Trip trip = new Trip();
            trip.setUserCreatorId(userId);

            trip.setStartLocation(gps.getLatitude()+ ", "+gps.getLongitude());
            trip.setEndLocation(1111);
            trip.setKilometers(1);
            trip.setTimeTrip(10);
            trip.setScoreTrip((int) ((Math.random() * (Max - Min)) + Min));
            trip.setStartDate("10/8/1922");
            trip.setEndDate(String.valueOf(345678));
            trip.setStartTime("10:00");
            trip.setEndTime("11:00");


            //user.setPicture("@");
            dao.insertTrip(trip);
            populateSensorTable(trip.getTripId());





    }

    public void populateSensorTable(long tripId) {
        String[] num = {"ONE", "DOS", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"};
        String[] alp = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "E", "W", "X", "Y", "Z"};
        float accMax = 0.3f;
        float accMin = -0.3f;

        int speedMin = 0;
        int speedMax = 200;
        Random random = new Random();





        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i < 10; i++) {
            Sensor sensor = new Sensor();

            sensor.setTripCreatorId(tripId);
            sensor.setxAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setyAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setzAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setPitch((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setYaw((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setCarSpeed((int) ((Math.random() * (speedMax - speedMin)) + speedMin));
            sensor.setGoogleCurSpeed((int) ((Math.random() * (speedMax - speedMin)) + speedMin));
            sensor.setCurLocationLong(41.40338f);
            sensor.setCurLocationLat(2.17403f);
            sensor.setValSpeed(random.nextBoolean());
            sensor.setSafeAcc(random.nextBoolean());
            sensor.setSafeDes(random.nextBoolean());
            sensor.setSafeLeft(random.nextBoolean());
            sensor.setSafeRight(random.nextBoolean());
            sensor.setHardAcc(random.nextBoolean());
            sensor.setHardDes(random.nextBoolean());
            sensor.setSharpLeft(random.nextBoolean());
            sensor.setSharpRight(random.nextBoolean());


            //user.setPicture;
            dao.insertSensor(sensor);


        }
    }

    private void geolocation(View view) {

        text_latitud=(TextView) view.findViewById(R.id.text_latitud);
        text_longitud=(TextView) view.findViewById(R.id.text_longitud);
        text_speed=(TextView) view.findViewById(R.id.text_speed);

        //check permitions again
        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);



        }else{
            Location LocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGPS !=null){
                double latitude = LocationGPS.getLatitude();
                double longitude = LocationGPS.getLongitude();
                double speed = LocationGPS.getSpeed();

                /**Show the values and save them**/
                Toast.makeText(getContext(),"Latitud: " + latitude+"longitude: " + longitude+"speed: " + speed,Toast.LENGTH_LONG).show();
                text_latitud.setText("Latitud: " + latitude);
                text_longitud.setText("longitude: " + longitude);
                text_speed.setText("speed: " + speed);


                /*********************************/
            }else if (LocationNetwork !=null){
                double latitude = LocationNetwork.getLatitude();
                double longitude = LocationNetwork.getLongitude();
                double speed = LocationNetwork.getSpeed();

                /**Show the values and save them**/
                Toast.makeText(getContext(),"Latitud: " + latitude+"longitude: " + longitude+"speed: " + speed,Toast.LENGTH_LONG).show();
                text_latitud.setText("Latitud: " + latitude);
                text_longitud.setText("longitude: " + longitude);
                text_speed.setText("speed: " + speed);
                /*********************************/
            }else if (LocationPassive !=null){
                double latitude = LocationPassive.getLatitude();
                double longitude = LocationPassive.getLongitude();
                double speed = LocationPassive.getSpeed();

                /**Show the values and save them**/
                Toast.makeText(getContext(),"Latitud: " + latitude+"longitude: " + longitude+"speed: " + speed,Toast.LENGTH_LONG).show();
                text_latitud.setText("Latitud: " + latitude);
                text_longitud.setText("longitude: " + longitude);
                text_speed.setText("speed: " + speed);
                /*********************************/
            }else{
                Toast.makeText(getContext(),"Can't get current location",Toast.LENGTH_LONG).show();



            }

        }

    }

    private void turnOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Enable GPS?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NOT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();




    }



    public void scoreTrip(){


        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        // here i need to call the variables from sensor data and create the scoring and feedback


    }

    /**************************************adding new staff****************************************/

    // checking location permissions
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    // checking google play services
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }







}

