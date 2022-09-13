package com.finalproyect.niftydriverapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.loginfragments.LoginFragment;
import com.finalproyect.niftydriverapp.ui.loginfragments.SignUpFragment;
import com.finalproyect.niftydriverapp.ui.mytrips.MyTripsFragment;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;
import com.finalproyect.niftydriverapp.ui.settings.ChangeParametersFragment;
import com.finalproyect.niftydriverapp.ui.settings.SettingsFragment;
import com.finalproyect.niftydriverapp.ui.starttrip.StartTripFragment;
import com.finalproyect.niftydriverapp.ui.tripView.TripViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.finalproyect.niftydriverapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CallBackFragment{

    private ActivityMainBinding binding;
    CallBackFragment callBackFragment;



    private Button bt_startTrip;

    //Buttons profile fragment
    private Button bt_scoreView,bt_graphView;

    SharedPreferences sp;







    //Global variable
    BottomNavigationView bottomNavigationView;
    private int intLayout = 1;


    // for save preferences like user id and user state means open session

    // for save preferences like user id and user state means open session


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init shared preferences
        sp = getApplicationContext().getSharedPreferences("userProfile",Context.MODE_PRIVATE);
        //editor.putBoolean("userState", false);
        //editor.commit();
        long userid = sp.getLong("userId",0);
        boolean userState = sp.getBoolean("userState",false);
        Toast.makeText(getApplicationContext(),"UserId "+ userid +" userState " + userState, Toast.LENGTH_LONG).show();



        // depending of the user state
        if(userState == true){
            //disable night mode but show a bug can be better show

            intLayout = 1;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setContentView(R.layout.activity_main);

            bottomNavigationView = findViewById(R.id.nav_view);
            bottomNavigationView.setOnItemSelectedListener(item -> onBottonNavigationItemClicked(item));
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);

        }else {
            setContentView(R.layout.activity_login);
            addFragmentLogin();
        }




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

    private void loadSettingsFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_settings, fragment)
                .commit();

    }

    private void addFragmentLogin(){
        LoginFragment fragment = new LoginFragment();
        fragment.setCallBackFragment(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_login, fragment)
                .commit();

    }

    private void addFragmentSetting(){
        SettingsFragment fragment = new SettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()

                .add(R.id.fragment_container_settings, fragment)
                .addToBackStack(null)
                .commit();

    }

    private void replaceFragmentLogin(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction().addToBackStack(null)
                .replace(R.id.fragment_container_login, fragment)
                .commit();

    }

    private void replaceFragmentSetting(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction().addToBackStack(null)
                .replace(R.id.fragment_container_settings, fragment)
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
                intLayout = 2;
                setContentView(R.layout.activity_settings);
                addFragmentSetting();





                return true;
            case R.id.bt_logout:
                //init sharedpreferences
                closeApp();


        }

        return super.onOptionsItemSelected(item);
    }

    public void closeApp(){
        SharedPreferences.Editor editor = sp.edit();
        sp = getApplicationContext().getSharedPreferences("userProfile",Context.MODE_PRIVATE);
        //editor.putBoolean("userState", false);
        //editor.commit();
        long userId = sp.getLong("userId",0);
        boolean userState = sp.getBoolean("userState",false);
        Toast.makeText(this,"Logout selected",Toast.LENGTH_LONG).show();
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);

        user.setLoginState(false);
        dao.updateUser(user);
        editor.clear();
        editor.commit();
        editor.apply();
        Toast.makeText(getApplicationContext(),"Save preferences " + user.isLoginState(), Toast.LENGTH_LONG).show();
        finish();
        System.exit(0);


    }


    @Override
    public void changeFragmentLogin() {
        replaceFragmentLogin(new SignUpFragment());
    }

    @Override
    public void changeFragmentSetting() {
        replaceFragmentSetting(new ChangeParametersFragment());
    }

    @Override
    public void onBackPressed() {
        /*
        if (intLayout ==2){
            intLayout = 1;
            setContentView(R.layout.activity_main);
            loadFragment(new ProfileFragment());
        }*/


    

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_settings);
        if(fragment!=null){ // if is not = null means there is more fragments in the container
            // remove fragments
        }



        super.onBackPressed();
    }
}