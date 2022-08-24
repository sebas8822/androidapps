package com.secondapp.profileapp.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
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




}

