package com.example.wearabletest.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.wearabletest.Activities.MainActivity;
import com.example.wearabletest.R;
import com.example.wearabletest.Util.AmpCalc;
import com.example.wearabletest.Util.FileUtil;
import com.example.wearabletest.Util.SoundLevelNotif;
import com.example.wearabletest.Util.mRecorder;

import java.io.File;

public class BackgroundAudioListener extends Service {

    public static final int ONGOING_NOTIFICATION_ID = 811;
    private int messageId = 812;
    private static final int refreshTime = 500;
    public static final String ONGOING_CHANNEL_ID = "AudioBuddyOngoingListener";
    private Handler mediaHandler;
    private mRecorder myRecorder;
    private boolean running;
    private float volume = 10000;
    private File file;
    private Context bgAudioListenerContext;
    private static final int DB_THRESHOLD = 70;

    @Override
    public IBinder onBind(Intent i){
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startID) {
        createNotificationChannel();
        startForeground(ONGOING_NOTIFICATION_ID, getOngoingNotification());
        Log.d(ONGOING_CHANNEL_ID, "in onStartCommand");

        myRecorder = new mRecorder();

        mediaHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                boolean terminate = msg.getData().getBoolean("terminate");
                if (this.hasMessages(messageId)){
                    if (terminate){
                        running = false;
                    }
                }
                volume = myRecorder.getMaxAmplitude();
                if (volume > 0 && volume < 10000) {
                    AmpCalc.setDbCount(20 * (float)Math.log10(volume));
                    int db = (int) AmpCalc.dbCount;
                    processDb(db);
                }
                if (terminate){
                    running = false;
                }
                else{
                    if (running){
                        mediaHandler.sendMessageDelayed(getNonTerminateMessage(), refreshTime);
                    }
                }
            }
        };

        bgAudioListenerContext = this;
        running = true;
        mediaHandler.sendMessage(getNonTerminateMessage());
        setupFile();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.d("ServiceTag", "onDestroy entered");
        mediaHandler.sendMessageAtFrontOfQueue(getTerminateMessage());
        super.onDestroy();
    }

    private void setupFile() {
        file = FileUtil.createFile(getExternalCacheDir().getAbsolutePath()+"/bg.amr");
        if (file != null){
            Toast.makeText(bgAudioListenerContext, "Created file", Toast.LENGTH_SHORT).show();
            startRecord();
        }
        else{
            Toast.makeText(bgAudioListenerContext, "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecord() {
        if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.v("ServiceTag", "Permissions checked and granted");
            myRecorder.setRecorderFile(file);
            if (myRecorder.startRecorder()) {
                //starts handler loop
                Log.v("ServiceTag", "Sending handler message");
                mediaHandler.sendMessage(getNonTerminateMessage());
            }
            else {
                Log.v("ServiceTag", "Failed to start recording");
            }
        }
        else {
            Toast.makeText(bgAudioListenerContext, "Audio recording or file writing permissions not granted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Message getNonTerminateMessage(){
        Message newMessage = Message.obtain();
        Bundle bd = new Bundle();
        bd.putBoolean("terminate", false);
        newMessage.what = messageId;
        newMessage.setData(bd);
        return newMessage;
    }
    private Message getTerminateMessage(){
        Message newMessage = Message.obtain();
        Bundle bd = new Bundle();
        bd.putBoolean("terminate", true);
        newMessage.what = messageId;
        newMessage.setData(bd);
        return newMessage;
    }

    public Notification getOngoingNotification() {
        Intent notifIntent = new Intent (this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
        Notification nf = new Notification.Builder( this, ONGOING_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_ongoing_title))
            .setContentText(getString(R.string.notification_ongoing_description))
            .setSmallIcon(R.drawable.audio_buddy_head)
            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.audio_buddy_head))
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

    private void processDb(int db) {
        if (db > DB_THRESHOLD){
            SoundLevelNotif.issueNotification(this);
        }
    }
}
