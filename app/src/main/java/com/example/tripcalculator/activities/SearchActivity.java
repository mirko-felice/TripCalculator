package com.example.tripcalculator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.Utility.Utilities;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.MapFragment;
import com.example.tripcalculator.fragments.SearchResultFragment;
import com.example.tripcalculator.databinding.ActivitySearchBinding;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_INTERNET_PERMISSION = 2;
    //map vars
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private SearchView searchView;
    //permission vars
    private static boolean isLocationAllowed = false;
    private static boolean isNetworkConnected = false;
    FragmentManager fragmentManager;
    Snackbar snackbar;

    //tripId
    private int tripId = -1;

    ActivitySearchBinding binding;
    SearchResultFragment searchResultFragment;
    MapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        snackbar = Snackbar.make(findViewById(R.id.search_layout), "No Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Impostazioni", (v) -> { setSettingsIntent(); });

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        searchResultFragment = new SearchResultFragment();
        mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.search_layout, mapFragment);
        fragmentTransaction.add(R.id.search_layout, searchResultFragment);
        fragmentTransaction.hide(searchResultFragment);
        fragmentTransaction.commit();
        Intent intent = getIntent();
        if (intent.hasExtra("TripId")){
            this.tripId = intent.getIntExtra("TripId", -1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerNetworkCallback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchResultFragment.executeQueue(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView =  (SearchView) searchMenuItem.getActionView();
        if (searchManager != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                closeSearch();
                return true;
            }
        });
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    fragmentTransaction.show(searchResultFragment).commit();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mapFragment.clearMarkers();
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.show(searchResultFragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isLocationAllowed = true;
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.map_fragment), "Permessi negati!\nLa tua posizione non può essere visualizzata sulla mappa!", Snackbar.LENGTH_LONG);
                }
                break;
            case REQUEST_INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    //TODO modificare "map_fragment" nel frammento alternativo della visualizzazione del percorso
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.map_fragment), "Impossibile utilizzare la mappa senza l'accesso ad internet!", Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    private void closeSearch(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.hide(searchResultFragment).commit();
    }

    public void setSearchResult(List<Location> locations){
        Utilities.hideKeyboard(this);
        mapFragment.setSearchLocationMarkers(locations);
    }

    public void focusOn(Location location){
        closeSearch();
        mapFragment.focusOn(location);
    }

    public void addLocationToTrip(Location location){
        LiveData<List<Location>> liveData = AppDatabase.getInstance(this).locationDao().getLocationsFromTrip(tripId);
        liveData.observe(this, locations -> {
            location.Id = 0;
            location.TripId = tripId;
            location.Order = locations.size() > 0 ? locations.get(locations.size() - 1).Order + 1 : 1;
            location.IsPassed = false;
            Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(this).locationDao().insertLocation(location));
            liveData.removeObservers(this);
            finish();
        });
    }

    private void setSettingsIntent() {
        Intent intent = new Intent();
        intent.setAction((Settings.ACTION_WIRELESS_SETTINGS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void checkPermissions(){
        isLocationAllowed = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
        }else {
            isLocationAllowed = true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }
    }

    private void registerNetworkCallback(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService((Context.CONNECTIVITY_SERVICE));

        if (connectivityManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                isNetworkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            isNetworkConnected = false;
        }
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            isNetworkConnected = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            isNetworkConnected = false;
        }
    };
}
