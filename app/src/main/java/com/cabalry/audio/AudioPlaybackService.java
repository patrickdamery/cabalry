package com.cabalry.audio;

import android.content.Intent;
import android.os.IBinder;

import com.cabalry.base.RunnableService;

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.TasksUtil.*;

/**
 * AudioPlaybackService
 */
public class AudioPlaybackService extends RunnableService {

    private static AudioPlayer audioPlayer;
    private static Thread playbackThread;

    @Override
    public void onCreate() {
        super.onCreate();
        audioPlayer = new AudioPlayer();

        playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioPlayer.startPlayback(GetAlarmIP(AudioPlaybackService.this));
            }
        });

        UpdateListenerInfoTask updateListenerInfoTask = new UpdateListenerInfoTask();
        updateListenerInfoTask.setListenerInfo(GetUserID(this), GetUserKey(this), GetAlarmID(this), 50000);
        updateListenerInfoTask.execute();

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
        super.onDestroy();
        stopAudioPlayback();
    }

    public static void stopAudioPlayback() {
        if (isRunning()) {
            if (audioPlayer != null)
                audioPlayer.stopPlayback();

            if (playbackThread != null)
                playbackThread.interrupt();
        }
    }
}