package com.example.tripcalculator.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.MainActivity;
import com.example.tripcalculator.activities.TripActivity;
import com.example.tripcalculator.database.Location;
import com.example.tripcalculator.viewmodel.LocationViewModel;

public class Utilities {

    private static final int NOTIFICATION_REQUEST_CODE = 200;
    private static final String CHANNEL_ID = "dest";
    private static final String CHANNEL_NAME = "Arrivo Destinazione";

    public static void hideKeyboard(AppCompatActivity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showKeyboard(AppCompatActivity activity, View view){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

   public static void swapLocations(AppCompatActivity activity, Location firstLocation, Location secondLocation){
        int tempPosition = firstLocation.Order;
        firstLocation.Order = secondLocation.Order;
        secondLocation.Order = tempPosition;
        LocationViewModel locationViewModel = new ViewModelProvider(activity).get(LocationViewModel.class);
        locationViewModel.updateLocation(firstLocation);
        locationViewModel.updateLocation(secondLocation);
    }
    
    public static void createLocationNotification(Context context, Location location){
        Intent notificationIntent = new Intent(context, TripActivity.class);
        notificationIntent.putExtra("TripId", location.TripId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        String message = location.Reminder == null || location.Reminder.isEmpty() ? context.getString(R.string.empty_reminder) : context.getString(R.string.reminder_message, location.Reminder);

        Notification n = builder.setAutoCancel(true)
                .setContentTitle(context.getString(R.string.arrival_title, location.DisplayName))
                .setTicker(context.getString(R.string.ticker_reminder, location.DisplayName, message))
                .setSmallIcon(R.mipmap.ic_app_round)
                .setLargeIcon(null)
                .setShowWhen(true)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(pendingIntent).build();

        if (notificationManager != null) {
            notificationManager.notify(2, n);
        }
    }
}
