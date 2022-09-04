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

public class View_DataBase extends AppCompatActivity  {
    private EmpListAdapter empListAdapter;
    private Button w_addMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_base);
        //w_addMore = (Button) findViewById(R.id.bt_addMore);
        initRecyclerview();
        loadEmployee();
        /**w_addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(View_DataBase.this, MainActivity.class));
            }
        });*/
    }

    private void initRecyclerview() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        empListAdapter = new EmpListAdapter(this);
        recyclerView.setAdapter(empListAdapter);
    }


    private void loadEmployee(){
        //get the record from database
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Employee> employeeList = db.employeeDao().getAllEmployee();
        empListAdapter.setEmployeeList(employeeList);


    }

    private void showAddressMap(int position) {
        Intent intent = new Intent(this, EmpViewMap.class);
        startActivity(intent);
    }



}