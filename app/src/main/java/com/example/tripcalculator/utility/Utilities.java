package com.example.tripcalculator.utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Utilities {

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void swapLocations(Context context, Location firstLocation, Location secondLocation){
        int tempPosition = firstLocation.Order;
        firstLocation.Order = secondLocation.Order;
        secondLocation.Order = tempPosition;
        LocationViewModel locationViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LocationViewModel.class);
        locationViewModel.updateLocation(firstLocation);
        locationViewModel.updateLocation(secondLocation);
    }
}
