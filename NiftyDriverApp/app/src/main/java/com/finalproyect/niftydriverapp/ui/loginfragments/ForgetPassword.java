package com.finalproyect.niftydriverapp.ui.loginfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import android.os.StrictMode;
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


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;


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

                // Variables declaration
                String email, tempPass;
                tempPass = SignUpFragment.getTemporaryPassword();

                String messageToSend = "Need to reset your password?\n" + "Use your secret code!\n" +
                        "\n" + tempPass + "\n" + "\n" + "Go to the forgot password and change your password.\n" +
                        "\n\n" + "If you did not forget your password, you can ignore this email.";

                String subject = "Your temp password from NiftyDriverApp";

                // conversion to string inputs
                email = et_emailRP.getText().toString();

                // Data entry Validation
                try {
                    User user = dao.getUserByEmail(email);
                    user.setTempPassword(tempPass);
                    dao.updateUser(user);
                    sendEmail(ForgetPassword.this,email,subject,messageToSend);
                    Log.d("sendEmail", "Response" + user+ "TemPass"+user.getTempPassword());


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Email does not exit in the data base", Toast.LENGTH_SHORT).show();

                }


                //validate if the email is in the data base
                //generate ramdom pass and save into the perfil
                //send email


                //Toast.makeText(getApplicationContext(), "you are the end of  Send Email", Toast.LENGTH_SHORT).show();


            }
        });
        bt_changePassRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "you are here Change Pass", Toast.LENGTH_SHORT).show();
                // Variables declaration
                String email,tempPass,newPass,confPass;



                // conversion to string inputs
                email = et_emailRP.getText().toString();
                tempPass = et_temp_passRP.getText().toString();
                newPass = et_PassChPRP.getText().toString();
                confPass = et_confirmPassChPRP.getText().toString();

                //User to change pass
                User user = dao.getUserByEmail(email);


                //confir password is from user//confir passwor is valid
                if (user.getTempPassword().equals(tempPass)){
                    if(SignUpFragment.validNewPass(ForgetPassword.this,newPass,confPass)){
                        //update pass
                        user.setPassword(newPass);
                        user.setTempPassword(SignUpFragment.getTemporaryPassword());
                        dao.updateUser(user);
                        Toast.makeText(getApplicationContext(),"Password Updated Successfully",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Temporary Password is not correct",Toast.LENGTH_LONG).show();
                }




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

    public static void sendEmail(Context context, String email, String Subject,String messageToSend) {
        final String username = "paisa8822@gmail.com";
        final String userPassword = "smiewozzxfwlwolc";


        //Send email with the TempPassword
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, userPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(Subject);
            message.setText(messageToSend);
            Transport.send(message);
            Toast.makeText(context, "Email send successfully", Toast.LENGTH_LONG).show();


        } catch (MessagingException e) {
            throw new RuntimeException(e);

        }


    }




}