package com.example.tripcalculator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.Utility.PathOptimizingThread;
import com.example.tripcalculator.Utility.Utilities;
import com.example.tripcalculator.activities.SearchActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.MapFragmentBinding;
import com.example.tripcalculator.ui.LocationInfoWindow;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private MapFragmentBinding binding;
    //SEARCH
    private List<Marker> markers;
    private ArrayList<GeoPoint> searchResultPoints;
    //ROAD
    private ArrayList<Location> path;

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

    public void clearMarkers(){
        for (Marker marker : markers){
            marker.remove(map);
            map.getOverlays().remove(marker);
        }
        markers.clear();
        searchResultPoints.clear();
    }

    public void setSearchResults(List<Location> locations){
        IMapController mapController = map.getController();
        for (Location location : locations) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            searchResultPoints.add(point);
            Marker marker = new Marker(map);
            markers.add(marker);
            marker.setPosition(point);
            marker.setTitle(location.DisplayName);
            marker.setInfoWindow(new LocationInfoWindow(R.layout.my_infowindow, map, location, (SearchActivity)getActivity()));
            //marker.setSubDescription("Latitude: " + location.Latitude + ";\nLongitude" + location.Longitude + ";");
            map.getOverlays().add(marker);
        }
        Road road = new Road(searchResultPoints);
        BoundingBox boundingBox = road.mBoundingBox;
        map.zoomToBoundingBox(boundingBox, true);
    }

    public void focusOn(Location location) {
        IMapController mapController = map.getController();
        GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
        mapController.setZoom(15.0);
        mapController.setCenter(point);
    }

    public void setPath(ArrayList<Location> path){
        this.path = path;
    }

    public void optimizePath(Location startLocation, @Nullable Location endLocation){
        PathOptimizingThread optimizingThread = new PathOptimizingThread(this.path, startLocation, endLocation);
        optimizingThread.run();
        this.path = optimizingThread.getPath();
    }

    public void moveLocation(int from, int to){
        if (to >= 0 && from <= path.size() - 1) {
            Location location = path.get(from);
            for (int i = from; i > to; i--) {
                path.add(i, path.get(i - 1));
            }
            path.add(to, location);
        }
    }

    public void showActualRoad(){
        ArrayList<GeoPoint> passedWaypoints = new ArrayList<>();
        ArrayList<GeoPoint> nextWaypoints = new ArrayList<>();
        for(Location location : path){
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            if (location.IsPassed) {
                passedWaypoints.add(point);
            } else {
                nextWaypoints.add(point);
            }
        }
        RoadManager roadManager = new OSRMRoadManager(getContext());
        Road passedRoad = roadManager.getRoad(passedWaypoints);
        Road roadToDo = roadManager.getRoad(nextWaypoints);
        Polyline passedRoadOverlay = RoadManager.buildRoadOverlay(passedRoad, Color.GREEN, 1.5f);
        Polyline roadToDoOverlay = RoadManager.buildRoadOverlay(roadToDo, Color.WHITE, 1.5f);
        map.getOverlays().add(passedRoadOverlay);
        map.getOverlays().add(roadToDoOverlay);
    }
}
