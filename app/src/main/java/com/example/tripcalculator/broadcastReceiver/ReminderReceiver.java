package com.example.tripcalculator.broadcastReceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;

import com.example.tripcalculator.R;
import com.example.tripcalculator.activities.MainActivity;

import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "plan";
    private static final CharSequence CHANNEL_NAME = "Pianificazione";
    public static final int NOTIFICATION_REQUEST_CODE = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(PreferenceManager.getDefaultSharedPreferences(context).getString("language", "0").equals("0") ? Locale.ITALIAN : Locale.ENGLISH);
        context = context.createConfigurationContext(configuration);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notificationIntent);

        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("TripId", intent.getIntExtra("TripId", -1)).apply();
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        String name = intent.getStringExtra("TripName");

        String title = context.getString(R.string.plan_trip_not_title);
        String message = context.getString(R.string.plan_trip_not_message, name);

        Notification n = builder.setAutoCancel(true)
                .setContentTitle(title)
                .setTicker(title + "\n" + message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(null)
                .setShowWhen(true)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent).build();

        if (notificationManager != null) {
            notificationManager.notify(1, n);
        }
    }
}
