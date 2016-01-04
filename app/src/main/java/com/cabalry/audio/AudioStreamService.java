package com.cabalry.audio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.cabalry.util.PreferencesUtil.*;

/**
 * Created by conor on 01/02/15.
 */
public class AudioStreamService extends Service {

    private static AudioStreamer mAudioStreamer;

    @Override
    public void onCreate() {
        mAudioStreamer = new AudioStreamer();

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioStreamer.startStream(GetUserIP(AudioStreamService.this));
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
