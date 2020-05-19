package com.example.tripcalculator.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.ActivityMainBinding;
import com.example.tripcalculator.ui.viewpager.TripViewPagerAdapter;

import java.lang.reflect.Array;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme);
        //TODO ogni activity dovrà fare questo on resume
        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", true) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(PreferenceManager.getDefaultSharedPreferences(this).getString("language", "0").equals("0")? Locale.ITALIAN : Locale.ENGLISH);
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        binding.viewPager.setAdapter(new TripViewPagerAdapter(getSupportFragmentManager(), this));
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return false;
    }
}
