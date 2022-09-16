package com.finalproyect.niftydriverapp.ui.starttrip;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.finalproyect.niftydriverapp.MainActivity;

public class Geolocation extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 99;
    LocationManager locationManager;
    Context context;
    private Activity activity;
    double latitude;
    double longitude;
    double speed;

    //constructor
    public Geolocation(LocationManager locationManager,Context context ){
        this.locationManager = locationManager;
        this.context = context;
        getAddress();

    }

    public double getLatitude() {
        geolocation();
        return latitude;
    }

    public void setLatitude(double latitude) {
        geolocation();
        this.latitude = latitude;
    }

    public double getLongitude() {
        geolocation();
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        geolocation();
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }





    public void getAddress(){




        /***********Check if gps is enable or not********** */
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Fuction to enable GPS
            turnOnGPS();

        }else{

            //GPS is already on 
            geolocation();
        }



    }

    private void geolocation() {
        //check permitions again
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);



        }else{
            Location LocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGPS !=null){
                latitude = LocationGPS.getLatitude();
                longitude = LocationGPS.getLongitude();
                speed = LocationGPS.getSpeed();

                /**Show the values and save them**/

                /*********************************/
            }else if (LocationNetwork !=null){
                latitude = LocationNetwork.getLatitude();
                longitude = LocationNetwork.getLongitude();
                speed = LocationNetwork.getSpeed();

                /**Show the values and save them**/

                /*********************************/
            }else if (LocationPassive !=null){
                latitude = LocationPassive.getLatitude();
                longitude = LocationPassive.getLongitude();
                speed = LocationPassive.getSpeed();

                /**Show the values and save them**/

                /*********************************/
            }else{
                Toast.makeText(getApplicationContext(),"Can't get current location",Toast.LENGTH_LONG).show();



            }

        }

    }

    private void turnOnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

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


}
