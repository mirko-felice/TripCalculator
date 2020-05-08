package com.example.tripcalculator.ui.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripcalculator.broadcastReceiver.ReminderReceiver;
import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.database.Trip;
import com.example.tripcalculator.ui.TripActionModeCallback;
import com.example.tripcalculator.ui.TripViewHolder;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripViewHolder> implements ItemTouchHelperAdapter {

    private FragmentActivity activity;
    private List<Trip> trips = new ArrayList<>();
    private AlertDialog alertDialog;
    private Trip lastTripDismiss;
    private int lastTripDismissPosition;

    public TripRecyclerViewAdapter(FragmentActivity activity) {
        this.activity = activity;
        this.alertDialog = new AlertDialog.Builder(activity, R.style.Theme_MaterialComponents_Light_Dialog)
                .setTitle("Sei sicuro di voler eliminare il viaggio?")
                .setPositiveButton("SI", (dialog, which) -> deleteItem())
                .setNegativeButton("NO", (dialog, which) -> notifyDataSetChanged())
                .setOnDismissListener(dialog -> notifyDataSetChanged())
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
        holder.setName(trip.Name);
        holder.itemView.setOnLongClickListener(v -> {
            activity.startActionMode(new TripActionModeCallback(trip, activity.getApplicationContext()));
            return true;
        });
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
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(trips, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(trips, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        lastTripDismiss = trips.get(position);
        lastTripDismissPosition = position;
        this.alertDialog.show();
    }

    private void deleteItem(){
        Executors.newSingleThreadExecutor().execute(() -> AppDatabase.getInstance(activity).tripDao().deleteTrip(lastTripDismiss));
        notifyItemRemoved(lastTripDismissPosition);
    }
}
