package com.secondapp.profileapp.db;

//abstract class for entities

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Employee.class}, version = 1)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract EmployeeDao employeeDao();
    //create instace for database class
    private static AppDatabase INSTANCE;
    //Function to return the instance
    public static AppDatabase getDBInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "DB_emp")
                    .allowMainThreadQueries()
                    .build();

        }

        return INSTANCE;
    }

}
