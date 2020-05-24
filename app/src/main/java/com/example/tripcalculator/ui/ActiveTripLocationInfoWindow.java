package com.example.tripcalculator.ui;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Location;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class ActiveTripLocationInfoWindow extends MarkerInfoWindow {

    public ActiveTripLocationInfoWindow(MapView mapView, int myIndex, Location location, Activity activeTripActivity) {
        super(R.layout.active_trip_location_infowindow, mapView);
        TripActivity tripActivity = (TripActivity) activeTripActivity;

        String title = (myIndex + 1) + ". " + location.DisplayName;
        ((TextView)mView.findViewById(R.id.active_trip_location_title)).setText(title);
        String latitude = tripActivity.getString(R.string.latitude_n, location.Latitude);
        String longitude = tripActivity.getString(R.string.longitude_n, location.Longitude);
        ((TextView)mView.findViewById(R.id.active_trip_location_latitude)).setText(latitude);
        ((TextView)mView.findViewById(R.id.active_trip_location_longitude)).setText(longitude);
        if (location.IsPassed)
            setMessage(tripActivity.getString(R.string.location_passed));
        ((Button)mView.findViewById(R.id.active_trip_location_passed_btn)).setOnClickListener(v -> {
            tripActivity.setLocationAsPassed(myIndex);
            setMessage(tripActivity.getString(R.string.location_passed));
            setEnabled(false);
        });
    }

    @Override
    public void onOpen(Object item) { }

    public void passLocation(){
        ((Button)mView.findViewById(R.id.active_trip_location_passed_btn)).performClick();
    }

    private void setMessage(String string){
        ((TextView)mView.findViewById(R.id.active_trip_location_message_view)).setText(string);
    }

    public void setEnabled(boolean value){
        if (value){
            mView.findViewById(R.id.active_trip_location_message_view).setVisibility(View.GONE);
            mView.findViewById(R.id.active_trip_location_passed_btn).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.active_trip_location_passed_btn).setVisibility(View.GONE);
            mView.findViewById(R.id.active_trip_location_message_view).setVisibility(View.VISIBLE);
        }
    }
}
