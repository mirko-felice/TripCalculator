package com.example.tripcalculator.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
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
            if(newValue.equals("0")) {
                Locale.setDefault(Locale.ITALIAN);
                //getPreferenceManager().getSharedPreferences().edit().putString("lang", "it").apply();
            } else if (newValue.equals("1")){
                Locale.setDefault(Locale.ENGLISH);
                //getPreferenceManager().getSharedPreferences().edit().putString("lang", "en").apply();
            }
            return true;
        });

        SwitchPreferenceCompat switchPreferenceCompat = getPreferenceManager().findPreference("dark_theme");
        assert switchPreferenceCompat != null;
        switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
            if((Boolean) newValue){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return true;
        });
    }
}
