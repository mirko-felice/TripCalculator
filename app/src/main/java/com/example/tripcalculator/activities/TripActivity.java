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
import com.example.tripcalculator.utility.InternetUtility;
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
    private Snackbar netSnackbar;
    private ViewPager2 viewPager;

    private List<Location> path;
    private LocationViewModel locationViewModel;

    //TODO inserire "data inizio" e "data fine"
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        binding = ActivityTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        netSnackbar = Snackbar.make(binding.getRoot(), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, (v) -> InternetUtility.setNetSettingsIntent(this));
        LoaderFragment loader = new LoaderFragment((ViewGroup) binding.getRoot());
        loader.show(getSupportFragmentManager(), "loader");


        viewPager = binding.locationViewPager;
        viewPager.setVisibility(View.GONE);

        if (intent.hasExtra(TRIP_ID)){
            tripId = intent.getIntExtra(TRIP_ID, -1);

            locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
            LiveData<List<Location>> locationsLiveData = locationViewModel.getLocationsFromTrip(tripId);
            locationsLiveData.observe(this, locations -> {
                path.addAll(locations);

                LocationViewPagerAdapter adapter = new LocationViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), locations);

                viewPager.setAdapter(adapter);

                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                myFragment = new SummaryTripFragment(tripId);
                fragmentTransaction1.add(R.id.activity_active_trip_layout, myFragment);
                fragmentTransaction1.hide(myFragment);
                fragmentTransaction1.commit();

                viewPager.setOnClickListener(v -> mapFragment.focusOn(locations.get(viewPager.getCurrentItem())));
                binding.slideLeft.setOnClickListener(v -> {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() == 0 ?  0: viewPager.getCurrentItem() - 1, true);
                    mapFragment.focusOn(locations.get(viewPager.getCurrentItem()));
                });
                binding.slideRight.setOnClickListener(v -> {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() == locations.size() - 1 ? locations.size() - 1: viewPager.getCurrentItem() + 1, true);
                    mapFragment.focusOn(locations.get(viewPager.getCurrentItem()));
                });
                if (InternetUtility.isNetworkConnected()) {
                    mapFragment = new MapFragment(path);
                } else {
                    mapFragment = new MapFragment();
                    netSnackbar.show();
                }
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_layout, mapFragment)
                        .commit();
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if(mapFragment.isVisible())
                            mapFragment.focusOn(locations.get(position));
                    }
                });
                viewPager.setVisibility(View.VISIBLE);
                for (Location location: locations){
                    if(!location.IsPassed){
                        viewPager.post(() -> viewPager.setCurrentItem(locations.indexOf(location), true));
                        break;
                    }
                }

                locationsLiveData.removeObservers(this);
                loader.dismiss();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        InternetUtility.registerNetworkCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InternetUtility.unregisterNetworkCallback(this);
    }

    public void setLocationAsPassed(int index){
        path.get(index).IsPassed = true;
        locationViewModel.updateLocation(path.get(index));
        viewPager.setCurrentItem(index + 1, true);
        if (InternetUtility.isNetworkConnected()) {
            mapFragment.updatePassedLocation(index);
        } else {
            netSnackbar.show();
        }
        Utilities.createLocationNotification(this, path.get(index));
    }
}
