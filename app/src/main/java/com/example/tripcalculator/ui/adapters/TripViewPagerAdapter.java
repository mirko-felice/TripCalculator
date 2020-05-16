package com.example.tripcalculator.ui.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripcalculator.fragments.NextTripsFragment;
import com.example.tripcalculator.fragments.PastTripsFragment;

import java.util.ArrayList;
import java.util.List;

public class TripViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> fragmentsTitles;

    public TripViewPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragments = new ArrayList<>();
        fragments.add(new NextTripsFragment());
        fragments.add(new PastTripsFragment());
        fragmentsTitles = new ArrayList<>();
        fragmentsTitles.add("Prossimi Viaggi");
        fragmentsTitles.add("Cronologia Viaggi");
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
