package com.secondapp.profileapp.db;
/** Dao are the fuctions tha the tables can do "Hace las consultas"*/

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface EmployeeDao {

    @Query("SELECT * FROM employee") // select data from employee table
    List<Employee> getAllEmployee();
    //Functions
    @Insert
    void insertEmployee(Employee...employees);

    @Delete
    void delete(Employee employee);

    @Update
    void updateEmp(Employee employee);

    @Query("SELECT * FROM employee where eid = :empId")
    Employee findById(int empId);// look up specific employee

    //@Query("DELETE FROM employee")
    //mployee deleteAll();// look up specific employee

    // here can write sql queries to bring the information required to by th applicaiton





}
