package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.cabalry.custom.AudioStreamProgram;
import com.cabalry.utils.Preferences;

/**
 * Created by conor on 01/02/15.
 */
public class AudioStreamService extends Service {

    private static AudioStreamProgram audioStreamProgram;

    @Override
    public void onCreate() {

        // Initialize preferences.
        Preferences.initialize(getApplicationContext());

        audioStreamProgram = new AudioStreamProgram();

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioStreamProgram.startStream();
            }
        });

        streamThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        stopAudioStream();
    }

    public static void stopAudioStream() {
        if(audioStreamProgram != null) {
            audioStreamProgram.stopStream();
            audioStreamProgram = null;
        }
    }
}
