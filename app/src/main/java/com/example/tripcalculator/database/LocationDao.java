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

    @Query("SELECT * FROM Location where TripId = :tripId ORDER BY `Order` ASC")
    LiveData<List<Location>> getLocationsFromTrip(int tripId);

    @Query("SELECT * FROM Location where TripId = :tripId and Id != :id and PreviousId is null")
    LiveData<List<Location>> getPossiblePreviousLocations(int tripId, long id);

    @Query("SELECT * FROM Location where Id = :locationId")
    LiveData<Location> getLocationFromId(long locationId);

    @Insert
    void insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

}
