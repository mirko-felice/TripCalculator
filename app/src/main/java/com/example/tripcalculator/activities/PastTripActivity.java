package com.example.tripcalculator.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tripcalculator.databinding.ActivityPastTripBinding;
import com.example.tripcalculator.ui.adapters.PastTripPagerAdapter;

public class PastTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().hasExtra("TripId")){
            finish();
        }
        int tripId = getIntent().getIntExtra("TripId", -1);
        ActivityPastTripBinding binding = ActivityPastTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.pastTripViewPager.setAdapter(new PastTripPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tripId));
    }
}
