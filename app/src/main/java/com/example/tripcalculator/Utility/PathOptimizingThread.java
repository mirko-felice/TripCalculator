package com.example.tripcalculator.Utility;

import androidx.annotation.Nullable;

import com.example.tripcalculator.database.Location;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Executable;
import java.util.ArrayList;

public class PathOptimizingThread implements Runnable {

    private ArrayList<Location> path;
    private Location startPoint;
    private Location endPoint;

    //TODO modify if "startPoint" and "endPoint" is included in "path"
    public PathOptimizingThread(ArrayList<Location> path, Location startPoint, @Nullable Location endPoint){
        this.path = path;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public void run() {
        if (this.path != null){
            ArrayList<Location> primaryLocations = new ArrayList<>();
            ArrayList<Location> nextLocations = new ArrayList<>();
            ArrayList<Location> resultPath = new ArrayList<>();



            for (Location location : path){
                if (location.PreviousId == null){
                    primaryLocations.add(location);
                } else {
                    nextLocations.add(location);
                }
            }


            resultPath.add(startPoint);

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

            if (endPoint != null){
                resultPath.add(endPoint);
            }

            this.path = resultPath;
        }
    }

    public ArrayList<Location> getPath() {
        return path;
    }
}
