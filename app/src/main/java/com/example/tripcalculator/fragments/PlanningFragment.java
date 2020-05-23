package com.example.tripcalculator.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.databinding.PlanningFragmentBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class PlanningFragment extends DialogFragment {

    private boolean is24Format;
    private Calendar now;
    private PlanningFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PlanningFragmentBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setNavigationOnClickListener(v -> dismiss());
        binding.toolbar.setTitle("Pianificazione Viaggio");
        binding.toolbar.inflateMenu(R.menu.plan);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            dismiss();
            return true;
        });
        now = Calendar.getInstance();

        is24Format = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("is24format", true);

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        binding.clock.setText(timeFormatter.format(now.getTime()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
        binding.date.setText(dateFormatter.format(now.getTime()));

        binding.setTimeBtn.setOnClickListener(v -> showTimeDialog());

        binding.setDateBtn.setOnClickListener(v -> showDateDialog());

        //AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
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
            binding.clock.setText(String.format(Locale.getDefault(), "%d : %d", hourOfDay, minute));

            //alarmManager.setExact(AlarmManager.RTC, timeToSet.getTimeInMillis(), pendingIntent);
            //TODO aggiornare il tempo iniziale
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24Format);
        timePickerDialog.setOnCancelListener(dialog -> showSnack());
        timePickerDialog.show();
    }

    private void showDateDialog() {
        CalendarConstraints calendarConstraints = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(calendarConstraints).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(selection);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault());
            binding.date.setText(dateFormatter.format(instance.getTime()));
        });
        datePicker.addOnNegativeButtonClickListener(v -> showSnack());
        datePicker.addOnCancelListener(dialog -> showSnack());
        datePicker.show(requireActivity().getSupportFragmentManager(), datePicker.toString());
    }

    private void showSnack() {
        Snackbar.make(binding.getRoot(), "Valore non impostato", BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
