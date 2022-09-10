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
    public double startLocation;
    @ColumnInfo(name = "End_Location")
    public float endLocation;
    @ColumnInfo(name = "Kilometres")
    public float kilometers;
    @ColumnInfo(name = "Time_Trip")
    public double timeTrip;
    @ColumnInfo(name = "Score_Trip")
    public float scoreTrip;
    @ColumnInfo(name = "Date_and_Time")
    public String dateTime; // need be checked

    public long getTripId() {
        return tripId;
    }

    /**public void setTripId(long tripId) {
        this.tripId = tripId;
    }*/

    public long getUserCreatorId() {
        return userCreatorId;
    }

    public void setUserCreatorId(long userCreatorId) {
        this.userCreatorId = userCreatorId;
    }

    public double getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(double startLocation) {
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
