package com.example.wearabletest.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.wearabletest.R;

public class SoundLevelNotif {
    private static String CHANNEL_ID = "AudioBuddySoundThresholdNotification";
    //arbitrary notification id. could be any value.
    private static int NOTIFICATION_ID = 810;

    /**
     * Issues a notification
     * @param c Context where this method is being called. get it from within an activity with
     *          view.getContext.
     */
    public static void issueNotification(Context c) {
        createNotificationChannel(c);
        //creating the content of the notification
        //String values located in res/values/strings.xml
        //drawable value located in res/drawable
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, CHANNEL_ID)
                .setSmallIcon(R.drawable.audio_buddy_head)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(),R.drawable.audio_buddy_head))
                .setContentTitle(c.getString(R.string.notification_title))
                .setContentText(c.getString(R.string.notification_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Creates a notification channel for API 26+
     * @param c Context where this method is being called. get it from within an activity with
     *          view.getContext.
     */
    private static void createNotificationChannel(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //R string values located in res/values/strings.xml
            CharSequence name = c.getString(R.string.channel_name);
            String description = c.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //Registering the channel with Android
            NotificationManager notificationManager = c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
