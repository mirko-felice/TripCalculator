package com.example.tripcalculator.ui.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.Utility.DatabaseQueryHelper;
import com.example.tripcalculator.activities.ActiveTripActivity;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.broadcastReceiver.ReminderReceiver;
import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.TripViewHolder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripViewHolder> {

    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private List<MaterialCardView> tripCards = new ArrayList<>();
    private AlertDialog alertDialog;
    private Trip lastTripDismiss;
    private int lastTripDismissPosition;
    private ActionMode actionMode;

    public TripRecyclerViewAdapter(FragmentActivity activity) {
        this.activity = activity;
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), ReminderReceiver.NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);

        Trip trip = trips.get(position);
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
                Intent activeTripIntent = new Intent(activity.getApplicationContext(), ActiveTripActivity.class);
                activeTripIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(activeTripIntent);
            } else {
                Intent modifyIntent = new Intent(activity.getApplicationContext(), TripActivity.class);
                modifyIntent.putExtra("TripId", trip.TripId);
                activity.startActivity(modifyIntent);
            }
        });
        holder.itemView.findViewById(R.id.start_trip_btn).setOnClickListener(v -> {
            Intent startIntent = new Intent(activity.getApplicationContext(), ActiveTripActivity.class);
            trip.IsActive = true;
            trip.StartDate = new Date();
            Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(activity.getApplicationContext()).tripDao().updateTrip(trip));
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
                DatabaseQueryHelper.delete(trip, activity.getApplicationContext());
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
            inflater.inflate(R.menu.delete_menu, menu);
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
