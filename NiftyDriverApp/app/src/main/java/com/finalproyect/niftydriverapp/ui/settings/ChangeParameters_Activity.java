package com.finalproyect.niftydriverapp.ui.settings;

import static com.finalproyect.niftydriverapp.ui.functions.StaticContextFactory.getAppContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.loginfragments.ForgetPassword;
import com.finalproyect.niftydriverapp.ui.loginfragments.LoginFragment;
import com.finalproyect.niftydriverapp.ui.loginfragments.SignUpFragment;
import com.finalproyect.niftydriverapp.ui.settings.about.User_Guide_Activity;

import java.util.regex.Pattern;

public class ChangeParameters_Activity extends AppCompatActivity {

    Button bt_UpdateValuesChParameters, bt_forgetPasswordChParameters, bt_sendEmailRP, bt_changePassRP;
    EditText et_firstNameChParameters, et_lastNameChParameters, et_emailChParameters, et_passChParameters, et_emailRP, et_temp_passRP, et_PassChPRP, et_confirmPassChPRP;


    //Init Sharepreferences
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    AppDatabase db;
    DAO dao;

    long userId;

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_para);

        sp = getApplicationContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();
        long userId = sp.getLong("userId", 0);
        setUserId(userId);

        db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        dao = db.driverDao();
        //call user to delete
        User user = dao.getUserById(userId);

        et_firstNameChParameters = (EditText) findViewById(R.id.et_firstNameChParameters);
        et_lastNameChParameters = (EditText) findViewById(R.id.et_lastNameChParameters);
        et_emailChParameters = (EditText) findViewById(R.id.et_emailChParameters);
        et_passChParameters = (EditText) findViewById(R.id.et_passChParameters);


        et_firstNameChParameters.setText(user.getUserName());
        et_lastNameChParameters.setText(user.getLastName());
        et_emailChParameters.setText(user.getEmail());




        bt_forgetPasswordChParameters = findViewById(R.id.bt_forgetPasswordChParameters);
        bt_forgetPasswordChParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Forget password", Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));


            }
        });


        bt_UpdateValuesChParameters = findViewById(R.id.bt_UpdateValuesChParameters);
        bt_UpdateValuesChParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Update Values", Toast.LENGTH_LONG).show();

                String firstName, lastName, email, pass;
                firstName = et_firstNameChParameters.getText().toString();
                lastName = et_lastNameChParameters.getText().toString();
                email = et_emailChParameters.getText().toString();
                pass = et_passChParameters.getText().toString();


                if (SignUpFragment.validName(getApplicationContext(), firstName, lastName) == true && validEmailCurrentUser(user, email) == true && confirmPass(user, pass) == true) {
                    // Update values into the data base
                    user.setUserName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);

                    // Update the Database
                    dao.updateUser(user);
                    Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                    //update
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);

                    startActivity(intent);
                    overridePendingTransition(0, 0);


                } else {
                    Toast.makeText(getApplicationContext(), "Please check data ", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }




    public boolean validEmailCurrentUser(User user, String email) {
        // Variables declaration for regex function
        boolean val = true;

        if (!email.equals(user.getEmail())) {
            val = validEmail(email);
        }


        return val;
    }

    public boolean validEmail(String email) {
        // Variables declaration for regex function

        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        DAO dao = db.driverDao();


        String valemail = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

        boolean val = true;

        if (!(Pattern.matches(valemail, email))) {
            Toast.makeText(getApplicationContext(), "email is invalid", Toast.LENGTH_LONG).show();
            val = false;
        }
        //check if exits this email in the data base
        if (!(dao.getUserByEmail(email) == null)) {
            Toast.makeText(getApplicationContext(), "email already exits", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;
    }


    private boolean confirmPass(User user, String pass) {
        boolean val = true;
        Log.d("confirmPass", "passuser" + user.getPassword() + "passnow" + pass);
        if (!user.getPassword().equals(pass)) {
            Toast.makeText(getApplicationContext(), "password is incorrect", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;

    }


}