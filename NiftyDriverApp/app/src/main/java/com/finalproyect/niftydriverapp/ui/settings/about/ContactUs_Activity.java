package com.finalproyect.niftydriverapp.ui.settings.about;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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
import com.finalproyect.niftydriverapp.ui.loginfragments.ForgetPassword;
import com.finalproyect.niftydriverapp.ui.settings.ChangeParameters_Activity;

public class ContactUs_Activity extends AppCompatActivity {

    Button bt_sendEmail;
    EditText et_emailContactus,et_firstNameContactus, et_messageContactus;
    SharedPreferences sp;//Init sharepreferences for user




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





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
                String email, UserName, messageToSend;

                email = et_emailContactus.getText().toString();
                UserName = et_firstNameContactus.getText().toString();
                messageToSend = et_messageContactus.getText().toString();

                Log.d("contactUs","message"+messageToSend);
                String appEmail = "paisa8822@hotmail.com";
                String subject  = "Message from user";
                String finalMessage = "User Name: "+UserName+"\nUser Email: "+email+"\nMessage: \n"+messageToSend;

                Toast.makeText(getApplicationContext(),"Send Email button",Toast.LENGTH_LONG).show();
                ForgetPassword.sendEmail(ContactUs_Activity.this,appEmail,subject,finalMessage);




            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




    }



}