package com.testlyfecycle.lifecycledemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_number2 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number2);
        Log.d("ActivityLyfeCycle", "onCreate2");
    }




    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ActivityLyfeCycle", "onStart2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLyfeCycle", "onResume2");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLyfeCycle", "onPause2");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLyfeCycle", "onStop2");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLyfeCycle", "onDestroy2");
    }
}