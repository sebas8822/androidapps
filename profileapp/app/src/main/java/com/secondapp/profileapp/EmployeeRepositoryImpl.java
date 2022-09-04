package com.secondapp.profileapp;

import android.app.Application;
import android.os.AsyncTask;

import com.secondapp.profileapp.db.AppDatabase;
import com.secondapp.profileapp.db.Employee;
import com.secondapp.profileapp.db.EmployeeDao;

import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {




    EmployeeDao dao;
    AppDatabase db;


    public EmployeeRepositoryImpl(EmployeeDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return dao.getAllEmployee();
    }

    @Override
    public Employee findEmpById(int empId) {
        return dao.findById(empId);
    }

    @Override
    public void insertEmployee(final Employee employee)
    {
        /**new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                db.employeeDao().insertEmployee(employee);
                return null;
            }
        }.execute();*/

        dao.insertEmployee(employee);
    }

    @Override
    public void updateEmployee(Employee employee) {
        dao.updateEmp(employee);
    }

    @Override
    public void deleteEmployee(Employee employee) {
        dao.delete(employee);
    }

    /**@Override
    public void deleteAll(Employee employee) {
        dao.deleteAll();
    }*/




}
