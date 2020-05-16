package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tripcalculator.R;
import com.example.tripcalculator.databinding.ActivityMainBinding;
import com.example.tripcalculator.ui.adapters.SummaryViewPagerAdapter;

public class MyFragment extends Fragment {

    private int tripId;

    public MyFragment(int tripId) {
        this.tripId = tripId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityMainBinding binding = ActivityMainBinding.inflate(inflater, container, false);
        binding.viewPager.setAdapter(new SummaryViewPagerAdapter(getParentFragmentManager(), tripId));
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.summary_menu, menu);
    }
}
