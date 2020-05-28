package com.example.tripcalculator.ui.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.LocationFragment;
import com.example.tripcalculator.fragments.MapFragment;

import java.util.List;

public class LocationViewPagerAdapter extends FragmentStateAdapter {

    private final List<Location> locations;
    private final MapFragment mapFragment;


    public LocationViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Location> locations, MapFragment mapFragment) {
        super(fragmentManager, lifecycle);
        this.locations = locations;
        this.mapFragment = mapFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new LocationFragment(locations.get(position), mapFragment);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
