package com.finalproyect.niftydriverapp.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.loginfragments.SignUpFragment;

public class ChangePassword_Activity extends AppCompatActivity {

    Button bt_changePass;
    EditText et_confirmPassChP, et_PassChP, et_currentPassChP;
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
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getApplicationContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sp.edit();
        long userId = sp.getLong("userId", 0);
        setUserId(userId);

        db = AppDatabase.getDbInstance(getApplicationContext());// Init database
        dao = db.driverDao();
        //call user to delete
        User user = dao.getUserById(userId);

        et_currentPassChP = (EditText) findViewById(R.id.et_currentPassChP);
        et_PassChP = (EditText) findViewById(R.id.et_PassChP);
        et_confirmPassChP = (EditText) findViewById(R.id.et_confirmPassChP);



        bt_changePass = findViewById(R.id.bt_changePass);
        bt_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String lastPass, newPass, confPass;

                lastPass = et_currentPassChP.getText().toString();
                newPass = et_PassChP.getText().toString();
                confPass = et_confirmPassChP.getText().toString();


                //confir password is from user//confir passwor is valid
                if (user.getPassword().equals(lastPass)){
                    if(SignUpFragment.validNewPass(getApplicationContext(),newPass,confPass)){
                        //update pass
                        user.setPassword(newPass);
                        dao.updateUser(user);
                        Toast.makeText(getApplicationContext(),"Password Updated",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Current password is not correct",Toast.LENGTH_LONG).show();
                }




            }
        });





    }




}