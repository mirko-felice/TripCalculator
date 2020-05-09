package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.Utility.DatabaseQueryHelper;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.ActivityTripBinding;
import com.example.tripcalculator.ui.TripItemTouchHelper;
import com.example.tripcalculator.ui.adapters.LocationRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.LocationViewModelFactory;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class TripActivity extends AppCompatActivity {

    private Trip trip;
    private LocationViewModel locationViewModel;
    private ActivityTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTripBinding.inflate(getLayoutInflater());
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        LocationRecyclerViewAdapter adapter = new LocationRecyclerViewAdapter(this);
        binding.locations.setAdapter(adapter);
        TripViewModel tripViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TripViewModel.class);
        Intent searchIntent = new Intent(this, SearchActivity.class);

        Intent intent = getIntent();
        if(intent.hasExtra("TripId")){
            //TODO modifica
            tripViewModel.getTripFromId(intent.getIntExtra("TripId", -1)).observe(this, trip1 -> {
                this.trip = trip1;
                binding.tripName.setText(trip.Name);
                locationViewModel = new LocationViewModelFactory(getApplication(), trip.TripId).create(LocationViewModel.class);
                locationViewModel.getLocations().observe(this, adapter::updateLocations);
                searchIntent.putExtra("TripId", trip.TripId);
            });
        } else {
            //TODO creazione
            tripViewModel.getLastInsertedTrip().observe(this, trip -> {
                this.trip = trip;
                binding.tripName.setText(trip.Name);
                locationViewModel = new LocationViewModelFactory(getApplication(), trip.TripId).create(LocationViewModel.class);
                locationViewModel.getLocations().observe(this, adapter::updateLocations);
                searchIntent.putExtra("TripId", trip.TripId);
            });
        }

        binding.addLocationBtn.setOnClickListener(v -> {
            startActivity(searchIntent);
        });
        binding.optimizeBtn.setOnClickListener(v -> {}); //TODO Aggiungere ottimizzazione

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TripItemTouchHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT, adapter));
        itemTouchHelper.attachToRecyclerView(binding.locations);

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save_changes){
            saveChanges();
            return true;
        }
        return false;
    }

    private void saveChanges() {
        this.trip.Name = Objects.requireNonNull(binding.tripName.getText()).toString();
        DatabaseQueryHelper.update(trip, this);
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Le modifiche sono state salvate.", 1000);
        snackbar.setAnchorView(binding.addLocationBtn);
        snackbar.show();
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                    finish();
            }
        });
    }
}
