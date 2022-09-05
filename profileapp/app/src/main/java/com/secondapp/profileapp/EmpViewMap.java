package com.secondapp.profileapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EmpViewMap extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;


    /**-----------------Test Data------------------*/
    //String empLocation = "9 Seaview Avenue, Port Macquarie ";
    //String empLocation = "Colombia";


    /*** ------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_address);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Default location
        mMap = googleMap;
        LatLng Sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(Sydney).title("Marker in Sydney"));
        // Functions
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Sydney));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Sydney, 5);
        mMap.animateCamera(cameraUpdate);
        // Check if there is extra intent I used to pass the location to show
        if (getIntent().hasExtra("emp_address")) {
            String empLocation = getIntent().getStringExtra("emp_address");
            Toast.makeText(this.getApplicationContext(), empLocation + "now you are in maps", Toast.LENGTH_SHORT).show();

            // bring the location related to the address
            Geocoder geocoder = new Geocoder(EmpViewMap.this, Locale.getDefault());

            try {
                // define the list address to show the first one
                List<Address> listAddress = geocoder.getFromLocationName(empLocation, 1);
                if (listAddress.size() > 0) {
                    LatLng addressEmp = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());

                    mMap.addMarker(new MarkerOptions().position(addressEmp).title("Employee Address"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(addressEmp));
                    CameraUpdate cameraUpdateemp = CameraUpdateFactory.newLatLngZoom(addressEmp, 15);
                    mMap.animateCamera(cameraUpdateemp);
                } else {
                    Toast.makeText(EmpViewMap.this, "Address no found", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
