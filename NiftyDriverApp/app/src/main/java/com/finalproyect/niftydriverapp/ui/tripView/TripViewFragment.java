package com.finalproyect.niftydriverapp.ui.tripView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.databinding.FragmentTripviewBinding;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.ui.fragIndicators.GraphView;
import com.finalproyect.niftydriverapp.ui.fragIndicators.ScoreViewTripView;
import com.finalproyect.niftydriverapp.ui.settings.Settings_Activity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * https://www.youtube.com/watch?v=wRDLjUK8nyU    https://www.youtube.com/watch?v=b5U8WZM45aY https://www.youtube.com/watch?v=NOVacL7ZPrc  https://www.youtube.com/watch?v=xl0GwkLNpNI
 */ // to draw the route

public class TripViewFragment extends Fragment implements OnMapReadyCallback {

    Button bt_scoreViewTripView, bt_graphViewTripView;
    ImageButton bt_leftTripButton, bt_rightButton;
    TextView tv_startTrip, tv_endTrip, tv_startTripLocation, tv_endTripLocation, tv_totalKilometresTripsview, tv_ScoreTripView, tv_totalTimeView, tv_titleTripsView, tv_tripViewData;


    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    long userId;
    int position;

    GoogleMap mMap;


    private FragmentTripviewBinding binding;


    String empLocation = "9 Seaview Avenue, port macquarie ";
    //String empLocation = "Colombia";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    SharedPreferences sp;//Init sharepreferences for user
    SharedPreferences.Editor editor;

    // for save preferences like user id and user state means open session
    @Override
    public void onAttach(@NonNull Context context) {
        sp = context.getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit(); // init sharedPreferences
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tripview, container, false);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Init shared preferences
        sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        long userId = sp.getLong("userId", 0);
        int position = sp.getInt("position", 0);
        ;
        setUserId(userId);
        setPosition(position);

        Toast.makeText(getContext(), "Position After call from SP: " + position + "User" + userId, Toast.LENGTH_LONG).show();

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        List<Trip> tripList = dao.getAllTripsByUser(userId);


        /**Set the views*/


        tv_startTrip = view.findViewById(R.id.tv_startTrip);
        tv_endTrip = view.findViewById(R.id.tv_endTrip);
        tv_startTripLocation = view.findViewById(R.id.tv_startTripLocation);
        tv_endTripLocation = view.findViewById(R.id.tv_endTripLocation);
        tv_tripViewData = view.findViewById(R.id.tv_tripViewData);
        tv_totalKilometresTripsview = view.findViewById(R.id.tv_totalKilometresTripsview);
        tv_ScoreTripView = view.findViewById(R.id.tv_ScoreTripView);
        tv_totalTimeView = view.findViewById(R.id.tv_totalTimeView);
        tv_titleTripsView = view.findViewById(R.id.tv_titleTripsView);


        bt_leftTripButton = view.findViewById(R.id.bt_leftTripButton);
        bt_rightButton = view.findViewById(R.id.bt_rightButton);
        bt_graphViewTripView = (Button) view.findViewById(R.id.bt_graphViewTripView);
        bt_scoreViewTripView = (Button) view.findViewById(R.id.bt_scoreViewTripView);

        if (tripList.isEmpty()) {

            settingViewEMPTY();
        } else {
            Trip lastTrip = tripList.get(position);


            settingView(lastTrip);
        }


