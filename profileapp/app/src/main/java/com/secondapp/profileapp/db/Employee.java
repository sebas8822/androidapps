package com.secondapp.profileapp.db;
/** For the tables*/

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="employee") // identifier required
public class Employee {

    @PrimaryKey(autoGenerate = true)
    public int eid; // for database purposes

    @ColumnInfo(name = "First_Name")
    public String firstName;
    @ColumnInfo(name = "Last_Name")
    public String lastName;
    @ColumnInfo(name = "Employee_ID")
    public String empId;// for user purposes
    @ColumnInfo(name = "Email")
    public String email;
    @ColumnInfo(name = "Employee_Address")
    public String empAddress;

    //methods to interact with the table setters and getters

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }
}

