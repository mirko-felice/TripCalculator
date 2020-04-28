package com.example.tripcalculator;

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
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new PastTripsFragment());
        fragments.add(new NextTripsFragment());
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
