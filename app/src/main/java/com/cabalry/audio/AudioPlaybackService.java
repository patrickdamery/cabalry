package com.cabalry.audio;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cabalry.base.RunnableService;

import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetAlarmIP;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;
import static com.cabalry.util.TasksUtil.UpdateListenerInfoTask;

/**
 * AudioPlaybackService
 */
public class AudioPlaybackService extends RunnableService {
    private static final String TAG = "AudioPlaybackService";

    private static AudioPlayer audioPlayer;
    private static Thread playbackThread;

    public static void stopAudioPlayback() {
        if (isRunning()) {
            if (audioPlayer != null)
                audioPlayer.stopPlayback();

            if (playbackThread != null)
                playbackThread.interrupt();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

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
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        stopAudioPlayback();
    }
}