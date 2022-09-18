package com.finalproyect.niftydriverapp.ui.starttrip;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproyect.niftydriverapp.R;

import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/*
 Main Page after Login
 Here, maps are initialized, sensors are initialized and sensor end values are computed,
 behavior is analyzed and score is computed.
*/
public class StartTripFragment extends Fragment implements SensorEventListener {
    GraphView grapht;
    Context context;
    Location location;
    private Button bt_startTrip;
    private EditText searchField;
    private TextView text_accel, text_prev_acc;
    private int turns, suddenAcceleration = 0;
    private float totalScore = 10;
    //private GoogleMap mMap;
    //GoogleApiClient mGoogleApiClient;
    //Location mLastLocation;
    //Marker mCurrLocationMarker, mUserLocationMarker;
    //LocationRequest mLocationRequest;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    double end_latitude, end_longitude;
    int i = 0;
    long tEnd, tStart;
    String timeString;
    private boolean running;
    private boolean paused;
    private long start = 0;
    private long pausedStart = 0;
    private long end = 0;
    private List<String> details = new ArrayList<>(4);
    int limitExceedCount = 0;
    String slimitExceedCount;
    String limitExceedTime;
    int maxSpeed = 0;
    String sMaxSpeed;
    String Name;
    private float currentSpeed = 0.0f;
    String speedlimit;
    int flag = 0;
    private boolean RainAndSnow;
    private int suddenBreaksCount = 0;
    long tBreakStart, tBreakEnd;
    float tempSpeed = 0;

    //private FirebaseDatabase firebaseDatabase;
    /// DatabaseReference mRootReference;
    //private DatabaseReference mLocationReference;
    //private DatabaseReference mUsersLocation, mScoreReference;

    // sensor variables
    // for gryo
    public static final float EPSILON = 0.000000001f;
    public static final int TIME_CONSTANT = 10;
    private static final float NS2S = 1.0f / 1000000000.0f;
    int count = 1;
    float pitchOut, rollOut, yawOut;
    // counter for sensor fusion
    int overYaw = 0;
    int overPitch = 0;
    //counter for quaternion
    int overYawQ = 0;
    int overPitchQ = 0;

    // final pitch and yaw values
    int finalOverYaw = 0;
    int finalOverPitch = 0;

