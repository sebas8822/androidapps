package com.secondapp.profileapp.db;

//abstract class for entities

import android.content.Context;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Inside the database going the entities
@Database(entities = {Employee.class}, version = 2, exportSchema = true) // between {} going the tables
public abstract class AppDatabase  extends RoomDatabase {
    // create the daos
    public abstract EmployeeDao employeeDao();
    //create instace for database class
    private static AppDatabase INSTANCE;// variable static
    //Method static obtain the intance of the same class to return the instance
    // Require contex(system identifier where the app is executing) to create the DB (services)
    public static AppDatabase getDbInstance(Context context) {
        // create the Db is not exists
        if (INSTANCE == null){

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "DB_emp")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }

        return INSTANCE; //
    }

}
