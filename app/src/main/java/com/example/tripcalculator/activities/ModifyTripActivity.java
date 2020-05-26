package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.databinding.ActivityModifyTripBinding;
import com.example.tripcalculator.ui.recyclerview.adapters.TripItemTouchHelper;
import com.example.tripcalculator.ui.recyclerview.adapters.LocationAdapter;
import com.example.tripcalculator.utility.IOptimizeCallback;
import com.example.tripcalculator.utility.PathOptimizingTask;
import com.example.tripcalculator.utility.Utilities;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ModifyTripActivity extends BaseActivity implements IOptimizeCallback {

    private static final String TRIP_ID = "TripId";
    private Trip trip;
    private ActivityModifyTripBinding binding;
    private TripViewModel tripViewModel;
    private LocationViewModel locationViewModel;
    private LocationAdapter adapter;
    private boolean canStart;

    //TODO evidenziare la possibilità di ordinare manualemnete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyTripBinding.inflate(getLayoutInflater());
        binding.locations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(this);
        binding.locations.setAdapter(adapter);
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        Intent searchIntent = new Intent(this, SearchActivity.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        LiveData<Trip> tripLiveData;

        Intent intent = getIntent();
        if(intent.hasExtra(TRIP_ID)){
            tripLiveData = tripViewModel.getTripFromId(intent.getIntExtra(TRIP_ID, -1));
        } else {
            tripLiveData = tripViewModel.getLastInsertedTrip();
        }

        tripLiveData.observe(this, trip -> {
            this.trip = trip;
            binding.tripName.setText(trip.Name);
            LiveData<List<Location>> listLiveData = locationViewModel.getLocationsFromTrip(trip.TripId);
            listLiveData.observe(this, locations -> {
                if (locations.size() == 0){
                    canStart = false;
                    binding.startingLocationLabel.setVisibility(View.GONE);
                    binding.addLocationBtn.setText(getString(R.string.add_start_point));
                } else {
                    canStart = locations.size() > 1;
                    adapter.updateLocations(locations);
                    binding.startingLocationLabel.setVisibility(View.VISIBLE);
                    binding.addLocationBtn.setText(getString(R.string.add_location));
                }
                binding.optimizeBtn.setOnClickListener(v -> new PathOptimizingTask(this,this).execute(locations.toArray(new Location[0])));
               Utilities.hideKeyboard(this);
            });
            searchIntent.putExtra(TRIP_ID, trip.TripId);
            tripLiveData.removeObservers(this);
        });

        binding.addLocationBtn.setOnClickListener(v -> startActivity(searchIntent));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TripItemTouchHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT, adapter));
        itemTouchHelper.attachToRecyclerView(binding.locations);

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modify_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.start_trip){
            saveChanges();
            if (canStart) {
                Intent intent = new Intent(this, TripActivity.class);
                intent.putExtra(TRIP_ID, trip.TripId);
                trip.IsActive = true;
                trip.StartDate = new Date();
                tripViewModel.updateTrip(trip);
                startActivity(intent);
                finish();
            } else {
                showStartDialog();
            }
            return true;
        } else if(item.getItemId() == android.R.id.home){
            this.onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (binding.tripName.getText() != null && binding.tripName.getText().toString().equals("")){
            showAlertDialog();
        } else if(binding.tripName.getText() != null && !trip.Name.equals(binding.tripName.getText().toString())) {
            showDialog();
        } else {
            tripViewModel.updateTrip(trip);
            super.onBackPressed();
        }
    }

    private void showAlertDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Il tuo viaggio non ha un nome, dagliene uno!");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showStartDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Il tuo viaggio non può iniziare se non inserisci almeno 2 località!");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void saveChanges() {
        if(binding.tripName.getText() != null && !binding.tripName.getText().toString().equals("")) {
            this.trip.Name = binding.tripName.getText().toString();
            tripViewModel.updateTrip(trip);
            /*Snackbar snackbar = Snackbar.make(binding.getRoot(), getString(R.string.save_changes), 400);
            snackbar.setAnchorView(binding.addLocationBtn);
            snackbar.show();
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT)
                        finish();
                }
            });*/
        }
    }

    private void showDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Attenzione");
        builder.setMessage("Stai per tornare indietro, vuoi salvare le modifiche al tuo viaggio?");
        builder.setPositiveButton("Si", (dialog, which) -> {
            saveChanges();
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> super.onBackPressed());
        builder.show();
    }

    @Override
    public void updateLocations(List<Location> locations) {
        adapter.updateLocations(locations);
    }

    @Override
    public void updateLocation(Location location) {
        locationViewModel.updateLocation(location);
    }

}
