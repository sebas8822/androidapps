package com.finalproyect.niftydriverapp.ui.settings.about;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.settings.ChangeParameters_Activity;

public class ContactUs_Activity extends AppCompatActivity {

    Button bt_sendEmail;
    EditText et_emailContactus,et_firstNameContactus, et_messageContactus;
    SharedPreferences sp;//Init sharepreferences for user




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();
        //Init shared preferences
        sp = getApplicationContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        //editor.putBoolean("userState", false);
        //editor.commit();
        long userId = sp.getLong("userId",0);
        User user = dao.getUserById(userId);
        et_emailContactus = findViewById(R.id.et_emailContactus);
        et_emailContactus.setText(user.getEmail());
        et_firstNameContactus = findViewById(R.id.et_firstNameContactus);
        et_firstNameContactus.setText(user.getUserName() + " " + user.getLastName());
        et_messageContactus = findViewById(R.id.et_messageContactus);
        bt_sendEmail = findViewById(R.id.bt_sendEmail);
        bt_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Send Email button",Toast.LENGTH_LONG).show();
                //https://www.youtube.com/watch?v=JQRcT_m4tsA
            }
        });






    }



}