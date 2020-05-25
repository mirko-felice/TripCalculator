package com.example.tripcalculator.ui.recyclerview.adapters;

import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.ModifyTripActivity;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.fragments.PlanningFragment;
import com.example.tripcalculator.ui.recyclerview.viewholders.TripViewHolder;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {

    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private List<MaterialCardView> tripCards = new ArrayList<>();
    private AlertDialog alertDialog;
    private ActionMode actionMode;
    private TripViewModel tripViewModel;
    private boolean isTripActive;

    public TripAdapter(FragmentActivity activity, boolean isTripActive) {
        this.activity = activity;
        this.isTripActive = isTripActive;
        this.alertDialog = new AlertDialog.Builder(activity, R.style.Theme_MaterialComponents_Light_Dialog)
                .setTitle("Sei sicuro di voler eliminare i viaggi selezionati?")
                .setPositiveButton("SI", (dialog, which) -> deleteItems())
                .setNegativeButton("NO", (dialog, which) -> deselectAllCards())
                .setOnDismissListener(dialog -> deselectAllCards())
                .create();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.trip_view, parent, false);
        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        tripViewModel = new ViewModelProvider(activity).get(TripViewModel.class);

        MaterialCardView card = holder.itemView.findViewById(R.id.trip_card);
        tripCards.add(card);
        holder.setName(trip.Name);
        if (trip.IsActive) {
            holder.itemView.findViewById(R.id.trip_info).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.trip_info)).setText(trip.StartDate.toString());
        }
        card.setOnLongClickListener(v -> {
            if (!trip.IsActive) {
                if (actionMode == null) {
                    card.setChecked(!card.isChecked());
                    actionMode = activity.startActionMode(new TripActionModeCallback());
                    actionMode.setTitle("Elimina Viaggi");
                } else {
                    int countChecked = 0;
                    card.setChecked(!card.isChecked());
                    for (MaterialCardView cardView : tripCards) {
                        if (cardView.isChecked()) {
                            countChecked++;
                        }
                    }
                    if (countChecked == 0) {
                        actionMode.finish();
                        actionMode = null;
                    }
                }
            }
            return true;
        });
        card.setOnClickListener(v -> {
            if (trip.IsActive) {
                Intent activeTripIntent = new Intent(activity.getApplicationContext(), TripActivity.class);
                activeTripIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(activeTripIntent);
            } else {
                Intent modifyIntent = new Intent(activity.getApplicationContext(), ModifyTripActivity.class);
                modifyIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(modifyIntent);
            }
        });
        ((Button)holder.itemView.findViewById(R.id.start_trip_btn)).setText(activity.getString(R.string.start_trip));
        holder.itemView.findViewById(R.id.start_trip_btn).setOnClickListener(v -> {
                    Intent startIntent = new Intent(activity.getApplicationContext(), TripActivity.class);
                    trip.IsActive = true;
                    trip.StartDate = new Date();

                    tripViewModel.updateTrip(trip);
                    startIntent.putExtra("TripId", trip.TripId);
                    activity.startActivity(startIntent);
                }
        );
        holder.itemView.findViewById(R.id.plan_trip_btn).setOnClickListener(v -> {
            new PlanningFragment(trip.Name).show(activity.getSupportFragmentManager(), "plan");
        });

        LiveData<List<Location>> locationLiveData = new ViewModelProvider(activity).get(LocationViewModel.class).getLocationsFromTrip(trip.TripId);
        locationLiveData.observe(activity, locations -> {
            if (locations.size() < 2) {
                holder.itemView.findViewById(R.id.start_trip_btn).setEnabled(false);
                holder.itemView.findViewById(R.id.plan_trip_btn).setEnabled(false);
                ((TextView) holder.itemView.findViewById(R.id.trip_info)).setText("Per poter partire il viaggio deve avere almeno 2 località.");
            } else if(!trip.IsActive){
                holder.itemView.findViewById(R.id.trip_info).setVisibility(View.GONE);
            }
            locationLiveData.removeObservers(activity);
        });
        if (isTripActive) {
            holder.itemView.findViewById(R.id.start_trip_btn).setEnabled(false);
        }
        if (trip.IsActive) {
            holder.itemView.findViewById(R.id.plan_trip_btn).setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateTrips(List<Trip> trips) {
        this.trips = trips;
        this.tripCards.clear();
        notifyDataSetChanged();
    }

    private void deleteItems(){
        for (MaterialCardView card : tripCards){
            if(card.isChecked()){
                Trip trip = trips.get(tripCards.indexOf(card));
                tripViewModel.deleteTrip(trip);
            }
        }
        deselectAllCards();
    }

    public void deselectAllCards() {
        for(MaterialCardView card : tripCards){
            card.setChecked(false);
        }
    }

    class TripActionModeCallback implements ActionMode.Callback {

        private boolean isBackPressed = true;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.delete:
                    alertDialog.show();
                    isBackPressed = false;
                    mode.finish();
                    return true;
                case android.R.id.home:
                    deselectAllCards();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(isBackPressed)
                deselectAllCards();
            actionMode = null;
        }
    }
}
