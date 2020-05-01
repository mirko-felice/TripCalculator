package com.example.tripcalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.adapters.TripViewPagerAdapter;
import com.example.tripcalculator.database.AppDatabase;
import com.example.tripcalculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewPager = binding.viewPager;
        viewPager.setAdapter(new TripViewPagerAdapter(getSupportFragmentManager()));

        ((Button)findViewById(R.id.button3)).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(this).close();
    }
}
