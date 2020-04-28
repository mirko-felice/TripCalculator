package com.example.tripcalculator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM Trip ORDER BY StartDate")
    public LiveData<List<Trip>> getAllTrips();

    @Query("SELECT * FROM Trip where TripId = :tripId")
    public LiveData<Trip> getTripFromId(int tripId);

    @Insert
    public void insertTrip(Trip trip);

    @Update
    public void updateTrip(Trip trip);

    @Delete
    public int deleteTrip(Trip trip);
}
