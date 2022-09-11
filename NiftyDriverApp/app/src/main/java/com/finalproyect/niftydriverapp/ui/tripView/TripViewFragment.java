package com.finalproyect.niftydriverapp.ui.tripView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.databinding.FragmentTripviewBinding;
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

public class TripViewFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap mMap;


    private FragmentTripviewBinding binding;

    /**---------------------------------------------*/
    String empLocation = "9 Seaview Avenue, port macquarie ";
    //String empLocation = "Colombia";


    /**---------------------------------------------*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tripview, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /////////////////////////////////////////////////////////////////////////////////////
        return view;
    }

    //Maps implementation
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(Sydney).title("Marker in Sydney"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Sydney));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Sydney,5);
        mMap.animateCamera(cameraUpdate);

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            List<Address> listAddress=geocoder.getFromLocationName(empLocation,1);
            if(listAddress.size()>0){
                LatLng addressEmp = new LatLng(listAddress.get(0).getLatitude(),listAddress.get(0).getLongitude());

                mMap.addMarker(new MarkerOptions().position(addressEmp).title("Employee Address"));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(addressEmp));
                CameraUpdate cameraUpdateemp = CameraUpdateFactory.newLatLngZoom(addressEmp,15);
                mMap.animateCamera(cameraUpdateemp);
            }
            else{
                Toast.makeText(getContext(),"Address no found", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}