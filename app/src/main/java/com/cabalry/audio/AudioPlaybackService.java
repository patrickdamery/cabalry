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
                audioPlayer.startPlayback(GetUserIP(AudioPlaybackService.this));
            }
        });
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

    public static void startAudioPlayer(int userID, String key, int alarmID) {
        if (isRunning()) {
            UpdateListenerInfoTask updateListenerInfoTask = new UpdateListenerInfoTask();
            updateListenerInfoTask.setListenerInfo(userID, key, alarmID, 50000);

            playbackThread.start();
        }
    }

    public static void stopAudioPlayback() {
        if (isRunning()) {
            if (audioPlayer != null) {
                audioPlayer.stopPlayback();
                audioPlayer = null;
            }

            playbackThread.interrupt();
        }
    }
}