package com.finalproyect.niftydriverapp.ui.profile;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.finalproyect.niftydriverapp.ImageResizer;
import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.StaticContextFactory;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.fragIndicators.GraphView;
import com.finalproyect.niftydriverapp.ui.fragIndicators.ScoreView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ProfileFragment extends Fragment {

    private int userId = 1;

    private TextView tv_userName, tv_mainScore, tv_totalTrips, tv_totalKilometres, tv_totalHours;

    private ImageView im_profile;

    private ImageButton bt_changeImage;

    private Button bt_scoreView,bt_graphView;

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
                        Bitmap selectedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_1);
                        //data.putExtra(MediaStore.EXTRA_OUTPUT,)
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    getContext().getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveImageDatabase(selectedImageBitmap);

                    }
                }

            });



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);

        //Setting profile fragment with user data from database
        //Profile image
        //populateUserTable();
        im_profile = (ImageView) view.findViewById(R.id.im_profile);
        setProfileImageFromDatabase(user);
        //User Name

        tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        tv_userName.setText(user.getUserName() + " " + user.getLastName());

        //Main score

        tv_mainScore = (TextView) view.findViewById(R.id.tv_mainScore);
        tv_mainScore.setText(mainScore());

        // Number of trips

        tv_totalTrips = (TextView) view.findViewById(R.id.tv_totalTrips);
        tv_totalTrips.setText(numTrips());

        //Number Kilometers

        tv_totalKilometres = (TextView) view.findViewById(R.id.tv_totalKilometres);
        tv_totalKilometres.setText(numKilometres());

        // Total Hours

        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);
        tv_totalHours.setText(totalTripHours());

        //Inflate Fragment in the profile fragment
        // create the object pointing the fragment that intent to open
        //ScoreView scoreView = new ScoreView();
        //Creates another object that will replace the other

        bt_changeImage = (ImageButton) view.findViewById(R.id.bt_changeImage);
        bt_changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(user);
            }
        });

        bt_scoreView = (Button) view.findViewById(R.id.bt_scoreView);
        bt_scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Score View", Toast.LENGTH_LONG).show();
                ScoreView scoreView = new ScoreView();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_profile_view,scoreView);
                fragmentTransaction.commit();
            }
        });

        bt_graphView = (Button) view.findViewById(R.id.bt_graphView);
        bt_graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Graph View View", Toast.LENGTH_LONG).show();
                GraphView graphView = new GraphView();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_profile_view,graphView);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private String totalTripHours() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getTotalHoursByUser(userId));
    }

    private String numKilometres() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getTotalKilometresByUser(userId));
    }

    private String numTrips() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getTotalTripsByUser(userId));
    }

    public String mainScore(){
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getScoreAverageTripByUser(userId));
    }







    public void setProfileImageFromDatabase(User user){
        byte[] imageData = user.getPicture();
        im_profile.setImageBitmap(createBitmapFromByteArray(imageData));


    }

    public static Bitmap createBitmapFromByteArray(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }






    // this function set profile image
    private void imageChooser(User user)
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);

    }





    private byte[] defaultImage(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.img_1);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] image = byteArray.toByteArray();

        return image;
    }

    public void saveImageDatabase(Bitmap selectedImageBitmap){
        //Resi
        Bitmap reducedSizeImage = ImageResizer.reduceBitmapSize(selectedImageBitmap,240000);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();//inicilite
        reducedSizeImage.compress(Bitmap.CompressFormat.PNG,100,byteArray);
        byte[] image = byteArray.toByteArray();

        Toast.makeText(getContext(), "The Profile image has been updated",Toast.LENGTH_LONG).show();

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);
        user.setPicture(image);
        dao.updateUser(user);
        im_profile.setImageBitmap(
                selectedImageBitmap);

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


}