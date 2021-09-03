package com.example.wearabletest.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wearabletest.R;
import com.example.wearabletest.Util.AmpCalc;
import com.example.wearabletest.Util.FileUtil;
import com.example.wearabletest.Util.mRecorder;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mDbText;
    private ProgressBar mDbProgress;
    private final String[] filePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String[] audioPermission = {Manifest.permission.RECORD_AUDIO};
    private File audioFile;
    private mRecorder myRecorder;
    private Handler handler;
    private float volume;

    private static final int REQUEST_EXTERNAL_FILE = 610;
    private static final int REQUEST_AUDIO_REC = 200;
    private static final int MSGWHAT = 0x1001;
    private static final int REFRESHTIME = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbText = (TextView) findViewById(R.id.text_db);
        mDbProgress = (ProgressBar) findViewById(R.id.progressBar);
        myRecorder = new mRecorder();
        // Enables Always-on
        //setAmbientEnabled();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (this.hasMessages(MSGWHAT)) {
                    return;
                }
                volume = myRecorder.getMaxAmplitude();
                if (volume > 0 && volume < 10000000){
                    AmpCalc.setDbCount(20 * (float)(Math.log10(volume)));
                    int db = (int) AmpCalc.dbCount;
                    mDbProgress.setProgress(db);
                    mDbText.setText(Integer.toString(db));
                }

                handler.sendEmptyMessageDelayed(MSGWHAT, REFRESHTIME);
            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (checkSelfPermission(filePermission[0]) == PackageManager.PERMISSION_GRANTED) {
            onFilePermissionGranted();
        }
        else {
            requestPermissions(filePermission, REQUEST_EXTERNAL_FILE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            switch(requestCode){
                case REQUEST_EXTERNAL_FILE:
                    onFilePermissionGranted();
                    return;
                case REQUEST_AUDIO_REC:
                    startRecord();
                    return;
            }
        }
        else{
            mDbText.setText("--");
            mDbProgress.setProgress(0);
        }
        return;
    }

    private void onFilePermissionGranted(){
        audioFile = FileUtil.createFile(getExternalCacheDir().getAbsolutePath()+"/temp.amr");
        if (audioFile != null){
            startRecord();
        }
    }

    private void startRecord(){
        if (checkSelfPermission(audioPermission[0]) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "starting record", Toast.LENGTH_SHORT).show();
            myRecorder.setRecorderFile(audioFile);
            if (myRecorder.startRecorder()) {
                handler.sendEmptyMessage(MSGWHAT);
            }
            else {
                Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            requestPermissions(audioPermission, REQUEST_AUDIO_REC);
        }
    }
}