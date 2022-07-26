package com.finalproyect.niftydriverapp.ui.loginfragments;

import static com.finalproyect.niftydriverapp.ui.functions.StaticContextFactory.getAppContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;
import com.finalproyect.niftydriverapp.ui.functions.StaticContextFactory;
import com.finalproyect.niftydriverapp.ui.profile.ProfileFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {


    ImageView im_profileRegister;
    EditText et_firstNameRegister, et_lastNameRegister, et_emailRegister, et_passRegister, et_confirmPassRegister;
    Button bt_singUp;
    ImageButton bt_changeImageRegister;

    String userEmail, Password;

    SharedPreferences.Editor editor;
    Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userProfile", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit(); // init sharedPreferences
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);


        et_firstNameRegister = view.findViewById(R.id.et_firstNameRegister);
        et_lastNameRegister = view.findViewById(R.id.et_lastNameRegister);
        et_emailRegister = view.findViewById(R.id.et_emailRegister);
        et_passRegister = view.findViewById(R.id.et_passRegister);
        et_confirmPassRegister = view.findViewById(R.id.et_confirmPassRegister);
        bt_singUp = (Button) view.findViewById(R.id.bt_singUp);
        bt_singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });


        //editor.putString("userEmail",userEmail);
        //editor.apply(); //like commit


        return view;
    }


    public void addUser() {
        // function to hide the keyboard immediately to press the button
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bt_singUp.getApplicationWindowToken(), 0);
        // Variables declaration
        String firstName, lastName, email, pass, confPass;

        // conversion to string inputs
        firstName = et_firstNameRegister.getText().toString();
        lastName = et_lastNameRegister.getText().toString();
        email = et_emailRegister.getText().toString();
        pass = et_passRegister.getText().toString();
        confPass = et_confirmPassRegister.getText().toString();

        // Condition to check if any field is empty
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confPass)) {
            Toast.makeText(getContext(), "All camps are required..", Toast.LENGTH_LONG).show();
        } else {
            // Validations conditions for registration

            //System.out.println("Input values " + " Name: " + firstName + " Last Name:" + lastName + " Employee ID" + empId + " Email:" + email);

            if (validName(getContext(), firstName, lastName) == true && validEmail(email) == true && validNewPass(getContext(), pass, confPass) == true) {
                // insert values into the data base
                saveUser(firstName, lastName, email, pass);
                Toast.makeText(getContext(), "Data inserted is correct\n"+" Name: " + firstName + "\n Last Name: " + lastName + "\n Email: " + email, Toast.LENGTH_LONG).show();
                /**new AlertDialog.Builder(getContext())
                        .setTitle("Data inserted is correct")
                        .setMessage(" Name: " + firstName + "\n Last Name: " + lastName + "\n Email: " + email)
                        .show();*/

            } else {
                /**new AlertDialog.Builder(getContext())
                        .setTitle("Data inserted is invalid ")
                        .setMessage("Check and insert data again")
                        .show();*/
                Toast.makeText(getContext(), "Data inserted is incorrect\n"+"Check and insert data again", Toast.LENGTH_LONG).show();

            }


        }

    }

    public static boolean validNewPass(Context context, String newPass, String confPass) {
        boolean val = true;
        String PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        //valid with regex if correct check if if equal to confirm pass

        if (!(Pattern.matches(PASSWORD_PATTERN, newPass))) {
            Toast.makeText(context, "Password does not meet the standards", Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Password must have: ");
            builder.setMessage("* At least 8 characters and at most 20 characters.\n" +
                    "* at least one digit.\n" +
                    "* at least one upper case alphabet.\n" +
                    "* at least one lower case alphabet.\n" +
                    "* at least one special character like !@#$%&*()-+=^.\n" +
                    "* It doesn’t contain any white space\n");
            android.app.AlertDialog dialog = builder.create();
            dialog.show();

            val = false;
        }


        if (val == true) {
            val = validPasswords(context, newPass, confPass);

        }
        return val;
    }

    public static boolean validPasswords(Context context, String pass, String confPass) {
        boolean val = true;

        if (!pass.equals(confPass)) {
            Toast.makeText(context, "Passwords are not the same check", Toast.LENGTH_LONG).show();
            val = false;
        }
        return val;
    }

    public static boolean validName(Context context, String firsName, String lastName) {


        // Variables declaration for regex function
        String valName = "[a-zA-Z. -]+";
        boolean val = true;
        if ((!(Pattern.matches(valName, firsName))) || (!(Pattern.matches(valName, lastName)))) {
            Toast.makeText(context, "Name is not accepted", Toast.LENGTH_LONG).show();
            val = false;
        }
        return val;
    }

    public boolean validEmail(String email) {
        // Variables declaration for regex function

        AppDatabase db = AppDatabase.getDbInstance(getContext());// Init database
        DAO dao = db.driverDao();


        String valemail = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

        boolean val = true;

        if (!(Pattern.matches(valemail, email))) {
            Toast.makeText(getContext(), "email is invalid", Toast.LENGTH_LONG).show();
            val = false;
        }
        //check if exits this email in the data base
        if (!(dao.getUserByEmail(email) == null)) {
            Toast.makeText(getContext(), "email already exits", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;
    }


    public static String getTemporaryPassword() {

        int length = 8;
        char[] chars = "!@#$%&*()-+=^ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz0123456789!@#$%&*()-+=^".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        Random rand = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();


    }


    private void saveUser(String firstName, String lastName, String email, String pass) {
        // pass the values to database
        AppDatabase db = AppDatabase.getDbInstance(getContext());// Init database
        DAO dao = db.driverDao();
        User user = new User();// Init new employee item 2 ways to input data


        user.setUserName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(pass);
        user.setTempPassword(getTemporaryPassword());
        user.setPicture(defaultImage());
        user.setLoginState(false);
        user.setThemeState(false);


        //user.setPicture("@");
        dao.insertUser(user);


        //finish();
        Toast.makeText(getContext(), "User Created Successfully", Toast.LENGTH_SHORT).show();

    }


    private byte[] defaultImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] image = byteArray.toByteArray();

        return image;
    }


}
