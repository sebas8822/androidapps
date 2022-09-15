package com.testlyfecycle.lifecycledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ActivityLyfeCycle", "onCreate");
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Activity_number2.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ActivityLyfeCycle", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLyfeCycle", "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLyfeCycle", "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLyfeCycle", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLyfeCycle", "onDestroy");
    }
}
