package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.tripcalculator.R;
import com.example.tripcalculator.databinding.ActivityMainBinding;
import com.example.tripcalculator.ui.viewpager.TripViewPagerAdapter;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.viewPager.setAdapter(new TripViewPagerAdapter(getSupportFragmentManager(), this));
        setContentView(binding.getRoot());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("exit", false)){
            finish();
            SplashActivity.isFirstStart = true;
            startActivity(new Intent(this, SplashActivity.class));
        }
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
