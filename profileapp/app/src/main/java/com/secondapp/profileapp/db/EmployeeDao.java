package com.secondapp.profileapp.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmployeeDao {

    @Query("SELECT * FROM employee") // select data from employee table
    List<Employee> getAllEmployee();
    //Functions
    @Insert
    void insertEmployee(Employee...employees);

    @Delete
    void delete(Employee employee);



}
