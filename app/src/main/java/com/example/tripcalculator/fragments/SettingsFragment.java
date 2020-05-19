package com.example.tripcalculator.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.MainActivity;

import java.util.Locale;
import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        ListPreference listPreference = getPreferenceManager().findPreference("language");
        assert listPreference != null;
        //listPreference.setIcon(R.drawable.ic_language_light);
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
            editor.putString("language", String.valueOf(newValue)).apply();
            restart();
            return true;
        });

        SwitchPreferenceCompat switchPreferenceCompat = getPreferenceManager().findPreference("dark_theme");
        assert switchPreferenceCompat != null;
        switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean("dark_theme", (Boolean) newValue).apply();
            restart();
            return true;
        });
    }

    private void restart(){
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
        Runtime.getRuntime().exit(0);
    }
}
