package com.finalproyect.niftydriverapp.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithTrip {

    @Embedded
    public User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "userCreatorId"
    )
    public List<Trip> trips;

}
