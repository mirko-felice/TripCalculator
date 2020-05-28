package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.repo.TripRepository;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private TripRepository repo;

    public TripViewModel(@NonNull Application application) {
        super(application);
        repo = new TripRepository(application);
    }

    public LiveData<List<Trip>> getAllTrips() {
        return repo.getAllTrips();
    }

    public LiveData<List<Trip>> getEndedTrips() {
        return repo.getEndedTrips();
    }

    public LiveData<Trip> getActiveTrip() {
        return repo.getActiveTrip();
    }

    public LiveData<Trip> getLastInsertedTrip(){
        return repo.getLastInsertedTrip();
    }

    public LiveData<Trip> getTripFromId(int tripId) {
        return repo.getTripFromId(tripId);
    }

    public LiveData<Trip> getPlannedTrip() {
        return repo.getPlannedTrip();
    }

    public void insertTrip(Trip trip){
        repo.insertTrip(trip);
    }

    public void updateTrip(Trip trip){
        repo.updateTrip(trip);
    }

    public void deleteTrip(Trip trip){
        repo.deleteTrip(trip);
    }


}
