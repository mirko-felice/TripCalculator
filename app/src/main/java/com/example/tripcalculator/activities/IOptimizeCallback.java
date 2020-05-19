package com.example.tripcalculator.activities;

import com.example.tripcalculator.database.Location;

import java.util.List;

public interface IOptimizeCallback {

   void updateLocations(List<Location> locations);

   void updateLocation(Location location);
}
