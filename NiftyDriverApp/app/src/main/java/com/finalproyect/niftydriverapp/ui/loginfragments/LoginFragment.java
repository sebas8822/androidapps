package com.finalproyect.niftydriverapp.ui.loginfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.finalproyect.niftydriverapp.CallBackFragment;
import com.finalproyect.niftydriverapp.MainActivity;
import com.finalproyect.niftydriverapp.R;
import com.finalproyect.niftydriverapp.db.AppDatabase;
import com.finalproyect.niftydriverapp.db.DAO;
import com.finalproyect.niftydriverapp.db.User;

public class LoginFragment extends Fragment {

    //variables
    Button bt_signInButton, bt_forgetPassword, bt_signUpButton;
    CallBackFragment callBackFragment;
    EditText et_userEmail,et_Password;
    String userEmail, Password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    // for save preferences like user id and user state means open session
    @Override
    public void onAttach(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences("userProfile",Context.MODE_PRIVATE );
        editor = sharedPreferences.edit(); // init sharedPreferences
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        et_userEmail = view.findViewById(R.id.et_userEmail);
        et_Password = view.findViewById(R.id.et_Password);


        bt_signInButton = (Button) view.findViewById(R.id.bt_signInButton);
        bt_signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sign In button", Toast.LENGTH_LONG).show();
                loginUser();

                //String uName,uPass;
                //uName = sharedPreferences.getString("userName", null);
            }
        });

        bt_forgetPassword = (Button) view.findViewById(R.id.bt_forgetPassword);
        bt_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Forget Password button", Toast.LENGTH_LONG).show();
            }
        });

        bt_signUpButton = (Button) view.findViewById(R.id.bt_signUpButton);
        bt_signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sign up Button", Toast.LENGTH_LONG).show();
                if (callBackFragment!=null){
                    callBackFragment.changeFragmentLogin();

                }

            }
        });

        return view;
    }


    public void loginUser() {

        AppDatabase db = AppDatabase.getDbInstance(getContext());// Init database
        DAO dao = db.driverDao();
        // function to hide the keyboard immediately to press the button
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bt_signInButton.getApplicationWindowToken(), 0);
        // Variables declaration
        String  email, pass;

        // conversion to string inputs
        email = et_userEmail.getText().toString();
        pass = et_Password.getText().toString();


        // Condition to check if any field is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(getContext(), "All camps are required..", Toast.LENGTH_LONG).show();
        } else {
            // Validations conditions for registration

            //System.out.println("Input values " + " Name: " + firstName + " Last Name:" + lastName + " Employee ID" + empId + " Email:" + email);

            if (validUser(email, pass)) {
                // insert values into the data base
                Toast.makeText(getContext(), "start the application an setup sharedpreference", Toast.LENGTH_LONG).show();

                //save preferences with sharepreferences and set the user state true to allow to acces to the app and keep it online until the user log out
                User user = dao.getUserByEmail(email);
                editor.putLong("userId", user.getIdUser());
                user.setLoginState(true);
                dao.updateUser(user);
                editor.putBoolean("userState", user.isLoginState());
                editor.commit();
                Toast.makeText(getContext(),"Save preferences" + user.isLoginState(), Toast.LENGTH_LONG).show();


                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                /**
                new AlertDialog.Builder(getContext())
                        .setTitle("Data inserted is correct")
                        .setMessage(" Name: " + firstName + "\n Last Name: " + lastName + "\n Email: " + email )
                        .show();*/

            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Data inserted is invalid ")
                        .setMessage("check email or password otherwise Sing up")
                        .show();

            }


        }

    }

    private boolean validUser(String email, String pass) {// init data base
        AppDatabase db = AppDatabase.getDbInstance(getContext());// Init database
        DAO dao = db.driverDao();

        boolean val = true;
        if ((dao.getUserByEmail(email)==null)) {
            Toast.makeText(getContext(), "User does not exits", Toast.LENGTH_LONG).show();
            val = false;
            //check password

        }else{
            if (!(dao.getUserByEmail(email).getPassword().equals(pass))){
                val = false;
                Toast.makeText(getContext(), "Password is not correct", Toast.LENGTH_LONG).show();

            }
        }

        return val;
    }

    public void setCallBackFragment(CallBackFragment callBackFragment){
        this.callBackFragment = callBackFragment;
    }


}
