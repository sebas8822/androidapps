package com.finalproyect.niftydriverapp.ui.settings;

import static com.finalproyect.niftydriverapp.ui.profile.ProfileFragment.createBitmapFromByteArray;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.finalproyect.niftydriverapp.CallBackFragment;
import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.FusionSensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.functions.ImageResizer;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;
import com.finalproyect.niftydriverapp.ui.settings.about.About_Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Settings_Activity extends AppCompatActivity {

    //Init Sharepreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    AppDatabase db;
    DAO dao;
    User user;

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


    //Obtain image setup in profile view and save into database init activity to obtain a result
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(

            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {

                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    // init data obtain image
                    Intent data = result.getData();
                    //save data into uri variable transforme into bitmap

                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();

                        Bitmap selectedImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);
                        //data.putExtra(MediaStore.EXTRA_OUTPUT,)
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    getApplicationContext().getContentResolver(),
                                    selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //imageChoose = selectedImageBitmap;
                        saveImageDatabase(selectedImageBitmap);

                    }
                }

            });



    @Override
    protected void onResume() {
        super.onResume();
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);
        /**onResume*/tv_currentName.setText(user.getUserName() + " " + user.getLastName());
        /**onResume*/tv_currentEmailSettings.setText(user.getEmail());


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getApplicationContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();
        long userId = sp.getLong("userId", 0);
        boolean switchThemeState = sp.getBoolean("switchThemeState", false);
        setUserId(userId);

        db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        dao = db.driverDao();
        //call user to delete
        user = dao.getUserById(userId);


        //Current name and Email

        tv_currentName = (TextView) findViewById(R.id.tv_currentName);
        tv_currentName.setText(user.getUserName() + " " + user.getLastName());

        tv_currentEmailSettings = (TextView) findViewById(R.id.tv_currentEmailSettings);
        tv_currentEmailSettings.setText(user.getEmail());

        //Image Profile
        im_profileSettings = (ImageView) findViewById(R.id.im_profileSettings);
        setProfileImageFromDatabase(user);

        //Theme
        sw_themeDarkMode = (Switch) findViewById(R.id.sw_themeDarkMode);
        sw_themeDarkMode.setChecked(user.isThemeState());

        sw_themeDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    user.setThemeState(true);
                    dao.updateUser(user);
                    editor.putBoolean("switchThemeState", true);
                    editor.putBoolean("themeState", user.isThemeState());
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    user.setThemeState(false);
                    dao.updateUser(user);
                    editor.putBoolean("switchThemeState", false);
                    editor.putBoolean("themeState", user.isThemeState());
                    editor.commit();
                }

            }
        });

        bt_changeImageSettings = (ImageButton) findViewById(R.id.bt_changeImageSettings);
        bt_changeImageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "press button to change image", Toast.LENGTH_LONG).show();
                imageChooser();

            }
        });

        //change user settings
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

                        closeApp(user);
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

                        deleteUser(user);
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


    public void setProfileImageFromDatabase(User user) {
        byte[] imageData = user.getPicture();
        im_profileSettings.setImageBitmap(createBitmapFromByteArray(imageData));
    }


    public void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);

    }

    public void saveImageDatabase(Bitmap selectedImageBitmap) {
        //Resi
        Bitmap reducedSizeImage = ImageResizer.reduceBitmapSize(selectedImageBitmap, 240000);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();//inicilite
        reducedSizeImage.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] image = byteArray.toByteArray();

        Toast.makeText(getApplicationContext(), "The Profile image has been updated", Toast.LENGTH_LONG).show();

        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);
        user.setPicture(image);
        dao.updateUser(user);
        im_profileSettings.setImageBitmap(
                selectedImageBitmap);

    }


    public void closeApp(User user) {


        //Toast.makeText(this, "Logout selected", Toast.LENGTH_LONG).show();


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

    public void deleteUser(User user) {


        //call the list of thips made by the user
        List<Trip> trips = dao.getAllTripsByUser(userId);
        //notify before to deleted
        Toast.makeText(this, user.getUserName() + " Deleted", Toast.LENGTH_LONG).show();


        //delete first fusionSensor entries, then trips and finally the user wipe all data
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
