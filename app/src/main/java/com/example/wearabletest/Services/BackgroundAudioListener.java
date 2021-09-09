package com.example.wearabletest.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.wearabletest.Activities.MainActivity;
import com.example.wearabletest.R;

public class BackgroundAudioListener extends Service {

    public static final int ONGOING_NOTIFICATION_ID = 811;
    public static final String ONGOING_CHANNEL_ID = "AudioBuddyOngoingListener";

    @Override
    public IBinder onBind(Intent i){
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startID) {
        createNotificationChannel();
        startForeground(ONGOING_NOTIFICATION_ID, getOngoingNotification());
        Log.d(ONGOING_CHANNEL_ID, "in onStartCommand");
        return START_NOT_STICKY;
    }

    public Notification getOngoingNotification() {
        Intent notifIntent = new Intent (this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
        Notification nf = new Notification.Builder( this, ONGOING_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_ongoing_title))
            .setContentText(getString(R.string.notification_ongoing_description))
            .setContentIntent(pIntent)
            .build();
        return nf;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_ongoing_name);
            String description = getString(R.string.channel_ongoing_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ONGOING_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager nM = getSystemService(NotificationManager.class);
            nM.createNotificationChannel(channel);
        }
    }
}
