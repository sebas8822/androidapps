package com.finalproyect.niftydriverapp;

import static android.Manifest.*;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.finalproyect.niftydriverapp.ui.mytrips.MyTripsFragment;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;
import com.finalproyect.niftydriverapp.ui.starttrip.StartTripFragment;
import com.finalproyect.niftydriverapp.ui.tripView.TripViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    private Button bt_startTrip;

    //Buttons profile fragment
    private Button bt_scoreView,bt_graphView;


    //Global variable
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //disable night mode but show a bug can be better show
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(item -> onBottonNavigationItemClicked(item));
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


        /** This is before class

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_startTrip, R.id.navigation_tripView, R.id.navigation_myTrips)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //bt_scoreView = findViewById(R.id.bt_scoreView);*/



        /**
        bt_startTrip = findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"This is graph view",Toast.LENGTH_LONG).show();
                bt_startTrip.setText("another view");

            }
        });*/





    }

    private boolean onBottonNavigationItemClicked(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_profile:
                loadFragment(new ProfileFragment());
                return true;
            case R.id.navigation_startTrip:
                loadFragment(new StartTripFragment());
                return true;
            case R.id.navigation_tripView:
                loadFragment(new TripViewFragment());
                return true;
            case R.id.navigation_myTrips:
                loadFragment(new MyTripsFragment());
                return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .commit();

    }


    // call top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bt_settings:
                Toast.makeText(this,"Settings selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.bt_logout:
                Toast.makeText(this,"Logout selected",Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }



}