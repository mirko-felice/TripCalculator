package com.example.tripcalculator.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

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
        binding.addLocation.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        setContentView(binding.getRoot());

        viewModel = new LocationViewModelFactory(getApplication(), getIntent().getIntExtra("TripId", 0)).create(LocationViewModel.class);
        viewModel.getLocations().observe(this, adapter::updateLocations);
    }
}
