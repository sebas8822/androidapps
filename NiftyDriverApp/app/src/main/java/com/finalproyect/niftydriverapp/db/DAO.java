package com.finalproyect.niftydriverapp.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DAO {


    @Insert
    void insertUser(User...user);

    @Insert
    void insertTrip(Trip...trip);

    @Insert
    void insertSensor(Sensor...sensor);


    @Delete
    void delete(User user);

    @Delete
    void deleteAllTrips(User user);

    @Update
    void updateUser(User user);


    // Get the list of trips
    @Query("SELECT * FROM Trip where userCreatorId= :id")
    List<Trip> getAllTrips(long id);


    @Query("select * from user where userId= :id")
    public User getUserById(long id);

    @Query("select * from user where Email= :email")
    public User getUserByEmail(String email);

    @Transaction
    @Query("SELECT * FROM User")
    public List<UserWithTrip> getUsersWithTrip();

    @Transaction
    @Query("SELECT * FROM trip")
    public List<TripWithSensor> getTripWithSensor();

    @Query("SELECT COUNT(userId) FROM User")
    int getUserCount();

    @Query("SELECT AVG(Score_Trip) FROM Trip where userCreatorId= :id")
    int getScoreAverageTripByUser(long id);

    @Query("SELECT COUNT(userCreatorId) FROM Trip where userCreatorId= :id")
    int getTotalTripsByUser(long id);

    @Query("SELECT SUM(Kilometres) FROM Trip where userCreatorId= :id")
    int getTotalKilometresByUser(long id);

    @Query("SELECT SUM(Time_Trip) FROM Trip where userCreatorId= :id")
    int getTotalHoursByUser(long id);



    @Query("DELETE FROM User")
    void deleteAllUsers();

    @Query("DELETE FROM Trip")
    void deleteAllTrip();
    @Query("DELETE FROM Sensor")
    void deleteAllSensor();

    @Query("DELETE FROM Trip where userCreatorId= :id")
    void deleteAllTripByUserId(long id);

    @Query("DELETE FROM Sensor where tripCreatorId= :id")
    void deleteAllSensorByTripId(long id);










}
