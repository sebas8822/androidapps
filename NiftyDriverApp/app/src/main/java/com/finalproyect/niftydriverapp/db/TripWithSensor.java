package com.finalproyect.niftydriverapp.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TripWithSensor {

    @Embedded
    public Trip trip;
    @Relation(
            parentColumn = "tripId",
            entityColumn = "tripCreatorId"
    )
    public List<FusionSensor> fusionSensors;
}
