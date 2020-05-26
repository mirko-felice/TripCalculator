package com.example.tripcalculator.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tripcalculator.R;
import com.example.tripcalculator.databinding.ActivitySplashBinding;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_INTERNET_PERMISSION = 1;
    public static boolean isFirstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme);
        if(isFirstStart){
            isFirstStart = false;
            setImmersive(true);
            setContentView(R.layout.activity_splash);
            ((ImageView)findViewById(R.id.splash_image)).setImageResource(R.drawable.ic_map);
            if(checkPermissions())
                new Handler().postDelayed(this::start, 2000);
        } else {
            start();
        }
    }

     @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if (requestCode == REQUEST_INTERNET_PERMISSION) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                 Snackbar.make(findViewById(R.id.snackbar_layout), "Impossibile utilizzare la mappa senza l'accesso ad internet!", Snackbar.LENGTH_LONG).show();
             }
         }
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
            return false;
        }
        return true;
    }

    private void start(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
