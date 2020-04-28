package com.example.tripcalculator.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM Location where Id = :id")
    public LiveData<Location> getLocationFromId(long id);

    @Query("SELECT * FROM Location where tripId = :tripId")
    public LiveData<List<Location>> getLocationsFromTrip(int tripId);

    @Insert
    public void insertLocation(Location location);

    @Update
    public void updateLocation(Location location);

    @Delete
    public void deleteLocation(Location location);
}
