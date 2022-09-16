package net.mypapit.mobile.speedmeter;
/*
*
*
* Copyright 2017 Mohammad Hafiz bin Ismail <mypapit@gmail.com>

Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions
and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {


    private TextView tvSpeed, tvUnit, tvLat, tvLon, tvAccuracy, tvHeading, tvMaxSpeed;
    private static final String[] unit = {"km/h", "mph", "meter/sec", "knots"};
    private int unitType;
    private NotificationCompat.Builder mbuilder;
    private NotificationManager mnotice;
    private double maxSpeed = -100.0;
    private MainActivity activity;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvMaxSpeed = (TextView) findViewById(R.id.tvMaxSpeed);
        tvUnit = (TextView) findViewById(R.id.tvUnitc);
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);
        tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        Typeface font = Typeface.createFromAsset(getBaseContext().getAssets(), "font/lcdn.ttf");
        tvSpeed.setTypeface(font);
        tvLat.setTypeface(font);
        tvLon.setTypeface(font);
        tvHeading.setTypeface(font);
        tvAccuracy.setTypeface(font);
        tvMaxSpeed.setTypeface(font);

        activity = this;
        //for handling notification
        mbuilder = new NotificationCompat.Builder(this);
        mnotice = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //FOR SAVE REFERENCES
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //SET UP UNITS PREFERENCES
        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        tvUnit.setText(unit[unitType - 1]);


        //SAVE VALUE INTO THE CURRENT ACTIVITY
        if (savedInstanceState !=null) {
            maxSpeed = savedInstanceState.getDouble("maxspeed",-100.0);

        }
        // check if the services are available ASK TO THE USER TO ENABLE
        if (!this.isLocationEnabled(this)) {


            //show dialog if Location Services is not enabled


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_not_found_title);  // GPS not found
            builder.setMessage(R.string.gps_not_found_message); // Want to enable?
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(intent);
                }
            });

            //if no - bring user to selecting Static Location Activity
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

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


        new SpeedTask(this).execute("string");
    }
    // save into the the sabe activity
    protected void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putDouble("maxspeed",maxSpeed);
    }

    // THIS BRING DE DATA WHEN THE APPLICATION RUN AGAIN
    protected void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);

        maxSpeed = bundle.getDouble("maxspeed",-100.0);

    }

    // Speed Longitud and Lantitud
    protected void onResume() {
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        maxSpeed = prefs.getFloat("maxspeed",-100.0f);


        tvUnit.setText(unit[unitType - 1]);

        if (maxSpeed > 0){

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

        float tempMaxpeed=0.0f;
        try {

            tempMaxpeed=Float.parseFloat(tvMaxSpeed.getText().toString());


        } catch (java.lang.NumberFormatException nfe) {

            tempMaxpeed=0.0f;

        }

        prefs.edit().putFloat("maxSpeed",tempMaxpeed);


    }

    //THIS CLASS PROVIDE THE VARIABLES SPEED lONGITUD AND lATITUD
    private class SpeedTask extends AsyncTask<String, Void, String> {
        final MainActivity activity;
        float speed = 0.0f;
        double lat;
        double lon;
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

                    filtSpeed = filter(filtSpeed, localspeed, 2);



                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(2);


                    lon = location.getLongitude();
                    lat = location.getLatitude();

                    Log.d("net.mypapit.speedview", "Speed " + localspeed + "latitude: " + lat + " longitude: " + location.getLongitude());
                    tvSpeed.setText(numberFormat.format(filtSpeed));

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


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);


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


}


