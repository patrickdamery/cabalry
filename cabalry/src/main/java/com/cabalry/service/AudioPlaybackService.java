package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.cabalry.custom.AudioPlaybackProgram;
import com.cabalry.db.DB;
import com.cabalry.utils.Preferences;
import org.json.JSONObject;

/**
 * Created by conor on 01/02/15.
 */
public class AudioPlaybackService extends Service {

    private static AudioPlaybackProgram audioPlaybackProgram;
    private boolean running = false;

    @Override
    public void onCreate() {
        if(running) return;
            running = true;

        // Initialize preferences.
        Preferences.initialize(getApplicationContext());

        audioPlaybackProgram = new AudioPlaybackProgram();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                JSONObject result = DB.updateListenerInfo(
                        Preferences.getAlarmId(), Preferences.getID(),
                        Preferences.getKey(), 50000);

                audioPlaybackProgram.startPlayback(2048, 50000);
                return null;
            }
        }.execute();
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
        stopAudioPlayback();
        running = false;
    }

    public static void stopAudioPlayback() {
        if(audioPlaybackProgram != null) {
            audioPlaybackProgram.stopPlayback();
            audioPlaybackProgram = null;
        }
    }
}