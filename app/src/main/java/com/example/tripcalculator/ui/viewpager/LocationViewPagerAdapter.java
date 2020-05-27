package com.example.tripcalculator.ui.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.fragments.LocationFragment;

import java.util.List;

public class LocationViewPagerAdapter extends FragmentStateAdapter {

    private final List<Location> locations;


    public LocationViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Location> locations) {
        super(fragmentManager, lifecycle);
        this.locations = locations;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new LocationFragment(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
