package com.example.tripcalculator.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ActivitySearchBinding;
import com.example.tripcalculator.fragments.MapFragment;
import com.example.tripcalculator.fragments.SearchResultFragment;
import com.example.tripcalculator.utility.NetUtility;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private static final int REQUEST_INTERNET_PERMISSION = 1;
    private static final String TRIP_ID = "TripId";
    private SearchView searchView;
    //permission vars
    FragmentManager fragmentManager;
    Snackbar netSnackbar;

    //tripId
    private int tripId = -1;

    ActivitySearchBinding binding;
    SearchResultFragment searchResultFragment;
    MapFragment mapFragment;
    MenuItem searchMenuItem;
    private List<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        netSnackbar = Snackbar.make(findViewById(R.id.search_layout), "No Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Impostazioni", (v) -> NetUtility.setNetSettingsIntent(this));

        fragmentManager = getSupportFragmentManager();

        searchResultFragment = new SearchResultFragment();
        mapFragment = new MapFragment();
        fragmentManager.beginTransaction()
                        .add(R.id.search_layout, mapFragment)
                        .add(R.id.search_layout, searchResultFragment)
                        .hide(searchResultFragment)
                        .commit();

        Intent intent = getIntent();
        if (intent.hasExtra(TRIP_ID)){
            this.tripId = intent.getIntExtra(TRIP_ID, -1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetUtility.registerNetworkCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetUtility.unregisterNetworkCallback(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            if (NetUtility.isNetworkConnected()) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                searchResultFragment.executeQueue(query);
                Utilities.hideKeyboard(this);
            } else {
                netSnackbar.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView =  (SearchView) searchMenuItem.getActionView();
        if (searchManager != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(searchResultFragment)
                        .commit();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(searchResultFragment)
                        .commit();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(searchResultFragment)
                    .commit();
            if(locations.size() == 0){
                searchView.requestFocus();
                Utilities.showKeyboard(this, searchView);
            }
            return true;
        }
        return false;
    }

    //TODO Spostare nella splash Activity
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Snackbar.make(findViewById(R.id.map_fragment), "Impossibile utilizzare la mappa senza l'accesso ad internet!", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }*/

    public void setSearchResult(List<Location> locations){
        Utilities.hideKeyboard(this);
        mapFragment.setSearchLocationMarkers(locations);
        this.locations = locations;
    }

    public void focusOn(Location location){
        searchMenuItem.collapseActionView();
        mapFragment.focusOn(location);
    }

    public void addLocationToTrip(Location location){
        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        LiveData<List<Location>> liveData = locationViewModel.getLocationsFromTrip(tripId);
        liveData.observe(this, locations -> {
            location.Id = 0;
            location.TripId = tripId;
            location.Order = locations.size() > 0 ? locations.get(locations.size() - 1).Order + 1 : 1;
            location.IsPassed = false;
            location.FullName = location.DisplayName;
            locationViewModel.insertLocation(location);
            liveData.removeObservers(this);
            finish();
        });
    }

    //TODO Spostare nella splash Activity
    /*private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }
    }*/
}
