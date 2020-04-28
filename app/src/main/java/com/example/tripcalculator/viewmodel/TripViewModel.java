package com.example.tripcalculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    public LiveData<List<Trip>> trips;

    public TripViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        trips = database.tripDao().getAllTrips();
    }
}
