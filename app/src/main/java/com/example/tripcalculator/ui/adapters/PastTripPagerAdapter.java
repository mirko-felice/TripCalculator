package com.example.tripcalculator.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripcalculator.fragments.DiaryFragment;
import com.example.tripcalculator.fragments.PastTripLocationsFragment;

import java.util.ArrayList;
import java.util.List;

public class PastTripPagerAdapter extends FragmentPagerAdapter {

    List<String> pageTitles;
    List<Fragment> pages;

    public PastTripPagerAdapter(@NonNull FragmentManager fm, int behavior, int tripId) {
        super(fm, behavior);
        pageTitles = new ArrayList<>();
        pageTitles.add("Località");
        pageTitles.add("Diario");
        pages = new ArrayList<>();
        pages.add(new PastTripLocationsFragment(tripId));
        pages.add(new DiaryFragment(tripId));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }
}
