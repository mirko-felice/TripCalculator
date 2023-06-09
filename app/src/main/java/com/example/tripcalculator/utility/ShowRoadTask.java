package com.example.tripcalculator.utility;

import android.graphics.Color;
import android.os.AsyncTask;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.LoaderFragment;
import com.example.tripcalculator.fragments.MapFragment;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowRoadTask extends AsyncTask<Location, Void, List<Polyline>> {

    private RoadManager roadManager;
    private LoaderFragment loaderFragment;
    private WeakReference<MapFragment> mapFragment;

    public ShowRoadTask(RoadManager roadManager, MapFragment mapFragment){
        this.roadManager = roadManager;
        this.loaderFragment = new LoaderFragment(mapFragment.requireActivity().findViewById(android.R.id.content));
        this.mapFragment = new WeakReference<>(mapFragment);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loaderFragment.show(mapFragment.get().requireActivity().getSupportFragmentManager(), "loader");
    }

    @Override
    protected List<Polyline> doInBackground(Location... path) {
        ArrayList<GeoPoint> passedWaypoints = new ArrayList<>();
        ArrayList<GeoPoint> nextWaypoints = new ArrayList<>();
        Location prevLocation = null;
        GeoPoint prevPoint = null;
        for (Location location : path) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            if (location.IsPassed) {
                passedWaypoints.add(point);
            } else {
                if (prevLocation != null && prevLocation.IsPassed) {
                    nextWaypoints.add(prevPoint);
                }
                nextWaypoints.add(point);
            }
            prevPoint = point;
            prevLocation = location;
        }
        List<Polyline> polylines = new ArrayList<>();
        polylines.add(null);
        polylines.add(null);
        if (passedWaypoints.size() > 1) {
            Road passedRoad = roadManager.getRoad(passedWaypoints);
            if (passedRoad.mStatus == Road.STATUS_OK) {
                Polyline passedRoadOverlay = RoadManager.buildRoadOverlay(passedRoad, Color.GREEN, 4f);
                polylines.set(0, passedRoadOverlay);
            }
        }
        if (!nextWaypoints.isEmpty()) {
            Road roadToDo = roadManager.getRoad(nextWaypoints);
            if (roadToDo.mStatus == Road.STATUS_OK) {
                Polyline roadToDoOverlay = RoadManager.buildRoadOverlay(roadToDo, Color.BLACK, 4f);
                polylines.set(1, roadToDoOverlay);
            }
        }
        return polylines;
    }

    @Override
    protected void onPostExecute(List<Polyline> polylines) {
        super.onPostExecute(polylines);
        Polyline passedRoadOverlay = polylines.get(0);
        Polyline roadToDoOverlay = polylines.get(1);
        if(mapFragment.get().isVisible()) {
            MapView map = mapFragment.get().requireActivity().findViewById(R.id.map);
            if (passedRoadOverlay != null) {
                map.getOverlays().add(passedRoadOverlay);
            }
            if (roadToDoOverlay != null) {
                map.getOverlays().add(roadToDoOverlay);
            }
            mapFragment.get().setPathLocationMarkers();
            map.invalidate();
        }
        loaderFragment.dismiss();
    }
}