        bt_leftTripButton.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext(),"Position before: Left " + position,Toast.LENGTH_LONG).show();
                replaceViewDirection("left", position, tripList);
                //Toast.makeText(getContext(),"Position After left: " + position + "User" + userId ,Toast.LENGTH_LONG).show();


            }
        });


        bt_rightButton.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View view) {

                replaceViewDirection("right", position, tripList);
                //Toast.makeText(getContext(),"Position After Right: " + position + "User" + userId ,Toast.LENGTH_LONG).show();


            }
        });


        bt_scoreViewTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Score View", Toast.LENGTH_LONG).show();
                ScoreViewTripView scoreView = new ScoreViewTripView();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_tripView_view, scoreView);
                fragmentTransaction.commit();
            }
        });


        bt_graphViewTripView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Graph View View", Toast.LENGTH_LONG).show();
                GraphView graphView = new GraphView();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_tripView_view, graphView);
                fragmentTransaction.commit();
            }
        });


        /////////////////////////////////////////////////////////////////////////////////////
        return view;
    }


    public void replaceViewDirection(String direction, int position, List<Trip> tripList) {
        //left = 1
        //Right = 0


        if (direction == "left") {
            if (position > 0) {
                //Toast.makeText(getContext(), "Decrease 1" + "CPos" + position, Toast.LENGTH_LONG).show();
                position--;
                /**Pass this values to the shared preference*/
                editor.putInt("position", position);
                editor.commit();
                replaceTripView();
            } else {
                Toast.makeText(getContext(), "First Trip you can't back" + position, Toast.LENGTH_LONG).show();
                //can be eliminated
            }
        }
        if (direction == "right") {
            if (position < (tripList.size() - 1)) {
                //Toast.makeText(getContext(), "Increase 1" + "CPos" + position + " Size list" + tripList.size(), Toast.LENGTH_LONG).show();
                position++;
                /**Pass this values to the shared preference*/
                editor.putInt("position", position);
                editor.commit();
                replaceTripView();
            } else {
                Toast.makeText(getContext(), "It is you last trip" + tripList.size() + " ", Toast.LENGTH_LONG).show();

            }
        }


    }

    public void replaceTripView() {

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new TripViewFragment())
                .commit();
    }


    public void settingView(Trip lastTrip) {
        /**Set values*/
        tv_startTrip.setText(getTimeFromMillis(lastTrip.getStartTime()));
        tv_endTrip.setText(getTimeFromMillis(lastTrip.getEndTime()));
        tv_startTripLocation.setText(getStartAddressLocation(lastTrip));
        tv_endTripLocation.setText(getEndAddressLocation(lastTrip));
        tv_tripViewData.setText(getDateFromMillis(lastTrip.getStartDate()));
        tv_totalKilometresTripsview.setText(String.valueOf((int) lastTrip.getKilometers()));

        tv_ScoreTripView.setText(String.valueOf((int) lastTrip.getScoreTrip()));
        float timeTrip = (float) lastTrip.getTimeTrip();
        if (timeTrip > 60) {
            tv_titleTripsView.setText("Hours");
            timeTrip = timeTrip / 60;
        }
        tv_totalTimeView.setText(String.format("%.1f", timeTrip));
    }

    public void settingViewEMPTY() {
        /**Set values*/
        long currentTime = System.currentTimeMillis();
        tv_startTrip.setText(getTimeFromMillis(currentTime));
        tv_endTrip.setText(getTimeFromMillis(currentTime));
        tv_startTripLocation.setText("No location available");
        tv_endTripLocation.setText("No location available");
        tv_tripViewData.setText(getDateFromMillis(currentTime));
        tv_totalKilometresTripsview.setText("0");

        tv_ScoreTripView.setText("0");

        tv_totalTimeView.setText("0");

        /**Inflate**/
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Trips Available!!");
        builder.setMessage("Please start a trip");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /*****************************Test Data******************************/
    Geocoder geocoder;

    private String getStartAddressLocation(Trip trip) {

        geocoder = new Geocoder(getContext());
        List<Address> geoResult = findGeocoder(trip.getStartLocationLAT(), trip.getStartLocationLON());
        Address thisAddress = geoResult.get(0);
        Log.d("testAddres", "address: " + thisAddress.getAddressLine(0));
        return String.valueOf(thisAddress.getAddressLine(0));

    }

    private String getEndAddressLocation(Trip trip) {
        geocoder = new Geocoder(getContext());

        List<Address> geoResult = findGeocoder(trip.getEndLocationLAT(), trip.getEndLocationLON());
        Address thisAddress = geoResult.get(0);
        Log.d("testAddres", "address: " + thisAddress.getAddressLine(0));
        return String.valueOf(thisAddress.getAddressLine(0));
    }


    private String getDateFromMillis(long dateMillis) {
        Date startDate = new Date(dateMillis);
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm");


        return df.format(startDate);
    }

    private String getTimeFromMillis(long timeMillis) {
        Date millis = new Date(timeMillis);

        DateFormat df = new SimpleDateFormat("HH:mm");

        return df.format(millis);
    }

    private List<Address> findGeocoder(Double lat, Double lon) {
        final int maxResults = 1;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    /**************************************************************/


    //Maps implementation
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(Sydney).title("Marker in Sydney"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Sydney));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Sydney, 5);
        mMap.animateCamera(cameraUpdate);

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            List<Address> listAddress = geocoder.getFromLocationName(empLocation, 1);
            if (listAddress.size() > 0) {
                LatLng addressEmp = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());

                mMap.addMarker(new MarkerOptions().position(addressEmp).title("Employee Address"));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(addressEmp));
                CameraUpdate cameraUpdateemp = CameraUpdateFactory.newLatLngZoom(addressEmp, 15);
                mMap.animateCamera(cameraUpdateemp);
            } else {
                Toast.makeText(getContext(), "Address no found", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}