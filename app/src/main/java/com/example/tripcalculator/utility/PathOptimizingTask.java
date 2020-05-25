package com.example.tripcalculator.utility;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.LoaderFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathOptimizingTask extends AsyncTask<Location, Integer, List<Location>> {

    private final LoaderFragment loaderFragment;
    private final IOptimizeCallback callback;
    private WeakReference<AppCompatActivity> activity;

    public PathOptimizingTask(AppCompatActivity activity, IOptimizeCallback callback) {
        this.activity = new WeakReference<>(activity);
        this.loaderFragment = new LoaderFragment(activity.findViewById(android.R.id.content));
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loaderFragment.show(activity.get().getSupportFragmentManager(), "loader");
    }

    @Override
    protected List<Location> doInBackground(Location... locations) {
        List<Location> path = new ArrayList<>(Arrays.asList(locations));
        List<Location> resultPath = new ArrayList<>();
        if (path.size() > 0){
            List<Location> primaryLocations = new ArrayList<>();
            List<Location> nextLocations = new ArrayList<>();

            resultPath.add(path.get(0));
            path.remove(0);

            for (Location location : path){
                if (location.PreviousId == null){
                    primaryLocations.add(location);
                } else {
                    nextLocations.add(location);
                }
            }

            RoadManager roadManager = new OSRMRoadManager(activity.get().getApplicationContext());
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
                    int j = 0;
                    while((i+2) != locations[j].Order)
                        j++;
                    locations[j].Order = temp;
                    nextLocationToAdd.Order = i + 2;
                    primaryLocations.remove(nextLocationToAdd);
                    resultPath.add(nextLocationToAdd);
                    callback.updateLocation(nextLocationToAdd);
                    callback.updateLocation(locations[j]);
                }
            }
        }
        return resultPath;
    }

    @Override
    protected void onPostExecute(List<Location> locations) {
        loaderFragment.dismiss();
        Snackbar.make(activity.get().findViewById(R.id.snackbar_layout), R.string.optimize_message, BaseTransientBottomBar.LENGTH_LONG).show();
        callback.updateLocations(locations);
    }
}
