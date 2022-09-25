package com.finalproyect.niftydriverapp.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="User") // identifier required
public class User {
    @PrimaryKey(autoGenerate = true)
    public long userId; // for database purposes

    @ColumnInfo(name = "User_Name")
    public String userName;
    @ColumnInfo(name = "Last_Name")
    public String lastName;
    @ColumnInfo(name = "Email")
    public String email;
    @ColumnInfo(name = "Password")
    public String password;
    @ColumnInfo(name = "Picture")
    public byte[] picture;
    @ColumnInfo(name = "Log_in_state")
    public boolean loginState;
    @ColumnInfo(name = "Theme_state")
    public boolean themeState;

    public boolean isThemeState() {
        return themeState;
    }

    public void setThemeState(boolean themeState) {
        this.themeState = themeState;
    }

    public boolean isLoginState() {
        return loginState;
    }

    public void setLoginState(boolean loginState) {
        this.loginState = loginState;
    }

    public long getIdUser() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
