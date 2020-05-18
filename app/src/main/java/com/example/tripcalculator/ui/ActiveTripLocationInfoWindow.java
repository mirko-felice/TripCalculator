package com.example.tripcalculator.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Location;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class ActiveTripLocationInfoWindow extends MarkerInfoWindow {

    private TripActivity tripActivity;
    private int myIndex;
    private Location location;

    public ActiveTripLocationInfoWindow(MapView mapView, int myIndex, Location location, Activity activeTripActivity) {
        super(R.layout.active_trip_location_infowindow, mapView);
        this.myIndex = myIndex;
        this.location = location;
        this.tripActivity = (TripActivity) activeTripActivity;
    }

    @Override
    public void onOpen(Object item) {
        ((TextView)mView.findViewById(R.id.active_trip_location_title)).setText(location.DisplayName);
        String latitude = tripActivity.getString(R.string.latitude_n, location.Latitude);
        String longitude = tripActivity.getString(R.string.longitude_n, location.Longitude);
        ((TextView)mView.findViewById(R.id.active_trip_location_latitude)).setText(latitude);
        ((TextView)mView.findViewById(R.id.active_trip_location_longitude)).setText(longitude);
        if (location.IsPassed)
            mView.findViewById(R.id.active_trip_location_passed_btn).setVisibility(View.GONE);
        ((Button)mView.findViewById(R.id.active_trip_location_passed_btn)).setOnClickListener(v -> {
            tripActivity.setLocationAsPassed(myIndex);
            v.setVisibility(View.GONE);
        });
    }

    public void passLocation(){
        ((Button)mView.findViewById(R.id.active_trip_location_passed_btn)).callOnClick();
    }
}
