package com.finalproyect.niftydriverapp.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;

public class ChangePassword_Activity extends AppCompatActivity {

    Button bt_changePass;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        bt_changePass = findViewById(R.id.bt_changePass);
        bt_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Change Password button", Toast.LENGTH_LONG).show();

            }
        });





    }




}