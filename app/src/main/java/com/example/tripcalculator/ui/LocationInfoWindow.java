package com.example.tripcalculator.ui;

import android.app.Activity;
import android.widget.TextView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.SearchActivity;
import com.example.tripcalculator.database.Location;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class LocationInfoWindow extends MarkerInfoWindow {

    public LocationInfoWindow(MapView mapView, Location location, Activity activity) {
        super(R.layout.my_infowindow, mapView);
        SearchActivity searchActivity = (SearchActivity) activity;

        ((TextView)mView.findViewById(R.id.title)).setText(location.DisplayName);
        String latitudeString = searchActivity.getString(R.string.latitude_n, location.Latitude);
        String longitudeString = searchActivity.getString(R.string.longitude_n, location.Longitude);
        ((TextView)mView.findViewById(R.id.latitude)).setText(latitudeString);
        ((TextView)mView.findViewById(R.id.longitude)).setText(longitudeString);
        mView.findViewById(R.id.addButton).setOnClickListener(v -> searchActivity.addLocationToTrip(location));
    }

    @Override
    public void onOpen(Object item) { }
}
