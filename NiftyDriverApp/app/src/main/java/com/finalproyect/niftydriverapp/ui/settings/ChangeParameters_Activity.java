package com.finalproyect.niftydriverapp.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;

public class ChangeParameters_Activity extends AppCompatActivity {

    Button bt_UpdateValuesChParameters;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_para);

        bt_UpdateValuesChParameters = findViewById(R.id.bt_UpdateValuesChParameters);
        bt_UpdateValuesChParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Update Values", Toast.LENGTH_LONG).show();

            }
        });





    }




}