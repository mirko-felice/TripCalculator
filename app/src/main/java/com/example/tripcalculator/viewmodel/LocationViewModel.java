package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {

    private LiveData<List<Location>> locations;

    public LocationViewModel(@NonNull Application application, int tripId) {
        super(application);
        locations = AppDatabase.getInstance(application.getApplicationContext()).locationDao().getLocationsFromTrip(tripId);
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }
}
