package com.finalproyect.niftydriverapp.ui.loginfragments;

import android.app.AlertDialog;
import android.os.Bundle;

import android.os.StrictMode;
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


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;



public class ForgetPassword extends AppCompatActivity {


    AppDatabase db;
    DAO dao;

    private EditText et_emailRP, et_temp_passRP, et_PassChPRP, et_confirmPassChPRP;
    private Button bt_sendEmailRP, bt_changePassRP;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forget_password);
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());
        DAO dao = db.driverDao();

        et_emailRP = (EditText) findViewById(R.id.et_emailRP);
        et_temp_passRP = (EditText) findViewById(R.id.et_temp_passRP);
        et_PassChPRP = (EditText) findViewById(R.id.et_PassChPRP);
        et_confirmPassChPRP = (EditText) findViewById(R.id.et_confirmPassChPRP);
        bt_sendEmailRP = (Button) findViewById(R.id.bt_sendEmailRP);
        bt_changePassRP = (Button) findViewById(R.id.bt_changePassRP);





        bt_sendEmailRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "you are here Send Email", Toast.LENGTH_SHORT).show();
                // Variables declaration
                String email;
                final String username = "paisa8822@gmail.com";
                final String userPassword = "smiewozzxfwlwolc";
                String messageTosend = "hola desde tu app";



                // conversion to string inputs
                email = et_emailRP.getText().toString();


                // Data entry Validation
                //validate if the email is in the data base
                //generate ramdom pass and save into the perfil
                //send email


                Properties prop = new Properties();
                prop.put("mail.smtp.auth","true");
                prop.put("mail.smtp.starttls.enable","true");
                prop.put("mail.smtp.host","smtp.gmail.com");
                prop.put("mail.smtp.port","587");
                Session session= Session.getInstance(prop,
                        new javax.mail.Authenticator(){
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username,userPassword);
                            }
                        });

                try{
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(et_emailRP.getText().toString()));
                    message.setSubject("Your temp password from NiftyDriverApp");
                    message.setText(messageTosend);
                    Transport.send(message);
                    Toast.makeText(getApplicationContext(),"Email send succesfully",Toast.LENGTH_LONG).show();



                }catch (MessagingException e){
                    throw new RuntimeException(e);

                }



                Toast.makeText(getApplicationContext(), "you are the end of  Send Email", Toast.LENGTH_SHORT).show();


            }
        });
        bt_changePassRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "you are here Change Pass", Toast.LENGTH_SHORT).show();
                // Variables declaration
                String email;

                // conversion to string inputs
                email = et_emailRP.getText().toString();


                // Data entry Validation
                //validate if the email is in the data base
                //generate ramdom pass and save into the perfil
                //send email

                Toast.makeText(getApplicationContext(), "you are here Send Email", Toast.LENGTH_SHORT).show();


            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

}