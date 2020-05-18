package com.example.tripcalculator.utility;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.ui.adapters.LocationRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathOptimizingThread extends AsyncTask<Location, Void, List<Location>> {

    private final LocationRecyclerViewAdapter adapter;
    private AppCompatActivity activity;

    public PathOptimizingThread(AppCompatActivity activity, LocationRecyclerViewAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

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

            RoadManager roadManager = new OSRMRoadManager(activity.getApplicationContext());
            for (int i = 0; i < path.size(); i++) {
                GeoPoint firstPoint = new GeoPoint(resultPath.get(resultPath.size() - 1).Latitude, resultPath.get(resultPath.size() - 1).Longitude);
                Location nextLocationToAdd = null;
                double minDistance = 0;
                for (Location location : primaryLocations) {
                    GeoPoint secondPoint = new GeoPoint(location.Latitude, location.Longitude);
                    ArrayList<GeoPoint> miniPath = new ArrayList<>();
                    miniPath.add(firstPoint);
                    miniPath.add(secondPoint);
                    Road segment = roadManager.getRoad(miniPath);
                    double distance = segment.mLength;
                    if (nextLocationToAdd == null) {
                        minDistance = distance;
                        nextLocationToAdd = location;
                    } else if (distance < minDistance) {
                        nextLocationToAdd = location;
                        minDistance = distance;
                    }
                }
                if (nextLocationToAdd != null) {
                    List<Location> newPossibleLocations = new ArrayList<>();
                    for (Location location : nextLocations) {
                        if (location.PreviousId != null && location.PreviousId == nextLocationToAdd.Id) {
                            primaryLocations.add(location);
                            newPossibleLocations.add(location);
                        }
                    }
                    for (Location location : newPossibleLocations){
                        nextLocations.remove(location);
                    }
                    int temp = nextLocationToAdd.Order;
                    int j;
                    for (j = 0; (i+2) != locations[j].Order; j++);
                    locations[j].Order = temp;
                    nextLocationToAdd.Order = i + 2;
                    primaryLocations.remove(nextLocationToAdd);
                    resultPath.add(nextLocationToAdd);
                    LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
                    locationViewModel.updateLocation(nextLocationToAdd);
                    locationViewModel.updateLocation(locations[j]);
                }
            }
        }
        return path;
    }

    @Override
    protected void onPostExecute(List<Location> locations) {
        adapter.notifyDataSetChanged();
    }
}
