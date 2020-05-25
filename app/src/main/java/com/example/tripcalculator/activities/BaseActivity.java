package com.example.tripcalculator.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", true);
        AppCompatDelegate.setDefaultNightMode(isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        if (!isDarkTheme){
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        overrideConfiguration.setLocale(PreferenceManager.getDefaultSharedPreferences(this).getString("language", "0").equals("0") ? Locale.ITALIAN : Locale.ENGLISH);
        return super.createConfigurationContext(overrideConfiguration);
    }
}
