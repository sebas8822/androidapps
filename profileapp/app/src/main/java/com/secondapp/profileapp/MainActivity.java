package com.secondapp.profileapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.secondapp.profileapp.db.AppDatabase;
import com.secondapp.profileapp.db.Employee;

import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText w_firstName;
    private EditText w_lastName;
    private EditText w_empId;
    private EditText w_email;
    private EditText w_empAddress;
    private Button w_submit;
    private Button w_viewDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        w_firstName = (EditText) findViewById(R.id.et_firstName);
        w_lastName = (EditText) findViewById(R.id.et_lastName);
        w_empId = (EditText) findViewById(R.id.et_empId);
        w_email = (EditText) findViewById(R.id.et_email);
        w_empAddress = (EditText) findViewById(R.id.et_address);
        w_submit = (Button) findViewById(R.id.bt_button);
        w_viewDB = (Button) findViewById(R.id.bt_buttonVDB);


        w_submit.setOnClickListener(this);
        w_viewDB.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_button:
                //addEmployee();
                addEmployee2();
                break;
            case R.id.bt_buttonVDB:
                startActivity(new Intent(this, View_DataBase.class));
                break;
        }


    }

    public void addEmployee2(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(w_submit.getApplicationWindowToken(), 0);
        // Variables declaration
        String firstName, lastName, email, empId, empAddress;

        // conversion to string inputs
        firstName = w_firstName.getText().toString();
        lastName = w_lastName.getText().toString();
        empId = w_empId.getText().toString();
        email = w_email.getText().toString();
        empAddress = w_empAddress.getText().toString();
        saveEmployee(firstName, lastName, empId, email, empAddress);
    }





    public void addEmployee(){
        // function to hide the keyboard immediately to press the button
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(w_submit.getApplicationWindowToken(), 0);
        // Variables declaration
        String firstName, lastName, email, empId, empAddress;

        // conversion to string inputs
        firstName = w_firstName.getText().toString();
        lastName = w_lastName.getText().toString();
        empId = w_empId.getText().toString();
        email = w_email.getText().toString();
        empAddress = w_empAddress.getText().toString();

        // Condition to check if any field is empty
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)||TextUtils.isEmpty(empId) ||TextUtils.isEmpty(email) ||TextUtils.isEmpty(empAddress)){
            Toast.makeText(MainActivity.this,"All camps are required..",Toast.LENGTH_LONG).show();
        }
        else {
            // Validations conditions for registration

            System.out.println("Input values " + " Name: " + firstName + " Last Name:" + lastName + " Employee ID" + empId + " Email:" + email);

            if (validFirstName(firstName)==true && validLastName(lastName)==true && validEmpId(empId)==true && validEmail(email)== true && validEmployeeAddress(empAddress)== true) {
                // insert values into the data base
                saveEmployee(firstName, lastName, empId, email, empAddress);
                new AlertDialog.Builder(this)
                        .setTitle("Data inserted is correct")
                        .setMessage(" Name: " + firstName + "\n Last Name: " + lastName + "\n Employee ID: " + empId + "\n Email: " + email)
                        .show();

            }else{
                new AlertDialog.Builder(this)
                        .setTitle("Data inserted is invalid ")
                        .setMessage("check and insert data again")
                        .show();

            }



        }

    }



    // Validations conditions for every input
    public boolean validFirstName(String name) {


        // Variables declaration for regex function
        String valName = "[a-zA-Z. -]+";
        boolean val = true;
        if (!(Pattern.matches(valName, name))) {
            Toast.makeText(MainActivity.this, "Fist Name is not accepted", Toast.LENGTH_LONG).show();
            val = false;
        }
        return val;
    }
    public boolean validLastName(String last) {

        // Variables declaration for regex function
        String valName = "[a-zA-Z. -]+";


        boolean val = true;
        if (!(Pattern.matches(valName, last))) {
            Toast.makeText(MainActivity.this, "Last Name is not accepted", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;
    }

    public boolean validEmpId(String empId) {

        // Variables declaration for regex function
        boolean val = true;
        String valempID = "[1-9]{7}";


        if (!(Pattern.matches(valempID,empId))) {
            Toast.makeText(MainActivity.this, "code is invalid", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;
    }
    public boolean validEmail(String email) {
        // Variables declaration for regex function

        String valemail = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

        boolean val = true;

        if (!(Pattern.matches(valemail,email))) {
            Toast.makeText(MainActivity.this, "email is invalid", Toast.LENGTH_LONG).show();
            val = false;
        }
        return val;
    }
    public boolean validEmployeeAddress(String empA) {

        // Variables declaration for regex function
        String valName = "[a-zA-Z. -]+";


        boolean val = true;
        if (!(Pattern.matches(valName, empA))) {
            Toast.makeText(MainActivity.this, "Address is not accepted", Toast.LENGTH_LONG).show();
            val = false;
        }

        return val;
    }

    private void saveEmployee(String firstName, String lastName, String empId, String email, String empAddress){
        // pass the values to database
          AppDatabase db = AppDatabase.getDBInstance(this.getApplicationContext());
//        Employee employee = new Employee();
//        employee.firstName = firstName;
//        employee.lastName = lastName;
//        employee.empId = empId;
//        employee.email = email;
//        employee.empAddress = empAddress;
//        db.employeeDao().insertEmployee(employee);
//
//        finish();
        Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();

    }



}