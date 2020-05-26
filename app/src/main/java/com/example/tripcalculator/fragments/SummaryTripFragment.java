package com.example.tripcalculator.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.tripcalculator.databinding.ActivityMainBinding;
import com.example.tripcalculator.ui.viewpager.SummaryViewPagerAdapter;

public class SummaryTripFragment extends Fragment {

    private int tripId;

    public SummaryTripFragment(int tripId) {
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityMainBinding binding = ActivityMainBinding.inflate(inflater, container, false);
        binding.getRoot().setBackgroundColor(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? Color.BLACK : Color.WHITE);
        binding.viewPager.setAdapter(new SummaryViewPagerAdapter(getParentFragmentManager(), tripId, requireContext()));
        return binding.getRoot();
    }
}
