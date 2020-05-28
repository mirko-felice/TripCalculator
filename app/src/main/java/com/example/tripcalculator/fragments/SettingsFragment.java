package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.preference, rootKey);
        ListPreference listPreference = getPreferenceManager().findPreference("language");
        assert listPreference != null;
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String oldValue = listPreference.getValue();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle(R.string.restart_needed_title)
                    .setMessage(R.string.restart_needed_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                        editor.putString("language", String.valueOf(newValue)).apply();
                        restart();
                    })
                    .setNegativeButton(R.string.discard, (dialog, which) -> listPreference.setValue(oldValue))
                    .setCancelable(false)
                    .show();
            return true;
        });

        SwitchPreferenceCompat switchPreferenceCompat = getPreferenceManager().findPreference("dark_theme");
        assert switchPreferenceCompat != null;
        switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean oldValue = switchPreferenceCompat.isChecked();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle(R.string.restart_needed_title)
                    .setMessage(R.string.restart_needed_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean("dark_theme", (Boolean) newValue).apply();
                        restart();
                    })
                    .setNegativeButton(R.string.discard, (dialog, which) -> switchPreferenceCompat.setChecked(oldValue))
                    .setCancelable(false)
                    .show();
           return true;
        });

        SwitchPreferenceCompat switchPreference = getPreferenceManager().findPreference("is24format");
        assert switchPreference != null;
        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean("is24format", (Boolean) newValue).apply();
            return true;
        });

    }

    private void restart(){
        Intent exitIntent = new Intent(requireContext(), MainActivity.class);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        exitIntent.putExtra("exit", true);
        startActivity(exitIntent);

        requireActivity().finish();
    }
}
