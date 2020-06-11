package com.example.tripcalculator.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.tripcalculator.R;
import com.example.tripcalculator.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();
    }
}
