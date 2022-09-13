package com.finalproyect.niftydriverapp.ui.starttrip;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.Sensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class StartTripFragment extends Fragment {

    private Button bt_startTrip;

    private ImageView im_profile;

    private Button bt_populateSensorDatabase,bt_ResetTripDatabase,bt_ResetSensorDatabase;

    /***********************************Graph staff***************************************************/
    // VARIABLES
    private SensorManager mSensorManager;
    private android.hardware.Sensor mAccelerometer;
    private android.hardware.Sensor mGyro;





    // provide references
    TextView text_accel, text_prev_acc,text_curr_acc;
    ProgressBar prog_shakebar;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue; // I need it to compare with the current value if the phone move

    //variables to plot the graph
    private int pointsPlotted = 10;
    private int graphIntervalsCounter = 0;
    //variable for view port
    private Viewport viewport;

    //Available in the entire application - the realtime chart
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //new DataPoint(0, 1),

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
            // support to calculate changes in just one value
            //accelerationCurrentValue = Math.sqrt((x*x+y*y+z*z));


            //abs = absolute value ignore the sign values (-1) and give a value to compare
            //double changeInAcceleration = Math.abs(accelerationCurrentValue-accelerationPreviousValue);
            // save the change for the next read
            //accelerationPreviousValue = accelerationCurrentValue;
            //update the interface (show to user)

            //text_accel.setText("Acceleration change val = " + (int) changeInAcceleration);
            //text_curr_acc.setText("Current val = "+(int)accelerationCurrentValue);
            //text_prev_acc.setText("Prev val = "+(int)accelerationPreviousValue);

            text_accel.setText("X axis = " + x);
            text_prev_acc.setText("Y axis = "+ y);
            text_curr_acc.setText("Z axis = "+ z);


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


        /*****************************Sensor****************************************************/

        // association with the actual id
        text_accel =(TextView) view.findViewById(R.id.text_accel);
        text_prev_acc = view.findViewById(R.id.text_prev_acc);
        text_curr_acc = view.findViewById(R.id.text_curr_acc);



        // initialization Sensor objects
        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);// means get it from service built into the android system
        mAccelerometer = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);// define the default sensor that is looking for "accelerometer"
        initGraph(view);


        /*********************************************************************************/
        /*********************************UI Functions************************************************/

        bt_startTrip = (Button) view.findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Populate Trip", Toast.LENGTH_LONG).show();
                //populateUserTable();
                populateTripTable(1);
                populateTripTable(2);
            }
        });

        bt_populateSensorDatabase = (Button) view.findViewById(R.id.bt_populateSensorDatabase);
        bt_populateSensorDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Populate Sensor", Toast.LENGTH_LONG).show();
                //populateUserTable();
                populateSensorTable(1);
                populateSensorTable(2);
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

        bt_ResetSensorDatabase = (Button) view.findViewById(R.id.bt_ResetSensorDatabase);
        bt_ResetSensorDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = AppDatabase.getDbInstance(getContext());
                DAO dao = db.driverDao();
                dao.deleteAllSensor();
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

    public void populateTripTable(int userId) {
        String[] num = {"ONE", "DOS", "THREE", "FOUR","FIVE", "SIX","SEVEN", "EIGHT","NINE","TEN"};
        String[] alp = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","E","W","X","Y","Z"};

        int Max = 100;
        int Min = 0;

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i <2; i++) {
            Trip trip = new Trip();
            trip.setUserCreatorId(userId);

            trip.setStartLocation(1.1111);
            trip.setEndLocation(1111);
            trip.setKilometers(1+i);
            trip.setTimeTrip(10+i);
            trip.setScoreTrip((int) ((Math.random() * (Max - Min)) + Min));
            trip.setStartDate("10/8/1922");
            trip.setEndDate("10/8/1922");
            trip.setStartTime("10:00");
            trip.setEndTime("11:00");


            //user.setPicture("@");
            dao.insertTrip(trip);


        }


    }

    public void populateSensorTable(int tripId) {
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


    public void scoreTrip(){


        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        // here i need to cal the variables form sensor data and create the scoring and feedback


    }



}



/**
 Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media  .EXTERNAL_CONTENT_URI);
 startActivityForResult(intent, 3);*/

/**
 @Override
 public void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);
 if(resultCode == RESULT_OK && data!= null){
 Uri selectedImage = data.getData();
 Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.img_1);

 try {
 bitmap= MediaStore.Images.Media.getBitmap(
 getContext().getContentResolver(),
 selectedImage);
 } catch (IOException e) {
 e.printStackTrace();
 }

 ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
 bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
 byte[] image = byteArray.toByteArray();
 //populateUserDataBase(image);
 im_profile.setImageBitmap(bitmap);

 }



 }*/
