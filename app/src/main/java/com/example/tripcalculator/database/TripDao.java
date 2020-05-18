package com.example.tripcalculator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM Trip where IsEnded = 0 ORDER BY StartDate ")
    LiveData<List<Trip>> getAllTrips();

    @Query("SELECT * FROM Trip where TripId = :tripId")
    LiveData<Trip> getTripFromId(int tripId);

    @Query("SELECT * FROM Trip where IsEnded = 1")
    LiveData<List<Trip>> getEndedTrips();

    @Query("SELECT * FROM Trip order by InsertDate DESC limit 1")
    LiveData<Trip> getLastInsertedTrip();

    @Query("SELECT * FROM Trip where IsActive = 1")
    LiveData<Trip> getActiveTrip();

    @Insert
    void insertTrip(Trip trip);

    @Update
    void updateTrip(Trip trip);

    @Delete
    void deleteTrip(Trip trip);
}
