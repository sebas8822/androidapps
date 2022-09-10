package com.finalproyect.niftydriverapp.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName="Sensor") // identifier required
public class Sensor {

    @PrimaryKey(autoGenerate = true)
    public long sensorId; // for database purposes


    @ColumnInfo(name = "tripCreatorId")
    public long tripCreatorId;

    @ColumnInfo(name = "X_Acc")
    public float xAcc;
    @ColumnInfo(name = "Y_Acc")
    public float yAcc;
    @ColumnInfo(name = "Z_Acc")
    public float zAcc;
    @ColumnInfo(name = "Pitch")
    public float pitch;
    @ColumnInfo(name = "Yaw")
    public float yaw;
    @ColumnInfo(name = "Car_Speed")
    public float carSpeed;
    @ColumnInfo(name = "Google_Speed")
    public float googleCurSpeed;
    @ColumnInfo(name = "Current_Location_long")
    public float curLocationLong;

    public float getCurLocationLong() {
        return curLocationLong;
    }

    public void setCurLocationLong(float curLocationLong) {
        this.curLocationLong = curLocationLong;
    }

    public float getCurLocationLat() {
        return curLocationLat;
    }

    public void setCurLocationLat(float curLocationLat) {
        this.curLocationLat = curLocationLat;
    }

    @ColumnInfo(name = "Current_Location_Lat")
    public float curLocationLat;
    @ColumnInfo(name = "Val_Speed")
    public boolean valSpeed;
    @ColumnInfo(name = "Safe_acceleration")
    public boolean safeAcc;
    @ColumnInfo(name = "Safe_desacceleration")
    public boolean safeDes;
    @ColumnInfo(name = "Safe_left")
    public boolean safeLeft;
    @ColumnInfo(name = "Safe_Right")
    public boolean safeRight;
    @ColumnInfo(name = "hard_acceleration")
    public boolean hardAcc;
    @ColumnInfo(name = "hard_desacceleration")
    public boolean hardDes;
    @ColumnInfo(name = "Sharp_Left")
    public boolean sharpLeft;
    @ColumnInfo(name = "Sharp_Right")
    public boolean sharpRight;

    public long getSensorId() {
        return sensorId;
    }

    public void setSensorId(long sensorId) {
        this.sensorId = sensorId;
    }

    public long getTripCreatorId() {
        return tripCreatorId;
    }

    public void setTripCreatorId(long tripCreatorId) {
        this.tripCreatorId = tripCreatorId;
    }

    public float getxAcc() {
        return xAcc;
    }

    public void setxAcc(float xAcc) {
        this.xAcc = xAcc;
    }

    public float getyAcc() {
        return yAcc;
    }

    public void setyAcc(float yAcc) {
        this.yAcc = yAcc;
    }

    public float getzAcc() {
        return zAcc;
    }

    public void setzAcc(float zAcc) {
        this.zAcc = zAcc;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getCarSpeed() {
        return carSpeed;
    }

    public void setCarSpeed(float carSpeed) {
        this.carSpeed = carSpeed;
    }

    public float getGoogleCurSpeed() {
        return googleCurSpeed;
    }

    public void setGoogleCurSpeed(float googleCurSpeed) {
        this.googleCurSpeed = googleCurSpeed;
    }



    public boolean isValSpeed() {
        return valSpeed;
    }

    public void setValSpeed(boolean valSpeed) {
        this.valSpeed = valSpeed;
    }

    public boolean isSafeAcc() {
        return safeAcc;
    }

    public void setSafeAcc(boolean safeAcc) {
        this.safeAcc = safeAcc;
    }

    public boolean isSafeDes() {
        return safeDes;
    }

    public void setSafeDes(boolean safeDes) {
        this.safeDes = safeDes;
    }

    public boolean isSafeLeft() {
        return safeLeft;
    }

    public void setSafeLeft(boolean safeLeft) {
        this.safeLeft = safeLeft;
    }

    public boolean isSafeRight() {
        return safeRight;
    }

    public void setSafeRight(boolean safeRight) {
        this.safeRight = safeRight;
    }

    public boolean isHardAcc() {
        return hardAcc;
    }

    public void setHardAcc(boolean hardAcc) {
        this.hardAcc = hardAcc;
    }

    public boolean isHardDes() {
        return hardDes;
    }

    public void setHardDes(boolean hardDes) {
        this.hardDes = hardDes;
    }

    public boolean isSharpLeft() {
        return sharpLeft;
    }

    public void setSharpLeft(boolean sharpLeft) {
        this.sharpLeft = sharpLeft;
    }

    public boolean isSharpRight() {
        return sharpRight;
    }

    public void setSharpRight(boolean sharpRight) {
        this.sharpRight = sharpRight;
    }
}
