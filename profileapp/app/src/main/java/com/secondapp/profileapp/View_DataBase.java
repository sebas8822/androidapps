package com.secondapp.profileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.secondapp.profileapp.db.AppDatabase;
import com.secondapp.profileapp.db.Employee;

import java.util.List;

public class View_DataBase extends AppCompatActivity {
    private EmpListAdapter empListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_base);

        initRecyclerview();
        loadEmployee();

    }

    private void initRecyclerview() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        empListAdapter = new EmpListAdapter(this);
        recyclerView.setAdapter(empListAdapter);
    }


    private void loadEmployee() {
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Employee> employeeList = db.employeeDao().getAllEmployee();
        empListAdapter.setEmployeeList(employeeList);


    }


}