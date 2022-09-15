package com.finalproyect.niftydriverapp.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName="Trip") // identifier required
public class Trip {

    @PrimaryKey(autoGenerate = true)
    public long tripId; // for database purposes
    //Foreing key
    @ColumnInfo(name = "userCreatorId")
    public long userCreatorId;

    @ColumnInfo(name = "Start_Location")
    public String startLocation;
    @ColumnInfo(name = "End_Location")
    public float endLocation;
    @ColumnInfo(name = "Kilometres")
    public float kilometers;
    @ColumnInfo(name = "Time_Trip")
    public double timeTrip;
    @ColumnInfo(name = "Score_Trip")
    public float scoreTrip;

    @ColumnInfo(name = "Start_Date")
    public String startDate; // need be checked

    @ColumnInfo(name = "End_Date")
    public String endDate; // need be checked

    @ColumnInfo(name = "Start_Time")
    public String startTime; // need be checked

    @ColumnInfo(name = "End_Time")
    public String endTime; // need be checked

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getTripId() {
        return tripId;
    }



    public long getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(long userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public float getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(float endLocation) {
        this.endLocation = endLocation;
    }

    public float getKilometers() {
        return kilometers;
    }

    public void setKilometers(float kilometers) {
        this.kilometers = kilometers;
    }

    public double getTimeTrip() {
        return timeTrip;
    }

    public void setTimeTrip(double timeTrip) {
        this.timeTrip = timeTrip;
    }

    public float getScoreTrip() {
        return scoreTrip;
    }

    public void setScoreTrip(float scoreTrip) {
        this.scoreTrip = scoreTrip;
    }



}
