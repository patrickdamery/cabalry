package com.cabalry.audio;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cabalry.base.RunnableService;

import static com.cabalry.util.PreferencesUtil.GetAlarmIP;

/**
 * AudioStreamService
 */
public class AudioStreamService extends RunnableService {
    private static final String TAG = "AudioStreamService";

    private static AudioStreamer mAudioStreamer;

    public static void stopAudioStream() {
        if (mAudioStreamer != null) {
            mAudioStreamer.stopStream();
            mAudioStreamer = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mAudioStreamer = new AudioStreamer();

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioStreamer.startStream(GetAlarmIP(AudioStreamService.this));
            }
        });

        streamThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        stopAudioStream();
    }
}
