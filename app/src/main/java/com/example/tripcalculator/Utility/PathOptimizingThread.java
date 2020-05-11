package com.example.tripcalculator.Utility;

import android.os.AsyncTask;

import com.example.tripcalculator.database.Location;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathOptimizingThread extends AsyncTask<Location, Void, List<Location>> {

    @Override
    protected List<Location> doInBackground(Location... locations) {
        List<Location> path = new ArrayList<>(Arrays.asList(locations));
        if (path.size() > 0){
            List<Location> primaryLocations = new ArrayList<>();
            List<Location> nextLocations = new ArrayList<>();
            List<Location> resultPath = new ArrayList<>();
            resultPath.add(path.get(0));
            path.remove(0);

            for (Location location : path){
                if (location.PreviousId == null){
                    primaryLocations.add(location);
                } else {
                    nextLocations.add(location);
                }
            }

            for (int i = 0; i < path.size(); i++){
                GeoPoint firstPoint = new GeoPoint(resultPath.get(resultPath.size() - 1).Latitude, resultPath.get(resultPath.size() - 1).Longitude);
                Location nextLocationToAdd = null;
                double minDistance = -1;
                for (Location location : primaryLocations){
                    GeoPoint secondPoint = new GeoPoint(location.Latitude, location.Longitude);
                    ArrayList<GeoPoint> miniPath = new ArrayList<>();
                    miniPath.add(firstPoint);
                    miniPath.add(secondPoint);
                    Road segment = new Road(miniPath);
                    double distance = segment.mLength;
                    if (distance < minDistance){
                        nextLocationToAdd = location;
                        minDistance = distance;
                    }
                }
                for (Location location : nextLocations){
                    if (location.PreviousId == nextLocationToAdd.Id){
                        nextLocations.add(location);
                    }
                }
                nextLocations.remove(nextLocationToAdd);
                resultPath.add(nextLocationToAdd);
            }
        }
        return path;
    }
}
