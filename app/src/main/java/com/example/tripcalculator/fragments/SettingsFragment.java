package com.example.tripcalculator.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.tripcalculator.R;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        ListPreference listPreference = getPreferenceManager().findPreference("language");
        assert listPreference != null;
        //listPreference.setIcon(R.drawable.ic_language_light);
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if(newValue.equals("Italiano")) {
                Locale.setDefault(Locale.ITALY);
            } else if (newValue.equals("English")){
                Locale.setDefault(Locale.ENGLISH);
            }
            return true;
        });

        SwitchPreferenceCompat switchPreferenceCompat = getPreferenceManager().findPreference("theme");
        assert switchPreferenceCompat != null;
        switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
            if((Boolean) newValue){
                requireActivity().getApplication().setTheme(R.style.AppTheme);
            } else {
                requireActivity().getApplication().setTheme(0);
            }
            return true;
        });
    }
}
