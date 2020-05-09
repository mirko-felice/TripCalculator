package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private AppDatabase database;

    public TripViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<List<Trip>> getTrips() {
        return database.tripDao().getAllTrips();
    }

    public LiveData<Trip> getActiveTrip() {
        return database.tripDao().getActiveTrip();
    }

    public LiveData<Trip> getLastInsertedTrip(){
        return database.tripDao().getLastInsertedTrip();
    }

    public LiveData<Trip> getTripFromId(int tripId) {
        return database.tripDao().getTripFromId(tripId);
    }
}
