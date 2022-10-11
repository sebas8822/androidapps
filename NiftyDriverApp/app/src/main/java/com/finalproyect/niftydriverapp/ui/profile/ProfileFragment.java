package com.finalproyect.niftydriverapp.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.finalproyect.niftydriverapp.ui.functions.ImageResizer;
import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.ui.functions.StaticContextFactory;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.fragIndicators.GraphView_Profile;
import com.finalproyect.niftydriverapp.ui.fragIndicators.ScoreView_Profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ProfileFragment extends Fragment {

    SharedPreferences sp;//Init sharepreferences for user

    SharedPreferences.Editor editor;

    public long getUserId() {
        return userId;
    }


    public void setUserId(long userId) {
        this.userId = userId;
    }

    private long userId = getUserId();

    private TextView tv_userName, tv_mainScore, tv_totalTrips, tv_totalKilometres, tv_totalHours,tv_titleHours;

    private ImageView im_profile;

    private ImageButton bt_changeImage;

    private Button bt_scoreView, bt_graphView;

    Bitmap imageChoose;

//    AppDatabase db;
//    DAO dao;
//    User user ;




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
                                    getContext().getContentResolver(),
                                    selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //imageChoose = selectedImageBitmap;
                        saveImageDatabase(selectedImageBitmap);

                    }
                }

            });

    // for save preferences like user id and user state means open session to write
    @Override
    public void onAttach(@NonNull Context context) {
        sp = context.getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit(); // init sharedPreferences
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);


        /**OnResume*/setProfileImageFromDatabase(user);
        /**OnResume*/tv_userName.setText(user.getUserName() + " " + user.getLastName());
        /**OnResume*/tv_mainScore.setText(mainScore(userId));
        /**OnResume*/tv_totalTrips.setText(numTrips(userId));
        /**OnResume*/tv_totalKilometres.setText(numKilometres(userId));
        /**OnResume*/float totalTime =totalTripHours(userId);

        if(totalTime> 60){
            tv_titleHours.setText("Hours");

            totalTime = totalTime/60;
        }
        /**OnResume*/tv_totalHours.setText(String.format("%.1f", totalTime));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Init shared preferences
        /**no delete yet*///sp = getActivity().getSharedPreferences("userProfile", Context.MODE_PRIVATE);

        long userId = sp.getLong("userId", 0);

        /**Pass this values to the shared preference reset for trip view*/
        editor.putInt("position", 0);
        editor.commit();

        setUserId(userId);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        //Setting profile fragment with user data from database
        //Profile image
        //populateUserTable();
        im_profile = (ImageView) view.findViewById(R.id.im_profile);


        //User Name
        tv_userName = (TextView) view.findViewById(R.id.tv_userName);


        //Main score
        tv_mainScore = (TextView) view.findViewById(R.id.tv_mainScore);


        // Number of trips
        tv_totalTrips = (TextView) view.findViewById(R.id.tv_totalTrips);


        //Number Kilometers
        tv_totalKilometres = (TextView) view.findViewById(R.id.tv_totalKilometres);


        // Total Hours
        tv_titleHours= (TextView) view.findViewById(R.id.tv_titleHours);


        tv_totalHours = (TextView) view.findViewById(R.id.tv_totalHours);


        //Inflate Fragment in the profile fragment
        // create the object pointing the fragment that intent to open
        //ScoreView scoreView = new ScoreView();
        //Creates another object that will replace the other

        bt_changeImage = (ImageButton) view.findViewById(R.id.bt_changeImage);
        bt_changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();


            }
        });

        bt_scoreView = (Button) view.findViewById(R.id.bt_scoreView);
        bt_scoreView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
        bt_scoreView.setTextColor(getResources().getColor(R.color.white));


        bt_scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_scoreView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
                bt_scoreView.setTextColor(getResources().getColor(R.color.white));
                bt_graphView.setBackgroundColor(getResources().getColor(R.color.background_color));
                bt_graphView.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(getContext(), "Score View", Toast.LENGTH_LONG).show();
                ScoreView_Profile scoreViewProfile = new ScoreView_Profile();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_profile_view, scoreViewProfile);
                fragmentTransaction.commit();
            }
        });

        bt_graphView = (Button) view.findViewById(R.id.bt_graphView);
        bt_graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_graphView.setBackgroundColor(getResources().getColor(R.color.blue_sky_200));
                bt_graphView.setTextColor(getResources().getColor(R.color.white));
                bt_scoreView.setBackgroundColor(getResources().getColor(R.color.background_color));
                bt_scoreView.setTextColor(getResources().getColor(R.color.black));
                Toast.makeText(getContext(), "Graph View View", Toast.LENGTH_LONG).show();
                GraphView_Profile graphView = new GraphView_Profile();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_profile_view, graphView);
                fragmentTransaction.commit();


            }
        });

        return view;
    }


    public static long totalTripHours(long userId) {
        AppDatabase db = AppDatabase.getDbInstance(StaticContextFactory.getAppContext());
        DAO dao = db.driverDao();
        long totalTime = dao.getTotalHoursByUser(userId);


        return totalTime;

    }

    public static String numKilometres(long userId) {
        AppDatabase db = AppDatabase.getDbInstance(StaticContextFactory.getAppContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getTotalKilometresByUser(userId));
    }

    public static String numTrips(long userId) {
        AppDatabase db = AppDatabase.getDbInstance(StaticContextFactory.getAppContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getTotalTripsByUser(userId));
    }

    public static String mainScore(long userId) {
        AppDatabase db = AppDatabase.getDbInstance(StaticContextFactory.getAppContext());
        DAO dao = db.driverDao();
        return String.valueOf(dao.getScoreAverageTripByUser(userId));
    }

    public void setProfileImageFromDatabase(User user) {
        byte[] imageData = user.getPicture();
        im_profile.setImageBitmap(createBitmapFromByteArray(imageData));
    }

    public static Bitmap createBitmapFromByteArray(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    // this function set profile image
    public void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);

    }

    private byte[] defaultImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] image = byteArray.toByteArray();

        return image;
    }

    public void saveImageDatabase(Bitmap selectedImageBitmap) {
        //Resi
        Bitmap reducedSizeImage = ImageResizer.reduceBitmapSize(selectedImageBitmap, 240000);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();//inicilite
        reducedSizeImage.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] image = byteArray.toByteArray();

        Toast.makeText(getContext(), "The Profile image has been updated", Toast.LENGTH_LONG).show();

        AppDatabase db = AppDatabase.getDbInstance(getContext());
        DAO dao = db.driverDao();
        User user = dao.getUserById(userId);
        user.setPicture(image);
        dao.updateUser(user);
        im_profile.setImageBitmap(
                selectedImageBitmap);

    }






}