package com.learnacc.accelerometergraph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Magnifier;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**Resources
 * Sensor manager android developer - https://developer.android.com/reference/android/hardware/SensorManager
 *
 *
 * */


public class MainActivity extends AppCompatActivity {


    // VARIABLES
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyro;





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
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //new DataPoint(0, 1),

    });
    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
            //new DataPoint(0, 1),

    });




    private SensorEventListener sensorEventListener = new SensorEventListener() { // trigger every time when the sensor is change
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // to determine how much the phone is going to move
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];


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

            //prog_shakebar.setProgress((int)changeInAcceleration);
             /**
            //change colors base on shaking
            if (changeInAcceleration>14){
                text_accel.setBackgroundColor(Color.RED);
            }
            else if(changeInAcceleration>5){
                text_accel.setBackgroundColor(Color.parseColor("#a88932"));
            }
            else if(changeInAcceleration>2){
                text_accel.setBackgroundColor(Color.YELLOW);
            }
            else{
                text_accel.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
            }
            */
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
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) { //main activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // association with the actual id
        text_accel = findViewById(R.id.text_accel);
        text_prev_acc = findViewById(R.id.text_prev_acc);
        text_curr_acc = findViewById(R.id.text_curr_acc);

        prog_shakebar = findViewById(R.id.prog_shakebar);

        // initialization Sensor objects
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);// means get it from service built into the android system
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// define the default sensor that is looking for "accelerometer"


        // Add graphs set up
        GraphView graph = (GraphView) findViewById(R.id.graph);
        //to set some properties to use the graph
        viewport = graph.getViewport();// the variable is declare to be used in whole app
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        series.setColor(Color.RED);
        series2.setColor(Color.BLUE);
        series3.setColor(Color.GREEN);
        graph.addSeries(series);
        graph.addSeries(series2);
        graph.addSeries(series3);





    }

    protected void onResume() {
        super.onResume();
        // sensor manager is going to use this sensor function
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }





}