package com.finalproyect.niftydriverapp.ui.starttrip;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.Sensor;
import com.finalproyect.niftydriverapp.db.Trip;
import com.finalproyect.niftydriverapp.db.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class StartTripFragment extends Fragment {

    private Button bt_startTrip;

    private ImageView im_profile;

    private Button bt_populateSensorDatabase,bt_ResetTripDatabase,bt_ResetSensorDatabase;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starttrip, container, false);
        im_profile = (ImageView)view.findViewById(R.id.im_profile);





        bt_startTrip = (Button) view.findViewById(R.id.bt_startTrip);
        bt_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Populate Trip", Toast.LENGTH_LONG).show();
                //populateUserTable();
                populateTripTable(1);
                populateTripTable(2);
            }
        });

        bt_populateSensorDatabase = (Button) view.findViewById(R.id.bt_populateSensorDatabase);
        bt_populateSensorDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Populate Sensor", Toast.LENGTH_LONG).show();
                //populateUserTable();
                populateSensorTable(1);
                populateSensorTable(2);
            }
        });

        bt_ResetTripDatabase = (Button) view.findViewById(R.id.bt_ResetTripDatabase);
        bt_ResetTripDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = AppDatabase.getDbInstance(getContext());
                DAO dao = db.driverDao();
                dao.deleteAllTrip();
                Toast.makeText(getContext(), "Delete Trip", Toast.LENGTH_LONG).show();

            }
        });

        bt_ResetSensorDatabase = (Button) view.findViewById(R.id.bt_ResetSensorDatabase);
        bt_ResetSensorDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = AppDatabase.getDbInstance(getContext());
                DAO dao = db.driverDao();
                dao.deleteAllSensor();

                Toast.makeText(getContext(), "Delete Trip", Toast.LENGTH_LONG).show();

            }
        });



        return view;
    }





    private byte[] defaultImage(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.img_1);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] image = byteArray.toByteArray();


        return image;
    }




    public void populateUserTable() {
        String[] num = {"ONE", "DOS", "THREE", "FOUR","FIVE", "SIX","SEVEN", "EIGHT","NINE","TEN"};
        String[] alp = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","E","W","X","Y","Z"};

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserName(num[i]+"Sebastian");
            user.setLastName("Ramirez");
            user.setEmail(alp[i]+num[i]+"8822@hotmail.com");
            user.setPassword("S3b4st1@nR");
            user.setPicture(defaultImage());


            //user.setPicture("@");
            dao.insertUser(user);


        }


    }

    public void populateTripTable(int userId) {
        String[] num = {"ONE", "DOS", "THREE", "FOUR","FIVE", "SIX","SEVEN", "EIGHT","NINE","TEN"};
        String[] alp = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","E","W","X","Y","Z"};

        int Max = 100;
        int Min = 0;

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i <2; i++) {
            Trip trip = new Trip();
            trip.setUserCreatorId(userId);

            trip.setStartLocation(1.1111);
            trip.setEndLocation(1111);
            trip.setKilometers(1+i);
            trip.setTimeTrip(10+i);
            trip.setScoreTrip((int) ((Math.random() * (Max - Min)) + Min));
            trip.setDateTime("10/8/1922 10:00:00");


            //user.setPicture("@");
            dao.insertTrip(trip);


        }


    }

    public void populateSensorTable(int tripId) {
        String[] num = {"ONE", "DOS", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"};
        String[] alp = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "E", "W", "X", "Y", "Z"};
        float accMax = 0.3f;
        float accMin = -0.3f;

        int speedMin = 0;
        int speedMax = 200;
        Random random = new Random();





        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();

        for (int i = 0; i < 10; i++) {
            Sensor sensor = new Sensor();

            sensor.setTripCreatorId(tripId);
            sensor.setxAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setyAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setzAcc((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setPitch((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setYaw((float) ((Math.random() * (accMax - accMin)) + accMin));
            sensor.setCarSpeed((int) ((Math.random() * (speedMax - speedMin)) + speedMin));
            sensor.setGoogleCurSpeed((int) ((Math.random() * (speedMax - speedMin)) + speedMin));
            sensor.setCurLocationLong(41.40338f);
            sensor.setCurLocationLat(2.17403f);
            sensor.setValSpeed(random.nextBoolean());
            sensor.setSafeAcc(random.nextBoolean());
            sensor.setSafeDes(random.nextBoolean());
            sensor.setSafeLeft(random.nextBoolean());
            sensor.setSafeRight(random.nextBoolean());
            sensor.setHardAcc(random.nextBoolean());
            sensor.setHardDes(random.nextBoolean());
            sensor.setSharpLeft(random.nextBoolean());
            sensor.setSharpRight(random.nextBoolean());


            //user.setPicture;
            dao.insertSensor(sensor);


        }
    }


    public void scoreTrip(){


        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        // here i need to cal the variables form sensor data and create the scoring and feedback


    }



}



/**
 Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media  .EXTERNAL_CONTENT_URI);
 startActivityForResult(intent, 3);*/

/**
 @Override
 public void onActivityResult(int requestCode, int resultCode, Intent data) {
 super.onActivityResult(requestCode, resultCode, data);
 if(resultCode == RESULT_OK && data!= null){
 Uri selectedImage = data.getData();
 Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.img_1);

 try {
 bitmap= MediaStore.Images.Media.getBitmap(
 getContext().getContentResolver(),
 selectedImage);
 } catch (IOException e) {
 e.printStackTrace();
 }

 ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
 bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
 byte[] image = byteArray.toByteArray();
 //populateUserDataBase(image);
 im_profile.setImageBitmap(bitmap);

 }



 }*/
