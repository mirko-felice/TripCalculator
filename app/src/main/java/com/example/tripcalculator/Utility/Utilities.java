package com.example.tripcalculator.Utility;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.example.tripcalculator.database.Location;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class Utilities {

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
