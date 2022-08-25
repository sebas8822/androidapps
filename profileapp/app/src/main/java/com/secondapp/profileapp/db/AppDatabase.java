package com.secondapp.profileapp.db;

//abstract class for entities

import android.content.Context;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Employee.class}, version = 1)
public abstract class AppDatabase  extends RoomDatabase {

    public abstract EmployeeDao employeeDao();
    //create instace for database class
    private static AppDatabase INSTANCE;
    //Function to return the instance
    public static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null){
            Toast.makeText(context.getApplicationContext(), "inside if  Appdatabase", Toast.LENGTH_SHORT).show();
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "DB_emp")
                    .allowMainThreadQueries()
                    .build();
            Toast.makeText(context.getApplicationContext(), "after  instance +", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context.getApplicationContext(), "before return", Toast.LENGTH_SHORT).show();
        return INSTANCE;
    }

}
