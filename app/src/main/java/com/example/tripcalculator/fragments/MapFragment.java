package com.example.tripcalculator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.MapFragmentBinding;
import com.example.tripcalculator.ui.ActiveTripLocationInfoWindow;
import com.example.tripcalculator.ui.LocationInfoWindow;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment {

    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private MapFragmentBinding binding;
    //SEARCH
    private List<Marker> markers;
    private ArrayList<GeoPoint> searchResultPoints;
    //ROAD
    private List<Location> path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        markers = new ArrayList<>();
        searchResultPoints = new ArrayList<>();
        Context context = getContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        binding = MapFragmentBinding.inflate(inflater, container, false);

        map = binding.map;

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        map.setTileSource(TileSourceFactory.MAPNIK);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    public void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove(map);
            map.getOverlays().remove(marker);
        }
        markers.clear();
        searchResultPoints.clear();
    }

    public void setSearchLocationMarkers(List<Location> locations) {
        for (Location location : locations) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            searchResultPoints.add(point);
            Marker marker = new Marker(map);
            markers.add(marker);
            marker.setPosition(point);
            marker.setTitle(location.DisplayName);
            MarkerInfoWindow infoWindow = new LocationInfoWindow(map, location, getActivity());
            marker.setInfoWindow(infoWindow);
            //marker.setSubDescription("Latitude: " + location.Latitude + ";\nLongitude" + location.Longitude + ";");
            map.getOverlays().add(marker);
        }
        focusOn(locations.get(0));
    }

    public void setPathLocationMarkers(List<Location> locations) {
        for (Location location : locations) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            searchResultPoints.add(point);
            Marker marker = new Marker(map);
            markers.add(marker);
            marker.setPosition(point);
            marker.setTitle(location.DisplayName);
            MarkerInfoWindow infoWindow = new ActiveTripLocationInfoWindow(map, location, getActivity());
            marker.setInfoWindow(infoWindow);
            //marker.setSubDescription("Latitude: " + location.Latitude + ";\nLongitude" + location.Longitude + ";");
            map.getOverlays().add(marker);
        }
    }

    public void showAllMarkers(Road road) {
        map.zoomToBoundingBox(road.mBoundingBox, true);
    }

    public void focusOn(Location location) {
        IMapController mapController = map.getController();
        GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
        mapController.setZoom(15.0);
        mapController.setCenter(point);
    }

    public void setPath(List<Location> path) {
        this.path = path;
    }

    public void showActualRoad() {
        ArrayList<GeoPoint> passedWaypoints = new ArrayList<>();
        ArrayList<GeoPoint> nextWaypoints = new ArrayList<>();
        for (Location location : path) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            if (location.IsPassed) {
                passedWaypoints.add(point);
            } else {
                nextWaypoints.add(point);
            }
        }
        ShowRoadTask showRoadTask = new ShowRoadTask();
        showRoadTask.execute(passedWaypoints, nextWaypoints);

        try {
            List<Polyline> polylines = showRoadTask.get();
            Polyline passedRoadOverlay = polylines.get(0);
            Polyline roadToDoOverlay = polylines.get(1);
            if (passedRoadOverlay != null) {
                map.getOverlays().add(passedRoadOverlay);
            }
            if (roadToDoOverlay != null) {
                map.getOverlays().add(roadToDoOverlay);
            }
            setPathLocationMarkers(path);
            passedWaypoints.addAll(nextWaypoints);
            Road fullRoad = new Road(passedWaypoints);
            showAllMarkers(fullRoad);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ShowRoadTask extends AsyncTask<ArrayList<GeoPoint>, Void, List<Polyline>> {

        @Override
        protected List<Polyline> doInBackground(ArrayList<GeoPoint>... arrayLists) {
            RoadManager roadManager = new OSRMRoadManager(getContext());
            List<Polyline> polylines = new ArrayList<>();
            polylines.add(null);
            polylines.add(null);
            if (arrayLists[0].size() > 0) {
                Road passedRoad = roadManager.getRoad(arrayLists[0]);
                if (passedRoad.mStatus == Road.STATUS_OK) {
                    Polyline passedRoadOverlay = RoadManager.buildRoadOverlay(passedRoad, Color.GREEN, 4f);
                    polylines.add(0, passedRoadOverlay);
                }
            }
            if (arrayLists[1].size() > 0) {
                Road roadToDo = roadManager.getRoad(arrayLists[1]);

                Polyline roadToDoOverlay = RoadManager.buildRoadOverlay(roadToDo, Color.BLACK, 4f);
                polylines.add(1, roadToDoOverlay);
            }
            return polylines;
        }
    }
}
