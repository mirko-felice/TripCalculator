package com.example.tripcalculator.Utility;

import android.content.Context;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;

import java.util.concurrent.Executors;

public class DatabaseQueryHelper {

    public static void insert(Trip trip, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).tripDao().insertTrip(trip));
    }

    public static void insert(Location location, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).locationDao().insertLocation(location));
    }

    public static void update(Trip trip, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).tripDao().updateTrip(trip));
    }

    public static void update(Location location, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).locationDao().updateLocation(location));
    }

    public static void delete(Trip trip, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).tripDao().deleteTrip(trip));
    }

    public static void delete(Location location, Context context){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).locationDao().deleteLocation(location));
    }
}
