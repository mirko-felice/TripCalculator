package com.example.tripcalculator.ui.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripcalculator.R;
import com.example.tripcalculator.fragments.NextTripsFragment;
import com.example.tripcalculator.fragments.PastTripsFragment;

import java.util.ArrayList;
import java.util.List;

public class TripViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> fragmentsTitles;

    public TripViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragments = new ArrayList<>();
        fragments.add(new NextTripsFragment());
        fragments.add(new PastTripsFragment());
        fragmentsTitles = new ArrayList<>();
        fragmentsTitles.add(context.getString(R.string.next_trips));
        fragmentsTitles.add(context.getString(R.string.past_trips));
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
