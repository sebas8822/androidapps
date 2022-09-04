package com.secondapp.profileapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.secondapp.profileapp.db.AppDatabase;
import com.secondapp.profileapp.db.Employee;
import com.secondapp.profileapp.db.EmployeeDao;

import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText w_firstName;
    private EditText w_lastName;
    private EditText w_empId;
    private EditText w_email;
    private EditText w_empAddress;
    private Button w_submit;
    private Button w_viewDB;
    private Button w_resetDB;
    GoogleMap mMap;







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
        w_resetDB = (Button) findViewById(R.id.bt_buttonRDB);


        w_submit.setOnClickListener(this);
        w_viewDB.setOnClickListener(this);
        w_resetDB.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_button:
                addEmployee();
                //testPopulateDB();
                break;
            case R.id.bt_buttonVDB:
                startActivity(new Intent(this, View_DataBase.class));
                break;
            case R.id.bt_buttonRDB:
                resetDatabase();


                Toast.makeText(this, "Reset button", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    public void resetDatabase(){
//
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Employee> employeeList = db.employeeDao().getAllEmployee();
        EmployeeRepository repo = new EmployeeRepositoryImpl(db.employeeDao());

        for (Employee emp : employeeList){
            repo.deleteEmployee(emp);
        }
        Toast.makeText(this,  employeeList.size()+"Size ", Toast.LENGTH_SHORT).show();


    }
    // test method to populate the DataBase
    private void testPopulateDB() {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());// call database Instance
        EmployeeDao dao = db.employeeDao();// Call Dao Instance to query the db instance
        EmployeeRepository repo = new EmployeeRepositoryImpl(dao);// Call repository instance to communicate with dao
        Employee employee = new Employee(); // inicializate employee (Object)
        employee.setFirstName("Sebastian");
        employee.setLastName("Ramirez");
        employee.setEmpId("134567 ");
        employee.setEmail("sebastian@hotmail.com");
        employee.setEmpAddress("7 Seaview Ave, Port Macquarie");
        repo.insertEmployee(employee);



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

            if (validName(firstName,lastName)==true && validEmpId(empId)==true && validEmail(email)== true && validEmployeeAddress(empAddress)== true) {
                // insert values into the data base
                saveEmployee(firstName, lastName, empId, email, empAddress);
                new AlertDialog.Builder(this)
                        .setTitle("Data inserted is correct")
                        .setMessage(" Name: " + firstName + "\n Last Name: " + lastName + "\n Employee ID: " + empId + "\n Email: " + email + "\n Address: " + empAddress )
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
    public boolean validName(String firsName, String lastName) {


        // Variables declaration for regex function
        String valName = "[a-zA-Z. -]+";
        boolean val = true;
        if ((!(Pattern.matches(valName, firsName))) || (!(Pattern.matches(valName, lastName)))) {
            Toast.makeText(MainActivity.this, "Fist Name is not accepted", Toast.LENGTH_LONG).show();
            val = false;
        }
        return val;
    }
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
    public boolean validEmployeeAddress(String empLocation)  {
        boolean val = true;
        // Call geocode that allow me to ask to google database if the address exits
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());// inicializate Geocoder instance

        try {
            List<Address> listAddress = geocoder.getFromLocationName(empLocation, 1);
            if (!(listAddress.size() > 0)) {
                Toast.makeText(MainActivity.this, "Address no found", Toast.LENGTH_LONG).show();
                val = false;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        return val;
    }

    private void saveEmployee(String firstName, String lastName, String empId, String email, String empAddress){
        // pass the values to database
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());// Init database
        Employee employee = new Employee();// Init new employee item 2 ways to input data

        employee.setFirstName(firstName);
        //employee.firstName = firstName;
        employee.lastName = lastName;
        employee.empId = empId;
        employee.email = email;
        employee.empAddress = empAddress;
        db.employeeDao().insertEmployee(employee);

        //finish();
        Toast.makeText(this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();

    }



}