package com.thirdapp.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    private Button buttonOne;
    private EditText firstTxt;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        buttonOne = findViewById(R.id.button_fisrt_act);
        firstTxt = findViewById(R.id.txt_first_act);

        buttonOne.setOnClickListener(v->
        {
            Intent intent = new Intent(this,SecondActivity.class );
            intent.putExtra("MY_MESSAGE", firstTxt.getText().toString());
            startActivity(intent);
        });
    }
}
