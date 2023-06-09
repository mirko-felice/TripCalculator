package com.example.tripcalculator.utility;

import com.example.tripcalculator.database.Location;

import java.util.List;

public interface IOptimizeCallback {

   void updateLocations(List<Location> locations);

   void updateLocation(Location location);
}
