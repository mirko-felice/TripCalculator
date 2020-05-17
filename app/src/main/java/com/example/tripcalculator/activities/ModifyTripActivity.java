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
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.ActivityModifyTripBinding;
import com.example.tripcalculator.databinding.ActivityTripBinding;
import com.example.tripcalculator.ui.TripItemTouchHelper;
import com.example.tripcalculator.ui.adapters.LocationRecyclerViewAdapter;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ModifyTripActivity extends AppCompatActivity {

    private Trip trip;
    private ActivityModifyTripBinding binding;
    TripViewModel tripViewModel;
    private LocationViewModel locationViewModel;
    private LocationRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModifyTripBinding.inflate(getLayoutInflater());
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationRecyclerViewAdapter(this);
        binding.locations.setAdapter(adapter);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        Intent searchIntent = new Intent(this, SearchActivity.class);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Intent intent = getIntent();
        if(intent.hasExtra("TripId")){
            tripViewModel.getTripFromId(intent.getIntExtra("TripId", -1)).observe(this, trip1 -> {
                this.trip = trip1;
                initActivity(searchIntent);
            });
        } else {
            tripViewModel.getLastInsertedTrip().observe(this, trip -> {
                this.trip = trip;
                initActivity(searchIntent);
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

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void initActivity(Intent searchIntent){
        binding.tripName.setText(trip.Name);
        locationViewModel.getLocationsFromTrip(trip.TripId).observe(this, locations -> {
            if (locations.size() == 0){
                binding.addLocationBtn.setText(getString(R.string.add_start_point));
            } else {
                adapter.updateLocations(locations);
                binding.addLocationBtn.setText(getString(R.string.add_location));
            }
        });
        searchIntent.putExtra("TripId", trip.TripId);
    }

    private void saveChanges() {
        this.trip.Name = Objects.requireNonNull(binding.tripName.getText()).toString();
        tripViewModel.updateTrip(trip);
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Le modifiche sono state salvate.", 400);
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

    private void showDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Stai per tornare indietro, vuoi salvare le modifiche al tuo viaggio?");
        builder.setPositiveButton("Si", (dialog, which) -> saveChanges());
        builder.setNegativeButton("No", (dialog, which) -> super.onBackPressed());
        builder.show();
    }
}
