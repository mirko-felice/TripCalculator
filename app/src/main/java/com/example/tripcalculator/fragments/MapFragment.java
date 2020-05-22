package com.example.tripcalculator.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.MapFragmentBinding;
import com.example.tripcalculator.ui.ActiveTripLocationInfoWindow;
import com.example.tripcalculator.ui.LocationInfoWindow;
import com.example.tripcalculator.utility.ShowRoadTask;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
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
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapFragment extends MapViewFragment {

    private final static float DISTANCE_DELTA = 200F;
    private final static String PROVIDER = "USER";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private MapFragmentBinding binding;
    //SEARCH
    private List<Marker> markers;
    //ROAD
    private List<Location> path = null;
    private int nextLocationIndex = 0;
    private boolean hasPermissions = false;
    private IMapController mapController;
    //GPS
    private boolean isGPSOn = false;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        markers = new ArrayList<>();
        Context context = requireContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        binding = MapFragmentBinding.inflate(inflater, container, false);

        map = binding.map;

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(9.5);
        binding.checkPositionBtn.setVisibility(View.VISIBLE);
        binding.myPositionBtn.setVisibility(View.GONE);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        checkPermissions();
        checkGPS();
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());
        mLocationOverlay = new MyLocationNewOverlay(provider, map);
        if(isGPSOn){
            map.getOverlays().add(mLocationOverlay);
            mLocationOverlay.enableMyLocation();
            if (mLocationOverlay.getMyLocation() != null)
                startPoint = mLocationOverlay.getMyLocation();
        } else {
            showActivateGPSDialog();
        }
        mapController.setCenter(startPoint);
        map.setTileSource(TileSourceFactory.MAPNIK);

        initMapLayout();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        checkGPS();
        if (isGPSOn)
            mLocationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        if (isGPSOn)
            mLocationOverlay.disableMyLocation();
    }

    public void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove(map);
            map.getOverlays().remove(marker);
        }
        markers.clear();
    }

    public void setSearchLocationMarkers(List<Location> locations) {
        for (Location location : locations) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            Marker marker = new Marker(map);
            markers.add(marker);
            marker.setPosition(point);
            marker.setTitle(location.DisplayName);
            MarkerInfoWindow infoWindow = new LocationInfoWindow(map, location, getActivity());
            marker.setInfoWindow(infoWindow);
            map.getOverlays().add(marker);
        }
        if (locations.size() > 0){
            focusOn(locations.get(0));
        }
    }

    public void updatePassedLocation(int index){
        path.get(index).IsPassed = true;
        showActualRoad();
    }

    private void setPathLocationMarkers(List<Location> locations) {
        int i = 0;
        for (Location location : locations) {
            GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
            Marker marker = new Marker(map);
            markers.add(marker);
            marker.setPosition(point);
            marker.setTitle(location.DisplayName);
            MarkerInfoWindow infoWindow = new ActiveTripLocationInfoWindow(map, i, location, getActivity());
            marker.setInfoWindow(infoWindow);
            map.getOverlays().add(marker);
            i++;
        }
    }

    private void initMapLayout(){

        Overlay overlay = new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                if (path == null) {
                    Projection proj = mapView.getProjection();
                    GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                    double longitude = loc.getLongitude();
                    double latitude = loc.getLatitude();
                    createRequest(latitude + " " + longitude);
                }
                return true;
            }
        };
        map.getOverlays().add(overlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        binding.disableRotationBtn.setOnClickListener(v -> {
            mRotationGestureOverlay.setEnabled(false);
            binding.toNorthBtn.performClick();
            binding.disableRotationBtn.setVisibility(View.GONE);
            binding.enableRotationBtn.setVisibility(View.VISIBLE);
        });
        binding.enableRotationBtn.setOnClickListener(v -> {
            mRotationGestureOverlay.setEnabled(true);
            binding.enableRotationBtn.setVisibility(View.GONE);
            binding.disableRotationBtn.setVisibility(View.VISIBLE);
        });

        binding.zoomOutBtn.setOnClickListener(v -> showAllMarkers());
        binding.toNorthBtn.setOnClickListener(v -> map.getController().animateTo(map.getMapCenter(), map.getZoomLevelDouble(), 2000L, 0.0F));
        binding.clearMarkersBtn.setOnClickListener(v -> clearMarkers());

        binding.checkPositionBtn.setOnClickListener(v -> {
            if (!hasPermissions){
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    checkPermissions();
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireActivity().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (!isGPSOn){
                checkGPS();
                if (!isGPSOn)
                    showActivateGPSDialog();
                else
                    binding.myPositionBtn.performClick();
            }
        });
        binding.myPositionBtn.setOnClickListener(v -> {
            checkGPS();
            if (isGPSOn) {
                if (mLocationOverlay.getMyLocation() != null) {
                    focusOn(mLocationOverlay.getMyLocation());
                    if (path == null)
                        createRequest(mLocationOverlay.getMyLocation().getLatitude() + " " + mLocationOverlay.getMyLocation().getLongitude());
                } else {
                    Snackbar.make(requireActivity().findViewById(R.id.map_fragment), "Impossibile ottenere la posizione", Snackbar.LENGTH_LONG).show();
                }
            } else
                showActivateGPSDialog();
        });
    }

    private void showAllMarkers() {
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (Marker marker : markers){
            points.add(marker.getPosition());
        }
        if(points.size() > 1) {
            Road road = new Road(points);
            map.zoomToBoundingBox(road.mBoundingBox, true);
        } else if (points.size() == 1){
            focusOn(points.get(0));
        }
    }

    public void focusOn(Location location) {
        GeoPoint point = new GeoPoint(location.Latitude, location.Longitude);
        focusOn(point);
    }

    private void focusOn(GeoPoint point){
        mapController.animateTo(point, 15.0, 2000L, map.getMapOrientation());
    }

    public void setPath(List<Location> path) {
        this.path = path;
    }

    public void showActualRoad() {
        RoadManager roadManager = new OSRMRoadManager(requireContext());
        ShowRoadTask showRoadTask = new ShowRoadTask(roadManager, requireActivity());
        showRoadTask.execute(path.toArray(new Location[0]));

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
            showAllMarkers();
            binding.clearMarkersBtn.setVisibility(View.GONE);
            int i = 0;
            while (i < path.size() && path.get(i).IsPassed)
                i++;
            if (i < path.size()){
                nextLocationIndex = i;
                focusOn(path.get(nextLocationIndex));
            }
            if (hasPermissions) {
                startNavigation();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkGPS(){
        if (hasPermissions){
            LocationManager manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            isGPSOn = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSOn){
                binding.checkPositionBtn.setVisibility(View.GONE);
                binding.myPositionBtn.setVisibility(View.VISIBLE);
            } else {
                binding.myPositionBtn.setVisibility(View.GONE);
                binding.checkPositionBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
            } else {
                hasPermissions = true;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
            } else {
                hasPermissions = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                hasPermissions = true;
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Permessi negati!")
                        .setMessage("La tua posizione non può essere visualizzata sulla mappa!\n" +
                                "L'arrivo dovrà essere indicato manualmente!\n" +
                                "Per fornire i permessi cliccare sul simbolo del GPS sulla sua destra!" )
                        .setNeutralButton("OK", (dialog, which) -> {}).show();
            }
        }
    }

    private void startNavigation(){
        mLocationOverlay.getMyLocationProvider().startLocationProvider((location, source) -> {
            mLocationOverlay.onLocationChanged(location, source);
            android.location.Location nextLocation = new android.location.Location(PROVIDER);
            nextLocation.setLatitude(path.get(nextLocationIndex).Latitude);
            nextLocation.setLongitude(path.get(nextLocationIndex).Longitude);
            if (location.distanceTo(nextLocation) < DISTANCE_DELTA){
                ((ActiveTripLocationInfoWindow)markers.get(nextLocationIndex).getInfoWindow()).passLocation();
                mLocationOverlay.getMyLocationProvider().stopLocationProvider();
            }
        });
    }

    private void showActivateGPSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("GPS disattivato!")
                .setMessage("Vuoi attivare il GPS?")
                .setPositiveButton(R.string.settings, (dialog, which) -> setSettingsIntent())
                .setNegativeButton(R.string.close, (dialog, which) -> {}).show();
    }

    private void setSettingsIntent(){
        Intent intent = new Intent();
        intent.setAction((Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
            requireActivity().startActivity(intent);
        }
    }

    @Override
    protected void afterResponse(List<Location> locations) {
        setSearchLocationMarkers(locations);
    }
}
