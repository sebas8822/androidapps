package com.secondapp.profileapp;

import com.secondapp.profileapp.db.Employee;

import java.util.List;



public interface EmployeeRepository {


    //
    List<Employee> getAllEmployees();
    Employee findEmpById(int empId);
    void insertEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void deleteEmployee(Employee employee);



}
/** If the data source change for example room to other (webservices), it will be more easy change from here
 * instead change everything for that reason is used this pattern
 */