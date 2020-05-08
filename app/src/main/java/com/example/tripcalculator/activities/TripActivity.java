package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.ActivityTripBinding;
import com.example.tripcalculator.ui.adapters.LocationRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.LocationViewModelFactory;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.j256.ormlite.stmt.query.In;

import java.util.concurrent.Executors;

public class TripActivity extends AppCompatActivity {

    private Trip trip;
    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTripBinding binding = ActivityTripBinding.inflate(getLayoutInflater());
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter(this);
        binding.locations.setAdapter(adapter);
        TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TripViewModel.class);
        Intent searchIntent = new Intent(this, SearchActivity.class);
        viewModel.getLastInsertedTrip().observe(this, trip -> {
            this.trip = trip;
            binding.tripName.setText(trip.Name);
            locationViewModel = new LocationViewModelFactory(getApplication(), trip.TripId).create(LocationViewModel.class);
            locationViewModel.getLocations().observe(this, adapter::updateLocations);
            searchIntent.putExtra("TripId", trip.TripId);
        });
        /*binding.tripName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                trip.Name = s.toString();
                Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(getApplicationContext()).tripDao().updateTrip(trip));
            }
        });*/
        binding.addLocationBtn.setOnClickListener(v -> {
            startActivity(searchIntent);
        });
        setContentView(binding.getRoot());
    }
}
