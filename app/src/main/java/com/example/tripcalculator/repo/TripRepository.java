package com.example.tripcalculator.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.database.TripDao;

import java.util.List;

public class TripRepository {

    private TripDao tripDao;

    public TripRepository(Application application) {
        tripDao = AppDatabase.getInstance(application).tripDao();
    }

    public LiveData<List<Trip>> getAllTrips() {
        return tripDao.getAllTrips();
    }

    public LiveData<List<Trip>> getEndedTrips() {
        return tripDao.getEndedTrips();
    }

    public LiveData<Trip> getActiveTrip(){
        return tripDao.getActiveTrip();
    }

    public LiveData<Trip> getLastInsertedTrip(){
        return tripDao.getLastInsertedTrip();
    }

    public LiveData<Trip> getTripFromId(int tripId){
        return tripDao.getTripFromId(tripId);
    }

    public void insertTrip(Trip trip){
        AppDatabase.databaseWriteExecutor.execute(() -> tripDao.insertTrip(trip));
    }

    public void updateTrip(Trip trip){
        AppDatabase.databaseWriteExecutor.execute(() -> tripDao.updateTrip(trip));
    }

    public void deleteTrip(Trip trip){
        AppDatabase.databaseWriteExecutor.execute(() -> tripDao.deleteTrip(trip));
    }
}
