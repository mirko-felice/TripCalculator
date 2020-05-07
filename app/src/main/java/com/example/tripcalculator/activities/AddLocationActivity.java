package com.example.tripcalculator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.viewmodel.LocationViewModelFactory;
import com.example.tripcalculator.databinding.ActivityAddLocationBinding;
import com.example.tripcalculator.ui.adapters.LocationRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;

public class AddLocationActivity extends AppCompatActivity {

    private ActivityAddLocationBinding binding;
    private LocationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter(this);
        binding.locations.setAdapter(adapter);
        setContentView(binding.getRoot());
        LiveData<Trip> tripData = AppDatabase.getInstance(this).tripDao().getLastInsertedTrip();
        final Intent searchIntent = new Intent(this, SearchActivity.class);;
        tripData.observe(this, trip -> {
            viewModel = new LocationViewModelFactory(getApplication(), trip.TripId).create(LocationViewModel.class);
            viewModel.getLocations().observe(this, adapter::updateLocations);
            searchIntent.putExtra("TripId", tripData.getValue().TripId);
        });
        binding.addLocation.setOnClickListener(v -> startActivity(searchIntent));
    }
}
