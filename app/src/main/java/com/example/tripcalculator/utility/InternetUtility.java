package com.example.tripcalculator.utility;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tripcalculator.R;
import com.example.tripcalculator.fragments.MapFragment;
import com.google.android.material.snackbar.Snackbar;

public class InternetUtility {

    private static boolean networkConnected = false;
    private static Snackbar snackbar;
    private static MapFragment mapFragment;

    private InternetUtility(){}

    public static void registerNetworkCallback(AppCompatActivity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService((Context.CONNECTIVITY_SERVICE));

        if (connectivityManager != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                networkConnected = networkInfo != null && networkInfo.isConnected();
            }
        } else {
            networkConnected = false;
        }
        if (mapFragment != null)
            mapFragment.requireActivity().runOnUiThread(() -> mapFragment.setConnectionStatus(networkConnected));
    }

    private static ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            networkConnected = true;
            if (mapFragment != null)
                mapFragment.requireActivity().runOnUiThread(() -> mapFragment.setConnectionStatus(true));
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            networkConnected = false;
            if (mapFragment != null)
                mapFragment.requireActivity().runOnUiThread(() -> mapFragment.setConnectionStatus(false));
        }
    };

    public static void initSnackBar(AppCompatActivity activity, View viewHolder){
        snackbar = Snackbar.make(viewHolder, activity.getString(R.string.no_connection), Snackbar.LENGTH_LONG)
                            .setAction(R.string.settings, v -> setNetSettingsIntent(activity));
    }

    public static void showSnackbar(){
        snackbar.show();
    }

    public static boolean isNetworkConnected(){
        return networkConnected;
    }

    public static void unregisterNetworkCallback(AppCompatActivity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private static void setNetSettingsIntent(AppCompatActivity activity) {
        Intent intent = new Intent();
        intent.setAction((Settings.ACTION_WIRELESS_SETTINGS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivity(intent);
        }
    }

    public static void setMapFragment(MapFragment fragment){
        mapFragment = fragment;
    }
}
