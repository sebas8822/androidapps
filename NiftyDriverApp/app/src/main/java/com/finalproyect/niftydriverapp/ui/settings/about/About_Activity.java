package com.finalproyect.niftydriverapp.ui.settings.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.ui.settings.ChangePassword_Activity;

public class About_Activity extends AppCompatActivity {

    Button bt_userGuide, bt_ContactUs, bt_termsAndConditions;
    ImageButton link_github;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_userGuide = findViewById(R.id.bt_userGuide);
        bt_userGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getApplicationContext(),"User Guide Button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), User_Guide_Activity.class));
            }
        });
        bt_ContactUs = findViewById(R.id.bt_ContactUs);
        bt_ContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getApplicationContext(),"Contact Us Button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), ContactUs_Activity.class));
            }
        });

        bt_termsAndConditions = findViewById(R.id.bt_termsAndConditions);
        bt_termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Terms an condition Button", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), TermsConditions_Activity.class));
            }
        });
        link_github = findViewById(R.id.link_github);
        link_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"Github Button", Toast.LENGTH_LONG).show();
                golink("https://github.com/sebas8822");

            }
        });





    }

    private void golink(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }


}