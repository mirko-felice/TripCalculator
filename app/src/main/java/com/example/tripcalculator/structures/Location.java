package com.example.tripcalculator.structures;

public class Location {
    private double latitude;
    private double longitude;
    private String displayName;

    public Location(double latitude, double longitude, String displayName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.displayName = displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDisplayName() {
        return displayName;
    }
}
