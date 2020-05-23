package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.databinding.ActivityTripBinding;
import com.example.tripcalculator.fragments.LoaderFragment;
import com.example.tripcalculator.fragments.MapFragment;
import com.example.tripcalculator.fragments.SummaryTripFragment;
import com.example.tripcalculator.ui.viewpager.LocationViewPagerAdapter;
import com.example.tripcalculator.utility.NetUtility;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class TripActivity extends BaseActivity {

    private static final String TRIP_ID = "TripId";
    private int tripId = -1;
    private ActivityTripBinding binding;
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private SummaryTripFragment myFragment;
    Snackbar netSnackbar;

    private List<Location> path;
    private LocationViewModel locationViewModel;

    //TODO creare dialog fragment con imposta ora e data per il pianifica

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        binding = ActivityTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        netSnackbar = Snackbar.make(binding.getRoot(), "No Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Impostazioni", (v) -> { NetUtility.setNetSettingsIntent(this); });
        LoaderFragment loader = new LoaderFragment((ViewGroup) binding.getRoot());
        loader.show(getSupportFragmentManager(), "loader");

        ViewPager2 viewPager = binding.locationViewPager;
        viewPager.setVisibility(View.GONE);

        if (intent.hasExtra(TRIP_ID)){
            tripId = intent.getIntExtra(TRIP_ID, -1);

            locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
            LiveData<List<Location>> locationsLiveData = locationViewModel.getLocationsFromTrip(tripId);
            locationsLiveData.observe(this, locations -> {
                path.addAll(locations);

                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                myFragment = new SummaryTripFragment(tripId);
                fragmentTransaction1.add(R.id.activity_active_trip_layout, myFragment);
                fragmentTransaction1.hide(myFragment);
                fragmentTransaction1.commit();

                LocationViewPagerAdapter adapter = new LocationViewPagerAdapter(this, locations);
                viewPager.setAdapter(adapter);
                viewPager.setUserInputEnabled(false);
                viewPager.setVisibility(View.VISIBLE);
                for (Location location: locations){
                    if(!location.IsPassed){
                        viewPager.setCurrentItem(locations.indexOf(location), true);
                        break;
                    }
                }
                viewPager.setOnClickListener(v -> mapFragment.focusOn(locations.get(viewPager.getCurrentItem())));
                binding.slideLeft.setOnClickListener(v -> {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() == 0 ?  0: viewPager.getCurrentItem() - 1, true);
                    mapFragment.focusOn(locations.get(viewPager.getCurrentItem()));
                });
                binding.slideRight.setOnClickListener(v -> {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() == locations.size() - 1 ? locations.size() - 1: viewPager.getCurrentItem() + 1, true);
                    mapFragment.focusOn(locations.get(viewPager.getCurrentItem()));
                });
                if (NetUtility.isNetworkConnected()) {
                    mapFragment = new MapFragment(path);
                } else {
                    mapFragment = new MapFragment();
                    netSnackbar.show();
                }
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_layout, mapFragment)
                        .commit();
                locationsLiveData.removeObservers(this);
                loader.dismiss();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.details){
            if(myFragment.isVisible()){
                item.setIcon(R.drawable.ic_details);
                fragmentManager.beginTransaction().hide(myFragment).commit();
            } else {
                item.setIcon(R.drawable.ic_map);
                fragmentManager.beginTransaction().show(myFragment).commit();
            }
            return true;
        }
        return false;
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

    public void setLocationAsPassed(int index){
        path.get(index).IsPassed = true;
        locationViewModel.updateLocation(path.get(index));
        if (NetUtility.isNetworkConnected()) {
            mapFragment.updatePassedLocation(index);
        } else {
            netSnackbar.show();
        }
        Utilities.createLocationNotification(getApplicationContext(), path.get(index));
    }
}
