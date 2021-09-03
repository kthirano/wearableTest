package com.example.wearabletest.Util;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class mRecorder {

    public File recFile;
    private MediaRecorder mediaRecorder;
    public boolean isRecording = false;

    public float getMaxAmplitude() {
        if (mediaRecorder != null) {
            try {
                return mediaRecorder.getMaxAmplitude();
            } catch (IllegalArgumentException e){
                Log.d("MRECORDER", e.getMessage());
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public void setRecorderFile(File file) {recFile = file;}

    public boolean startRecorder() {
        if (recFile == null) {
            return false;
        }
        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(recFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            isRecording = false;
            Log.d("MRECORDER", e.getMessage());
        } catch (IllegalStateException e) {
            stopRecording();
            isRecording = false;
            Log.d("MRECORDER", e.getMessage());
        }
        return isRecording;
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            if (isRecording) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    Log.d("MRECORDER", e.getMessage());
                }
            }
            mediaRecorder = null;
            isRecording = false;
        }
    }

    public void delete() {
        stopRecording();
        if (mediaRecorder != null){
            recFile.delete();
            recFile = null;
        }
    }
}
