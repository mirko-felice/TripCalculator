package com.example.tripcalculator.Utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utilities {

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static List<Location> optimizePath(List<Location> path) {
        PathOptimizingThread optimizingThread = new PathOptimizingThread();
        optimizingThread.execute(path.toArray(new Location[path.size()]));
        try {
            path = optimizingThread.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void swapLocations(Context context, Location firstLocation, Location secondLocation){
        int tempPosition = firstLocation.Order;
        firstLocation.Order = secondLocation.Order;
        secondLocation.Order = tempPosition;
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).locationDao().updateLocation(firstLocation));
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(context).locationDao().updateLocation(secondLocation));
    }
}
