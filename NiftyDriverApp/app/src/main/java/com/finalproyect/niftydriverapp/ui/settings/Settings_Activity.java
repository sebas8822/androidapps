package com.finalproyect.niftydriverapp.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.CallBackFragment;
import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.settings.about.About_Activity;

import java.util.List;

public class Settings_Activity extends AppCompatActivity {

    //Init Sharepreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    long userId;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    CallBackFragment callBackFragment;
    Button bt_changeParameterSet, bt_changePasswordSettings, bt_aboutAppSettings, bt_logoutSettings, bt_deleteUserSettings;
    Switch sw_themeDarkMode;
    ImageButton bt_changeImageSettings;
    ImageView im_profileSettings;
    TextView tv_currentName, tv_currentEmailSettings;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getApplicationContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();
        long userId = sp.getLong("userId", 0);
        setUserId(userId);


        bt_changeParameterSet = findViewById(R.id.bt_changeParameterSet);
        bt_changeParameterSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Change Parameters button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), ChangeParameters_Activity.class));
            }
        });

        bt_changePasswordSettings = findViewById(R.id.bt_changePasswordSettings);
        bt_changePasswordSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Change Password button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), ChangePassword_Activity.class));
            }
        });

        bt_aboutAppSettings = findViewById(R.id.bt_aboutAppSettings);
        bt_aboutAppSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "About Nifty Driver button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), About_Activity.class));


            }
        });
        bt_logoutSettings = findViewById(R.id.bt_logoutSettings);
        bt_logoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "About Nifty Driver button", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Activity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        closeApp();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        bt_deleteUserSettings = findViewById(R.id.bt_deleteUserSettings);
        bt_deleteUserSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Activity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteUser();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });


    }


    public void closeApp() {






        //Toast.makeText(this, "Logout selected", Toast.LENGTH_LONG).show();
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);

        user.setLoginState(false);
        dao.updateUser(user);
        //just to make sure
        editor.putBoolean("userState", false);
        editor.clear();
        editor.commit();
        editor.apply();
        //Toast.makeText(getApplicationContext(), "Save preferences " + user.isLoginState(), Toast.LENGTH_LONG).show();
        this.finishAffinity();
        this.finish();
        System.exit(0);



    }

    public void deleteUser() {

        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();

        User user = dao.getUserById(userId);

        List<Trip> trips = dao.getAllTripsByUser(userId);

        Toast.makeText(this, user.getUserName()+" Deleted", Toast.LENGTH_LONG).show();



        for (Trip trip : trips) {

            List<FusionSensor> fusionSensors = dao.getAllFusionSensorByTrip(trip.getTripId());
            for (FusionSensor fusionSensor : fusionSensors) {
                dao.deleteFusionSensor(fusionSensor);

            }
            dao.deleteTrip(trip);
        }


        dao.deleteUser(user);
        //just to make sure
        editor.putBoolean("userState", false);
        editor.clear();
        editor.commit();
        editor.apply();

        this.finishAffinity();
        this.finish();
        System.exit(0);




    }


}
