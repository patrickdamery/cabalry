package com.cabalry.audio;

import android.content.Intent;
import android.os.IBinder;

import com.cabalry.base.RunnableService;

import static com.cabalry.util.PreferencesUtil.*;

/**
 * Created by conor on 01/02/15.
 */
public class AudioStreamService extends RunnableService {

    private static AudioStreamer mAudioStreamer;

    @Override
    public void onCreate() {
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
        if(mAudioStreamer != null) {
            mAudioStreamer.stopStream();
            mAudioStreamer = null;
        }
    }
}
