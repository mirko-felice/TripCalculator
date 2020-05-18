package com.example.tripcalculator.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.ActivityMainBinding;
import com.example.tripcalculator.ui.adapters.TripViewPagerAdapter;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme);
        //TODO ogni activity dovrà fare questo on resume
        AppCompatDelegate.setDefaultNightMode(getPreferences(MODE_PRIVATE).getBoolean("dark_theme", true) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        com.example.tripcalculator.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ViewPager viewPager;
        viewPager = binding.viewPager;
        TripViewPagerAdapter adapter = new TripViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(this).close();
    }
}
