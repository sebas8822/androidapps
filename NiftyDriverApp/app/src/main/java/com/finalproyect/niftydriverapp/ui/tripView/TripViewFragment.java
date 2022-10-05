package com.finalproyect.niftydriverapp.ui.tripView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.databinding.FragmentTripviewBinding;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.ui.fragIndicators.GraphView_Tripview;
import com.finalproyect.niftydriverapp.ui.fragIndicators.ScoreViewTripView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * https://www.youtube.com/watch?v=wRDLjUK8nyU    https://www.youtube.com/watch?v=b5U8WZM45aY https://www.youtube.com/watch?v=NOVacL7ZPrc  https://www.youtube.com/watch?v=xl0GwkLNpNI
 */ // to draw the route

public class TripViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
    Trip lastTrip;


    GoogleMap mMap;
    Marker marker;


    AppDatabase db;
    DAO dao;

    private FragmentTripviewBinding binding;

    List<Trip> tripList;


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

        setUserId(userId);
        setPosition(position);

        Toast.makeText(getContext(), "Position After call from SP: " + position + "User" + userId, Toast.LENGTH_LONG).show();

        db = AppDatabase.getDbInstance(getContext());
        dao = db.driverDao();
        tripList = dao.getAllTripsByUser(userId);


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
        bt_scoreViewTripView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
        bt_scoreViewTripView.setTextColor(getResources().getColor(R.color.white));


        if (tripList.isEmpty()) {

            settingViewEMPTY();
        } else {
            lastTrip = tripList.get(position);


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
                bt_scoreViewTripView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
                bt_scoreViewTripView.setTextColor(getResources().getColor(R.color.white));
                bt_graphViewTripView.setBackgroundColor(getResources().getColor(R.color.background_color));
                bt_graphViewTripView.setTextColor(getResources().getColor(R.color.black));
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
                bt_graphViewTripView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
                bt_graphViewTripView.setTextColor(getResources().getColor(R.color.white));
                bt_scoreViewTripView.setBackgroundColor(getResources().getColor(R.color.background_color));
                bt_scoreViewTripView.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(getContext(), "Graph View View", Toast.LENGTH_LONG).show();
                GraphView_Tripview graphView = new GraphView_Tripview();
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
        tv_startTripLocation.setText(lastTrip.getStartLocationName());
        tv_endTripLocation.setText(lastTrip.getEndLocationName());
        tv_tripViewData.setText(getDateFromMillis(lastTrip.getStartDate()));
        tv_totalKilometresTripsview.setText(String.format("%.1f", lastTrip.getKilometers()));

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


    /**************************************************************/



    //Maps implementation
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (tripList.isEmpty()) {
            LatLng emptyView = new LatLng(-44, 113);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emptyView, 13));
        } else {


            List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(lastTrip.getTripId());
            // Add a marker in Sydney and move the camera
            LatLng Start_location = new LatLng(lastTrip.getStartLocationLAT(), lastTrip.getStartLocationLON());
            LatLng End_location = new LatLng(lastTrip.getEndLocationLAT(), lastTrip.getEndLocationLON());
            marker = mMap.addMarker(new MarkerOptions().position(Start_location).title("Start Trip"));


            marker = mMap.addMarker(new MarkerOptions().position(End_location).title("End Trip").icon(bitmapDescriptorFromVector(getContext(), R.drawable.img_5)));


            PolylineOptions rectOption = new PolylineOptions().color(Color.BLUE);

            addPolyLines(rectOption);

            //Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            //addPolyLines(rectOption);

            Polyline polyline = mMap.addPolyline(rectOption);

            LatLng starMarker = new LatLng(fusionSensors.get(0).getCurLocationLAT(), fusionSensors.get(0).getCurLocationLON());
            LatLng endMarker = new LatLng(fusionSensors.get(fusionSensors.size()-1).getCurLocationLAT(), fusionSensors.get(fusionSensors.size()-1).getCurLocationLON());

            //LatLng midle = new LatLng(fusionSensors.get((fusionSensors.size())/2).getCurLocationLAT(),fusionSensors.get((fusionSensors.size())/2).getCurLocationLON());



            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Start_location, 13));
            //LatLngBounds bounds=starMarker;

            LatLngBounds bounds = null;


             try {

                 LatLngBounds.Builder builder = new LatLngBounds.Builder();
                 builder.include(starMarker);
                 builder.include(endMarker);
                 bounds = builder.build();


             } catch (Exception e) {
                 LatLngBounds.Builder builder = new LatLngBounds.Builder();
                 builder.include(endMarker);
                 builder.include(starMarker);
                 bounds = builder.build();
             }
           // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(midle, 13);
            LatLngBounds finalBounds = bounds;
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                            @Override
                                            public void onMapLoaded() {
                                                int padding = 200; // padding around start and end marker
                                                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(finalBounds.getCenter(), padding);
                                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(finalBounds, padding);
                                                mMap.animateCamera(cameraUpdate);
                                            }


            });


            mMap.setOnMarkerClickListener(this);

        }
    }

    public void addPolyLines(PolylineOptions rectOption) {


        boolean acceleration;
        boolean braking;
        boolean left;
        boolean right;
        boolean speed;
        int count;
        int count2 = 0;
        int accCount = 0;
        int brakingCount = 0;
        int LeftCount = 0;
        int RightCount = 0;

        List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(lastTrip.getTripId());

        for (FusionSensor fusionSensor : fusionSensors) {

            acceleration = fusionSensor.isHardAcc();
            braking = fusionSensor.isHardDes();
            left = fusionSensor.isSharpLeft();
            right = fusionSensor.isSharpRight();

            if (acceleration == true) {
                accCount++;
                LatLng braking_point = new LatLng(fusionSensor.getCurLocationLAT(), fusionSensor.getCurLocationLON());
                marker = mMap.addMarker(new MarkerOptions().position(braking_point).title("Hard Acceleration").icon(bitmapDescriptorFromVector(getContext(), R.drawable.img_2)));
            }

            if (braking == true) {
                LatLng braking_point = new LatLng(fusionSensor.getCurLocationLAT(), fusionSensor.getCurLocationLON());
                marker = mMap.addMarker(new MarkerOptions().position(braking_point).title("Hard Brake").icon(bitmapDescriptorFromVector(getContext(), R.drawable.img_3)));
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_gps_fixed_24));
            }

            if (left == true) {
                LeftCount++;
                LatLng braking_point = new LatLng(fusionSensor.getCurLocationLAT(), fusionSensor.getCurLocationLON());
                marker = mMap.addMarker(new MarkerOptions().position(braking_point).title("Hard Cornering").icon(bitmapDescriptorFromVector(getContext(), R.drawable.img_4)));
            }

            if (right == true) {
                RightCount++;
                LatLng braking_point = new LatLng(fusionSensor.getCurLocationLAT(), fusionSensor.getCurLocationLON());
                marker = mMap.addMarker(new MarkerOptions().position(braking_point).title("Hard Cornering").icon(bitmapDescriptorFromVector(getContext(), R.drawable.img_4)));
            }


            LatLng location = new LatLng(fusionSensor.getCurLocationLAT(), fusionSensor.getCurLocationLON());


            rectOption.add(location);
        }


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //Toast.makeText(getContext(),"hola from marker",Toast.LENGTH_LONG).show();

        if (marker.getTitle().equals("Hard Brake")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Hard Brake");
            builder.setMessage("Please anticipate where you are going to stop an press the brake soft with enough distance from another car");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (marker.getTitle().equals("Hard Acceleration")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Hard Acceleration");
            builder.setMessage("Try to accelerate soft you can avoid accidents and reduce petrol consume");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (marker.getTitle().equals("Hard Cornering")) {


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Hard Cornering");
            builder.setMessage("anticipate corners with the correct speed to turn smoothly or change the line smoothly you can avoid accidents");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return false;
    }


    // method definition
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResID) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResID);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);


        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}