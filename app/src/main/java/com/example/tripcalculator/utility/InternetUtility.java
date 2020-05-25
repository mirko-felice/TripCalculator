package com.example.tripcalculator.utility;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class InternetUtility {

    private static boolean networkConnected = false;

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
    }

    private static ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            networkConnected = true;
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            networkConnected = false;
        }
    };

    public static boolean isNetworkConnected(){
        return networkConnected;
    }

    public static void unregisterNetworkCallback(AppCompatActivity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public static void setNetSettingsIntent(AppCompatActivity activity) {
        Intent intent = new Intent();
        intent.setAction((Settings.ACTION_WIRELESS_SETTINGS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivity(intent);
        }
    }
}
