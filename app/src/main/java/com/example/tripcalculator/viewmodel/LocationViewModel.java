package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.repo.LocationRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {

    private LocationRepository repo;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepository(application);
    }

    public LiveData<List<Location>> getLocationsFromTrip(int tripId) {
        return repo.getLocationsFromTrip(tripId);
    }

    public LiveData<Location> getLocationFromId(long locationId) {
        return repo.getLocationFromId(locationId);
    }

    public LiveData<List<Location>> getPossiblePreviousLocations(int tripId, long locationId) {
        return repo.getPossiblePreviousLocations(tripId, locationId);
    }

    public void insertLocation(Location location){
        repo.insertLocation(location);
    }

    public void updateLocation(Location location){
        repo.updateLocation(location);
    }

    public void deleteLocation(Location location){
        repo.deleteLocation(location);
    }
}
