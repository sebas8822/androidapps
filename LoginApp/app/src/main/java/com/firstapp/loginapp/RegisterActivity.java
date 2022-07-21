package com.firstapp.loginapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText etfullname;
    private EditText etemail;
    private EditText etpassword;
    private EditText retrtpassword;
    private Button BuRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etfullname = findViewById(R.id.et_fullname);
        etemail = findViewById(R.id.et_email);
        etpassword = findViewById(R.id.et_password);
        retrtpassword = findViewById(R.id.et_rt_password);
        BuRegister = findViewById(R.id.bu_register);

        BuRegister.setOnClickListener(view -> {
            validateRegister();

        });


    }

    private void validateRegister() {

        String fullName, email, password, rtPassword;

        fullName = etfullname.getText().toString();
        email = etemail.getText().toString();
        password = etpassword.getText().toString();
        rtPassword = retrtpassword.getText().toString();

        System.out.println("Input values " + "/n" + fullName + "/n" + email + "/n" + password + "/n" + rtPassword);

        if (password.equals(rtPassword)) {
            new AlertDialog.Builder(this)
                    .setTitle("Register " + fullName)
                    .setMessage("Account Register")
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Register " + fullName)
                    .setMessage("Account Fail to Register")
                    .show();
        }


    }


}
