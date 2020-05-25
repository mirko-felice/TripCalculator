package com.example.tripcalculator.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.broadcastReceiver.ReminderReceiver;
import com.example.tripcalculator.databinding.PlanningFragmentBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class PlanningFragment extends DialogFragment {

    private boolean is24Format;
    private PlanningFragmentBinding binding;
    private Calendar timeToSet;
    private Calendar hourToSet = Calendar.getInstance();
    private Calendar dateToSet = Calendar.getInstance();
    private String tripName;
    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat dateFormatter;

    public PlanningFragment(String tripName) {
        this.tripName = tripName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PlanningFragmentBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    //TODO solo una pianfiicazione alla volta
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("TripName", tripName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), ReminderReceiver.NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        binding.toolbar.setTitle(R.string.plan_trip_title);
        binding.toolbar.inflateMenu(R.menu.plan);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            binding.date.getText();
            binding.clock.getText();
            if(alarmManager != null)
                alarmManager.setExact(AlarmManager.RTC, timeToSet.getTimeInMillis(), pendingIntent);
            dismiss();
            return true;
        });
        timeToSet = Calendar.getInstance();

        Locale locale = Locale.getDefault();
        is24Format = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("is24format", true);

        String pattern = is24Format ? "HH:mm" : "hh:mm a" ;

        timeFormatter = new SimpleDateFormat(pattern, locale);
        binding.clock.setText(timeFormatter.format(timeToSet.getTime()));

        dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", locale);
        binding.date.setText(dateFormatter.format(timeToSet.getTime()));

        binding.setTimeBtn.setOnClickListener(v -> showTimeDialog());

        binding.setDateBtn.setOnClickListener(v -> showDateDialog());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    private void showTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.TimePickerDialog, (view, hourOfDay, minute) -> {
            hourToSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            hourToSet.set(Calendar.MINUTE, minute);
            binding.clock.setText(timeFormatter.format(hourToSet.getTime()));
        }, hourToSet.get(Calendar.HOUR_OF_DAY), hourToSet.get(Calendar.MINUTE), is24Format);
        timePickerDialog.setOnCancelListener(dialog -> showSnack());
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog() {
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), R.style.Theme_MaterialComponents_DayNight_Dialog_Alert, (view, year, month, dayOfMonth) -> {
            dateToSet.set(Calendar.YEAR, year);
            dateToSet.set(Calendar.MONTH, month);
            dateToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.date.setText(dateFormatter.format(dateToSet.getTime()));
        }, dateToSet.get(Calendar.YEAR), dateToSet.get(Calendar.MONTH), dateToSet.get(Calendar.DAY_OF_MONTH));
        datePicker.setOnCancelListener(dialog -> showSnack());
        datePicker.getDatePicker().setMinDate(timeToSet.getTimeInMillis());
        datePicker.show();
    }

    private void showSnack() {
        Snackbar.make(binding.getRoot(), R.string.plan_cancel_message, BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
