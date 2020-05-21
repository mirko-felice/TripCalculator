package com.example.tripcalculator.ui.recyclerview.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.activities.ModifyTripActivity;
import com.example.tripcalculator.broadcastReceiver.ReminderReceiver;
import com.example.tripcalculator.R;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.recyclerview.viewholders.TripViewHolder;
import com.example.tripcalculator.viewmodel.LocationViewModel;
import com.example.tripcalculator.viewmodel.TripViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {

    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private List<MaterialCardView> tripCards = new ArrayList<>();
    private AlertDialog alertDialog;
    private Trip lastTripDismiss;
    private int lastTripDismissPosition;
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
        Intent intent = new Intent(activity.getApplicationContext(), ReminderReceiver.class);
        Trip trip = trips.get(position);
        intent.putExtra("TripName", trip.Name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), ReminderReceiver.NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        tripViewModel = new ViewModelProvider(activity).get(TripViewModel.class);

        MaterialCardView card = (MaterialCardView) holder.itemView.findViewById(R.id.trip_card);
        tripCards.add(card);
        holder.setName(trip.Name);
        card.setOnLongClickListener(v -> {
            if(actionMode == null){
                card.setChecked(!card.isChecked());
                actionMode = activity.startActionMode(new TripActionModeCallback());
            } else{
                int countChecked = 0;
                for (MaterialCardView cardView : tripCards){
                    if(cardView.isChecked()) {
                        countChecked++;
                    }
                }
                if(countChecked == 1){
                    card.setChecked(!card.isChecked());
                    actionMode.finish();
                    actionMode = null;
                    return false;
                } else {
                    card.setChecked(!card.isChecked());
                }
            }
            return true;
        });
        card.setOnClickListener(v -> {
            if(trip.IsActive){
                Intent activeTripIntent = new Intent(activity.getApplicationContext(), TripActivity.class);
                activeTripIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(activeTripIntent);
            } else {
                Intent modifyIntent = new Intent(activity.getApplicationContext(), ModifyTripActivity.class);
                modifyIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(modifyIntent);
            }
        });
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
            //activity.registerReceiver(new AlarmReceiver(), new IntentFilter("com.example.tripcalculator.NOTIFICATION"));
            AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Calendar now = Calendar.getInstance();
            Calendar timeToSet = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, R.style.TimePickerDialog, (view, hourOfDay, minute) -> {
                timeToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeToSet.set(Calendar.MINUTE, minute);

                if(timeToSet.before(now)){
                    showSnack();
                    return;
                }
                assert alarmManager != null;
                alarmManager.setExact(AlarmManager.RTC, timeToSet.getTimeInMillis(), pendingIntent);
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
            timePickerDialog.setOnCancelListener(dialog -> showSnack());

            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    timeToSet.setTimeInMillis(datePicker.getSelection());
                }
                timePickerDialog.show();
            });
            datePicker.addOnNegativeButtonClickListener(v1 -> showSnack());
            datePicker.addOnCancelListener(dialog -> showSnack());
            datePicker.show(activity.getSupportFragmentManager(), datePicker.toString());
        });

        LiveData<List<Location>> locationLiveData = new ViewModelProvider(activity).get(LocationViewModel.class).getLocationsFromTrip(trip.TripId);
        locationLiveData.observe(activity, locations -> {
            if (locations.size() < 2) {
                holder.itemView.findViewById(R.id.start_trip_btn).setEnabled(false);
                holder.itemView.findViewById(R.id.plan_trip_btn).setEnabled(false);
            }
            locationLiveData.removeObservers(activity);
        });
        if (isTripActive){
            holder.itemView.findViewById(R.id.start_trip_btn).setEnabled(false);
        }
        if (trip.IsActive){
            holder.itemView.findViewById(R.id.plan_trip_btn).setEnabled(false);
        }
    }

    private void showSnack(){
        Snackbar.make(activity.findViewById(R.id.coordinator_layout),"Sorry", Snackbar.LENGTH_LONG).show();
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
                    //TODO Aggiornare alertdialog
                    alertDialog.show();
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
            actionMode = null;
        }
    }
}