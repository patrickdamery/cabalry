package com.cabalry.audio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.cabalry.util.PrefsUtil.*;
import static com.cabalry.util.TasksUtil.*;

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

        audioPlaybackProgram = new AudioPlaybackProgram();

        UpdateListenerInfoTask updateListenerInfoTask = new UpdateListenerInfoTask();
        updateListenerInfoTask.setListenerInfo(GetUserID(this), GetUserKey(this), GetAlarmID(this), 50000);

        Thread playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioPlaybackProgram.startPlayback(GetUserIP(AudioPlaybackService.this), 2048);
            }
        });

        playbackThread.start();
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