    //counter for accelerometer reading
    int overX = 0;
    int overY = 0;
    float[] mMagneticField;
    float[] mGravity;
    DecimalFormat d = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
    Float getPitch = 0f;
    Float getRoll = 0f;
    Float getYaw = 0f;
    Float getPitchQ = 0f;
    Float getRollQ = 0f;
    Float getYawQ = 0f;
    // normal - sensor fusion, Q - denotes quaternion
    Float newPitchOut = 0f;
    Float newRollOut = 0f;
    Float newYawOut = 0f;
    //    int underX = 0;
//    int underY = 0;
    Float newPitchOutQ = 0f;
    Float newRollOutQ = 0f;
    Float newYawOutQ = 0f;
    float mPitch, mRoll, mYaw;
    // for accelerometer
    float xAccelerometer;
    float yAccelerometer;
    float zAccelerometer;
    float xPreviousAcc;
    float yPreviousAcc;
    float zPreviousAcc;
    float xAccCalibrated = 0f;
    float yAccCalibrated = 0f;
    float zAccCalibrated = 0f;
    boolean writeCheck = false;
    TextView textOverYaw, textOverPitch, textOverYawQ, textOverPitchQ, textOverX, textOverY;
    private SensorManager mSensorManager = null;
    // angular speeds from gyro
    private float[] gyro = new float[3];
    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];
    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];
    // magnetic field vector
    private float[] magnet = new float[3];
    // accelerometer vector
    private float[] accel = new float[3];
    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];
    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];
    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];
    private float timestamp;
    private boolean initState = true;
    private Timer fuseTimer = new Timer();
    private String SHARED_PREF_NAME = "driverbehaviorapp";
    private boolean mInitialized = false;
    Boolean yAccChange = false;
    Boolean xAccChange = false;
    LocationManager locationManager;
    // for 30 sec sensor values reset
    int getFinalOverYaw = 0;
    int getFinalOverPitch = 0;
    int getFinalOverX = 0;
    int getFinalOverY = 0;
    Boolean isBrakesApplied = false;
    TextView score;
    //speedlimit
    int mph;
    //score
    double safeScore = 0;
    double avgScore = 0;
    double previousScore = 0;
    List<Double> scoreList;
    ScoreArrayList scoreArrayList;
    RelativeLayout mainLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_starttrip, container, false);

        grapht = (GraphView) view.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();










        //initGraph(view);

        /**
         btnDirections = findViewById(R.id.location_directions);
         btnSearch = findViewById(R.id.location_search);
         btnStart = findViewById(R.id.navigation_start);
         btnBack = findViewById(R.id.B_back);
         searchField = findViewById(R.id.query_location);
         speedLimitText = findViewById(R.id.speedLimit);
         currentSpeedText = findViewById(R.id.currentSpeed);
         score = findViewById(R.id.score);
         mainLayout = findViewById(R.id.main_layout);*/


        // creating list to add score
        scoreList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        /***********************************I am not inicialize maps here**************************/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used


        text_accel = view.findViewById(R.id.text_accel);
        text_prev_acc = view.findViewById(R.id.text_prev_acc);


        // starting the navigation after user searches the destination on the map
        bt_startTrip = view.findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrip(view);
            }
        });

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

        // get sensorManager and initialise sensor listeners
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        initListeners();
        // wait for one second until gyroscope and magnetometer/accelerometer
        // data is initialised then scedule the complementary filter task
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                2000, TIME_CONSTANT);
        // analysing behavior every 2 sec
        fuseTimer.scheduleAtFixedRate(new BehaviorAnalysis(), 1000, 2000);

        //resetting the sensor values every 30 sec
        fuseTimer.scheduleAtFixedRate(new ResetSensorValues(), 1000, 30000);
        return view;
    }

    public void initGraph(View view){
        // Add graphs set up
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        series.setColor(Color.RED);
        series2.setColor(Color.BLUE);
        //series3.setColor(Color.GREEN);
        //graph.addSeries(series);
        //graph.addSeries(series2);
        //graph.addSeries(series3);
    }

    // initializing the sensors
    public void initListeners() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    // after maps are initialized and destination is set, the trip is started
    // check whether the location has been given by the user
    public void startTrip(View view) {
        //----------//get location of the destination ----------------------------------------------

        if (i == 0) {
            //DURING THE TRIP
            // during the start of a trip, values are initialized
            // change the button to display "End" to end the trip
            bt_startTrip.setText("END");
            tStart = System.currentTimeMillis();
            tBreakStart = System.currentTimeMillis();
            suddenBreaksCount = 0;
            suddenAcceleration = 0;
            scoreList.clear();
            i = 1;

            //checkReadings();


            //getting the latitude and longitude of the user

            // making changes to the UI
            /**
             bt_startTrip.setVisibility(View.GONE);
             btnDirections.setVisibility(View.GONE);
             searchField.setVisibility(View.GONE);
             btnBack.setVisibility(View.VISIBLE);
             currentSpeedText.setVisibility(View.VISIBLE);
             speedLimitText.setVisibility(View.VISIBLE);*/
        } else {
            // END OF THE TRIP
            // values are computed after the end of thr trip
            bt_startTrip.setText("START");
            /*****************************calculating time*********************************/
            tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            int hours = (int) (elapsedSeconds / 3600);
            int minutes = (int) ((elapsedSeconds % 3600) / 60);
            int seconds = (int) (elapsedSeconds % 60);
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            long elapsed = stop();
            double tseconds = ((double) elapsed / 1000000000.0);
            int shours = (int) (tseconds / 3600);
            int sminutes = (int) ((tseconds % 3600) / 60);
            int sseconds = (int) (tseconds % 60);
            limitExceedTime = String.format("%02d:%02d:%02d", shours, sminutes, sseconds);
            slimitExceedCount = Integer.toString(limitExceedCount);
            sMaxSpeed = Integer.toString(maxSpeed);
            i = 0;
            details.clear();
            //getting average score of the trip
            scoreArrayList = new ScoreArrayList(scoreList);
            avgScore = scoreArrayList.getAverage();
            double result = Math.round(avgScore * 100) / 100.0;

            Toast.makeText(getContext(), "Trip Score: " + result, Toast.LENGTH_LONG).show();
            onadd();
        }

    }


    // time is computed
    public long elapsed() {
        if (isRunning()) {
            if (isPaused())
                return (pausedStart - start);
            return (System.nanoTime() - start);
        } else
            return (end - start);
    }

    public String toStringText() {
        long enlapsed = elapsed();
        return ((double) enlapsed / 1000000000.0) + " Seconds";
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void start() {
        start = System.nanoTime();
        running = true;
        paused = false;
        pausedStart = -1;
    }

    public long stop() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            running = false;
            paused = false;

            return pausedStart - start;
        } else {
            end = System.nanoTime();
            running = false;
            return end - start;
        }
    }

    public long pause() {
        if (!isRunning()) {
            return -1;
        } else if (isPaused()) {
            return (pausedStart - start);
        } else {
            pausedStart = System.nanoTime();
            paused = true;
            return (pausedStart - start);
        }
    }

    public void resume() {
        if (isPaused() && isRunning()) {
            start = System.nanoTime() - (pausedStart - start);
            paused = false;
        }
    }

    /***adding values to the firebase database*/
    public void onadd() {
        details.add("Total Time: " + timeString);
        Log.d("onAdd","timeString"+timeString);
        details.add("Max Speed: " + sMaxSpeed);
        Log.d("onAdd","sMaxSpeed"+sMaxSpeed);
        details.add("LimitExceedTime: " + limitExceedTime);
        Log.d("onAdd","limitExceedTime"+limitExceedTime);
        details.add("LimitExceedCount: " + slimitExceedCount);
        Log.d("onAdd","slimitExceedCount"+slimitExceedCount);
        details.add("suddenBreaksCount: " + suddenBreaksCount);
        Log.d("onAdd","suddenBreaksCount"+suddenBreaksCount);
        details.add("suddenAcceleration: " + suddenAcceleration);
        Log.d("onAdd","suddenAcceleration"+suddenAcceleration);
        details.add("RainOrSnow: " + RainAndSnow);
        Log.d("onAdd","RainAndSnow"+RainAndSnow);
        String sScore = Double.toString(Math.round(avgScore * 100) / 100.0);
        details.add("Score :" + sScore);
        Log.d("onAdd","sScore"+sScore);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy' 'HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));
        details.add("DataAndTime :" + sdf.format(new Date()));

    }





    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        return Double.valueOf(twoDForm.format(d));
    }

    // on changing location

    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");



        latitude = location.getLatitude();
        longitude = location.getLongitude();



        double kph = (Double.parseDouble(speedlimit)) * 0.621;
        mph = (int) Math.round(kph);
        text_prev_acc.setText("Limit:" + "" + Integer.toString(mph));
        pause();
        currentSpeed = location.getSpeed() * 2.23f;


        CharSequence text = "Speed Limit Exceeded!";
        tBreakEnd = System.currentTimeMillis();
        long breakElapsed = tBreakStart - tBreakEnd;
        double breakElapsedSeconds = breakElapsed / 1000.0;
        int breakSeconds = (int) (breakElapsedSeconds % 60);
        if (breakSeconds % 5 == 0) {
            tempSpeed = currentSpeed;
        }
        if (breakSeconds % 2 == 0 && tempSpeed >= 35 && (tempSpeed - currentSpeed >= 20)) {
            // harsh brake case
            suddenBreaksCount++;
            isBrakesApplied = true;
        } else {
            isBrakesApplied = false;
        }
        if (breakSeconds % 2 == 0 && currentSpeed - tempSpeed >= 20) {
            // sudden acceleration case
            suddenAcceleration++;
        }
        // determining speed and over the speed cases
        if (currentSpeed > mph) {
            if (!isRunning()) {
                start();
            } else {
                resume();
            }
            if (flag == 0) {
                limitExceedCount++;
                flag = 1;
            }
        }
        if (currentSpeed < mph) {
            flag = 0;
        }
        if (maxSpeed < currentSpeed) {
            maxSpeed = (int) currentSpeed;
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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




    //Available in the entire application - the realtime chart
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0, 1),
    });
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
    });






    //variable for view port
    private Viewport viewport;
    private int pointsPlotted = 10000;

    // SENSOR PART, COMPUTATION AND ANALYSIS
    // Sensor Fusion involving Accelerometer, Gyroscope, and Magnetometer
    // Quaternion
    // Accelerometer
    @Override
    public void onSensorChanged(SensorEvent event) {
        updateValues();
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
                //checkReadings();



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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    int x = 1,y=5;
    // getting accelerometer values and calibrating the accelerometer
    private float calibrateAccelerometer() {
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
        }


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        double y;
        for(int x=0; x<90;x++) {
            y = Math.sin(2 * x * 0.2) - 2 * Math.sin(x * 0.2);
            series.appendData(new DataPoint(x, y), true, 90);
        }

        /**
         double y;
         for(int x=0; x<90;x++){
         y=Math.sin(2*x*0.2)-2*Math.sin(x*0.2);
         series.appendData(new DataPoint(x,y),true, 90);

         }*/
        /**Log.d("calibrateAccelerometer","xAccCalibrated"+xPreviousAcc + "x:"+x);

        //Set color title courve, datapoints radious, thickness




        series.appendData(new DataPoint(x, y), true, 2000);
        grapht.addSeries(series);
        x++;
        y++;
        if (x == 2000) {
            x = 1;
            series.resetData(new DataPoint[]{new DataPoint(1, 0)});
        }
        if (x==3){
            y=1;

        }*/

        return xAccCalibrated;
    }

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
            }
        }
    }

    // updating the values for accelerometer, sensor fusion and quaternion
    // and computing the counters for quaternion, sensor fusion, accelerometer
    private void updateValues() {
        if (newPitchOut != 0 && newPitchOutQ != 0 && newYawOut != 0 && newYawOutQ != 0 && xAccCalibrated != 0 && yAccCalibrated != 0) {
            writeCheck = false;
            xAccChange = false;
            yAccChange = false;
            count = count + 1;
            if (count == 2250) {
                count = 1;
            }

            if (newYawOut > .30 || newYawOut < -.30) {
                overYaw = overYaw + 1;
                writeCheck = true;
            }

            if (newPitchOut > .12 || newPitchOut < -.12) {
                overPitch = overPitch + 1;
                writeCheck = true;
            }

            if (newYawOutQ > .30 || newYawOutQ < -.30) {
                overYawQ = overYawQ + 1;
                writeCheck = true;
            }

            if (newPitchOutQ > .12 || newPitchOutQ < -.12) {
                overPitchQ = overPitchQ + 1;
                writeCheck = true;
            }

            if (xAccCalibrated > 3 || xAccCalibrated < -3) {
                overX = overX + 1;
                writeCheck = true;
                xAccChange = true;
            }

            if (yAccCalibrated > 2.5 || yAccCalibrated < -2.5) {
                overY = overY + 1;
                writeCheck = true;
                yAccChange = true;
            }

            // computing final values for pitch and yaw counters
            if (overPitch != 0 || overPitchQ != 0) {
                finalOverPitch = (int) (overPitch + 0.3 * overPitchQ);
            }

            if (overYaw != 0 || overYawQ != 0) {
                finalOverYaw = (int) (overYaw + 0.4 * overYawQ);
            }

            /*

            Here, one counter on any sensor doesn't reflect the crossing of threshold for 1 time,
            it just gives the total number of times the data was recorded during "1 crossing"
            For one time the user makes a rash turn, counter was reach upto 10 for that one single incident

            */

            // only saving if there is change in the counters (for future purpose also)
            if (writeCheck) {
                //Creating a shared preference
                SharedPreferences sharedPreferences =  getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                //Creating editor to store values to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //Adding values to editor
                editor.putInt("overPitch", finalOverPitch);
                editor.putInt("overYaw", finalOverYaw);
                editor.putInt("overX", overX);
                editor.putInt("overY", overY);

                //Saving values to editor
                editor.commit();
                Log.i("MapsActivity", "finalOverPitch : " + finalOverPitch);
            }
        }
    }

    // accelerometer
    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

    // gyroscope
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

    // gyroscope
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

    // sensor fusion values are computed at every 10 sec as initialized earlier
    private class calculateFusedOrientationTask extends TimerTask {
        float filter_coefficient = 0.85f;
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

    // analysis of driver behavior, computation is done at every 2 sec
    private class BehaviorAnalysis extends TimerTask {

        float speedLimit;
        // factors needed for analysis
        int factorSpeed = 0;
        int factorBrakes = 0;
        int factorWeather = 0;
        int factorAcceleration = 0;
        int factorTurn = 0;

        //calculate rateOverYaw and rateOverPitch by taking the division of pitch/yaw over 30 sec interval
        double rateOverPitch = finalOverPitch / count;
        double rateOverYaw = finalOverYaw / count;

        @Override
        public void run() {
            // see flowchart in the report to better understand the analysis
            if (mph != 0) {
                speedLimit = mph;
            } else {
                speedLimit = 0;
            }

            if (currentSpeed != 0) {
                if (currentSpeed > speedLimit) {
                    factorSpeed = 10;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mainLayout, "You speed is above the limit, please drive within the speedlimit", Snackbar.LENGTH_SHORT).show();
                            playSound();
                        }
                    });
                } else {
                    factorSpeed = 1;
                }

                if (isBrakesApplied == true) {
                    factorBrakes = 10;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mainLayout, "You shouldn't apply sudden brakes, please be careful", Snackbar.LENGTH_SHORT).show();
                            playSound();
                        }
                    });
                } else {
                    factorBrakes = 0;
                }

                if (RainAndSnow == true) {
                    factorWeather = 10;
                } else {
                    factorWeather = 0;
                }

                // writeCheck is the boolean used above to indicate the change in counters in turn and acc
                if (writeCheck == true) {

                    if (rateOverPitch < 0.04) {
                        if (xAccChange == true) {
                            // likely unsafe
                            factorAcceleration = 8;
                        } else {
                            // likely safe
                            factorAcceleration = 2;
                        }
                    } else {
                        if (xAccChange == true) {
                            // definitely unsafe
                            factorAcceleration = 10;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(mainLayout, "Harsh acceleration has been detected, please be safe", Snackbar.LENGTH_SHORT).show();
                                    playSound();
                                }
                            });
                        } else {
                            // probably unsafe
                            factorAcceleration = 8;
                        }
                    }

                    if (rateOverYaw < 0.01) {
                        if (yAccChange == true) {
                            // likely unsafe
                            factorTurn = 8;
                        } else {
                            // likely safe
                            factorTurn = 2;
                        }
                    } else {
                        if (yAccChange == true) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(mainLayout, "Harsh unsafe turn has been detected, please be safe", Snackbar.LENGTH_SHORT).show();
                                    playSound();
                                }
                            });
                            // definitely unsafe
                            factorTurn = 10;
                        } else {
                            // probably unsafe
                            factorTurn = 8;
                        }
                    }
                } else {
                    factorAcceleration = 0;
                    factorTurn = 0;
                }
            }
            double unsafeScore = 0.3 * factorSpeed + 0.2 * factorBrakes + 0.2 * factorWeather + 0.2 * factorAcceleration + 0.2 * factorTurn;
            if (unsafeScore < 10) {
                safeScore = 10 - unsafeScore;
            }

            if (unsafeScore > 10) {
                safeScore = 0;
            }

            // taking average with the previous score of user
            if (previousScore != 0) {
                safeScore = (safeScore + previousScore) / 2;
            }
            scoreList.add(safeScore);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text_accel.setText("Score : " + d.format(safeScore));
                }
            });
            previousScore = safeScore;
            Log.i("MapsActivity", "count : " + count);
            Log.i("MapsActivity", "score : " + safeScore);
            Log.i("MapsActivity", "final Pitch rate : " + rateOverPitch);
            Log.i("MapsActivity", "final Yaw rate : " + rateOverYaw);
        }
    }

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
            Log.i("MapsActivity", "final Pitch : " + finalOverPitch);
            Log.i("MapsActivity", "final Yaw : " + finalOverYaw);
            Log.i("MapsActivity", "final overX : " + overX);
            Log.i("MapsActivity", "final overY : " + overY);
        }
    }

    // for notification - sound
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
}

