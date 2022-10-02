package com.finalproyect.niftydriverapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class,Trip.class, FusionSensor.class}, version = 3, exportSchema = true) // between {} going the tables

public abstract class AppDatabase  extends RoomDatabase {
    // create the daos
    public abstract DAO driverDao();
    //create instace for database class
    private static AppDatabase INSTANCE;// variable static
    //Method static obtain the intance of the same class to return the instance
    // Require contex(system identifier where the app is executing) to create the DB (services)
    public static AppDatabase getDbInstance(Context context) {
        // create the Db is not exists
        if (INSTANCE == null){

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "DB_DriverBehaviorAPP")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();

        }

        return INSTANCE; //
    }

}