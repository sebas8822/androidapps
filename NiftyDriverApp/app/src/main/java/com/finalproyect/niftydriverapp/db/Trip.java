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
    public long startDate; // need be checked

    @ColumnInfo(name = "End_Date")
    public long endDate; // need be checked

    @ColumnInfo(name = "Start_Time")
    public long startTime; // need be checked

    @ColumnInfo(name = "End_Time")
    public long endTime; // need be checked

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
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
