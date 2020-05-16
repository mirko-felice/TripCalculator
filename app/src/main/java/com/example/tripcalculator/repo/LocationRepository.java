package com.example.tripcalculator.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.LocationDao;

import java.util.List;

public class LocationRepository {

    private LocationDao locationDao;

    public LocationRepository(Application application) {
        locationDao = AppDatabase.getInstance(application).locationDao();
    }

    public LiveData<Location> getLocationFromId(long locationId) {
        return locationDao.getLocationFromId(locationId);
    }

    public LiveData<List<Location>> getLocationsFromTrip(int tripId) {
        return locationDao.getLocationsFromTrip(tripId);
    }

    public LiveData<List<Location>> getPossiblePreviousLocations(int tripId, long locationId){
        return locationDao.getPossiblePreviousLocations(tripId, locationId);
    }

    public void insertLocation(Location location){
        AppDatabase.databaseWriteExecutor.execute(() -> locationDao.insertLocation(location));
    }

    public void updateLocation(Location location){
        AppDatabase.databaseWriteExecutor.execute(() -> locationDao.updateLocation(location));
    }

    public void deleteLocation(Location location){
        AppDatabase.databaseWriteExecutor.execute(() -> locationDao.deleteLocation(location));
    }
}
