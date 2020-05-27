package com.example.tripcalculator.ui.recyclerview.adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.ModifyTripActivity;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.broadcastReceiver.ReminderReceiver;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.fragments.PlanningFragment;
import com.example.tripcalculator.ui.recyclerview.viewholders.TripViewHolder;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {

    private static final String TRIP_ID = "TripId";
    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private List<MaterialCardView> tripCards = new ArrayList<>();
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private ActionMode actionMode;
    private TripViewModel tripViewModel;
    private boolean isTripActive;
    private boolean isTripPlanned;

    public TripAdapter(FragmentActivity activity, boolean isTripActive, boolean isTripPlanned) {
        this.activity = activity;
        this.isTripActive = isTripActive;
        this.isTripPlanned = isTripPlanned;
        this.alertDialogBuilder = new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_Light_Dialog)
                .setTitle(R.string.delete_trip_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteItems())
                .setNegativeButton(R.string.no, (dialog, which) -> deselectAllCards())
                .setOnDismissListener(dialog -> deselectAllCards());
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tripView = LayoutInflater.from(activity).inflate(R.layout.trip_view, parent, false);
        return new TripViewHolder(tripView);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        tripViewModel = new ViewModelProvider(activity).get(TripViewModel.class);

        MaterialCardView card = holder.getCard();
        tripCards.add(card);
        holder.setName(trip.Name);
        if (trip.IsActive) {
            holder.setTripInfoVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM yyyy - HH:mm", PreferenceManager.getDefaultSharedPreferences(activity).getString("language", "0").equals("0") ? Locale.ITALIAN : Locale.ENGLISH);
            assert trip.StartDate != null;
            holder.setTripInfo(dateFormat.format(trip.StartDate));
        }
        card.setOnLongClickListener(v -> {
            if (!trip.IsActive) {
                if (actionMode == null) {
                    card.setChecked(!card.isChecked());
                    actionMode = activity.startActionMode(new TripActionModeCallback());
                    Objects.requireNonNull(actionMode).setTitle(R.string.delete_trips);
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
                Intent activeTripIntent = new Intent(activity, TripActivity.class);
                activeTripIntent.putExtra(TRIP_ID, trip.TripId);
                activity.startActivity(activeTripIntent);
            } else {
                Intent modifyIntent = new Intent(activity, ModifyTripActivity.class);
                modifyIntent.putExtra(TRIP_ID, trip.TripId);
                activity.startActivity(modifyIntent);
            }
        });
        holder.setStartText(activity.getString(R.string.start_trip));
        holder.setPlanText(activity.getString(R.string.plan_trip));
        holder.setStartListener(v -> {
                    Intent startIntent = new Intent(activity, TripActivity.class);
                    trip.IsActive = true;
                    trip.StartDate = new Date();
                    tripViewModel.updateTrip(trip);
                    startIntent.putExtra(TRIP_ID, trip.TripId);
                    activity.startActivity(startIntent);
                    if (trip.IsPlanned)
                        deleteIntent(trip);
                }
        );
        holder.setPlanListener(v -> {
            if (!trip.IsPlanned) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    new PlanningFragment(trip).show(activity.getSupportFragmentManager(), "plan");
                else
                    showAlertDialog();
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
                builder.setTitle(R.string.remove_plan_title)
                        .setMessage(R.string.remove_planning_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> deleteIntent(trip))
                        .setNegativeButton(R.string.no, (dialog, which) -> {})
                        .show();
            }
        });

        LiveData<List<Location>> locationLiveData = new ViewModelProvider(activity).get(LocationViewModel.class).getLocationsFromTrip(trip.TripId);
        locationLiveData.observe(activity, locations -> {
            if (locations.size() < 2) {
                holder.setStartEnabled(false);
                holder.setPlanEnabled(false);
                holder.setTripInfo(activity.getString(R.string.start_error));
            } else if (!trip.IsActive){
                holder.setTripInfoVisibility(View.GONE);
            }
            locationLiveData.removeObservers(activity);
        });
        if (isTripActive) {
            holder.setStartEnabled(false);
            holder.setPlanEnabled(false);
        }
        if (isTripPlanned) {
            if (!trip.IsPlanned) {
                holder.setStartEnabled(false);
                holder.setPlanEnabled(false);
            } else {
                holder.setPlanText(activity.getString(R.string.remove_plan));
            }
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

    private void deleteIntent(Trip trip){
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, ReminderReceiver.class);
        intent.putExtra("TripName", trip.Name);
        intent.putExtra(TRIP_ID, trip.TripId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, ReminderReceiver.NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        trip.IsPlanned = false;
        TripViewModel tripViewModel = new ViewModelProvider(activity).get(TripViewModel.class);
        tripViewModel.updateTrip(trip);
        Snackbar.make(activity.findViewById(R.id.coordinator_layout), R.string.planning_removed, Snackbar.LENGTH_LONG).show();
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
                    alertDialogBuilder.show();
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

    private void showAlertDialog() {
        Toast.makeText(activity, R.string.version_error, Toast.LENGTH_LONG).show();
    }
}
