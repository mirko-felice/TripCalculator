package com.example.tripcalculator.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.MapFragmentBinding;
import com.example.tripcalculator.ui.ActiveTripLocationInfoWindow;
import com.example.tripcalculator.ui.LocationInfoWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment {

    private final static String TAG = "OSM_REQUEST";
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private MapFragmentBinding binding;
    //SEARCH
    RequestQueue requestQueue;
    private List<Marker> markers;
    private ArrayList<GeoPoint> searchResultPoints;
    //ROAD
    private List<Location> path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getContext());
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

        Overlay overlay = new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                double longitude = loc.getLongitude();
                double latitude = loc.getLatitude();
                createRequest(String.valueOf(latitude) + " " + String.valueOf(longitude));
                return true;
            }
        };

        map.getOverlays().add(overlay);
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
            map.getOverlays().add(marker);
        }
    }

    private void showAllMarkers(Road road) {
        map.zoomToBoundingBox(road.mBoundingBox, true);
    }

    public void focusOn(Location location) {
        IMapController mapController = map.getController();
        GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
        mapController.animateTo(point, 15.0, 2000L, 0.0F, true);
    }

    public void setPath(List<Location> path) {
        this.path = path;
    }

    public void showActualRoad() {
        ArrayList<GeoPoint> allWaypoints = new ArrayList<>();
        for (Location location : path) {
            allWaypoints.add(new GeoPoint(location.Latitude, location.Longitude));
        }
        ShowRoadTask showRoadTask = new ShowRoadTask(getContext());
        showRoadTask.execute(path.iterator().next());

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
            Road fullRoad = new Road(allWaypoints);
            showAllMarkers(fullRoad);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ShowRoadTask extends AsyncTask<Location, Void, List<Polyline>> {

        private Context context;

        private ShowRoadTask(Context context){
            this.context = context;
        }

        @Override
        protected List<Polyline> doInBackground(Location... path) {
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
            RoadManager roadManager = new OSRMRoadManager(context);
            List<Polyline> polylines = new ArrayList<>();
            polylines.add(null);
            polylines.add(null);
            if (passedWaypoints.size() > 0) {
                Road passedRoad = roadManager.getRoad(passedWaypoints);
                if (passedRoad.mStatus == Road.STATUS_OK) {
                    Polyline passedRoadOverlay = RoadManager.buildRoadOverlay(passedRoad, Color.GREEN, 4f);
                    polylines.add(0, passedRoadOverlay);
                }
            }
            if (nextWaypoints.size() > 0) {
                Road roadToDo = roadManager.getRoad(nextWaypoints);

                Polyline roadToDoOverlay = RoadManager.buildRoadOverlay(roadToDo, Color.BLACK, 4f);
                polylines.add(1, roadToDoOverlay);
            }
            return polylines;
        }
    }

    private class DistanceChecker extends AsyncTask<GeoPoint, Void, Void>{

        final private double CHECK_DISTANCE = 500.0;

        @Override
        protected Void doInBackground(GeoPoint... geoPoints) {
            try {
                while (CHECK_DISTANCE > geoPoints[0].distanceToAsDouble(geoPoints[1])){
                        this.wait(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void createRequest(String place){
        String url = "https://nominatim.openstreetmap.org/search?q=" + place + "&format=json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    List<Location> locations = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject singleAddress = response.getJSONObject(i);
                        Location location = new Location();
                        location.DisplayName = singleAddress.get("display_name").toString();
                        location.Latitude = Double.parseDouble(singleAddress.get("lat").toString());
                        location.Longitude = Double.parseDouble(singleAddress.get("lon").toString());
                        locations.add(location);
                    }
                    setSearchLocationMarkers(locations);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEARCH", error.toString());
            }
        });

        jsonArrayRequest.setTag(TAG);
        requestQueue.add(jsonArrayRequest);
    }
}
