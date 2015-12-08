package com.cabalry.audio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.cabalry.util.PreferencesUtil.*;

/**
 * Created by conor on 01/02/15.
 */
public class AudioStreamService extends Service {

    private static AudioStreamProgram mAudioStreamProgram;

    @Override
    public void onCreate() {
        mAudioStreamProgram = new AudioStreamProgram();

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioStreamProgram.startStream(GetUserIP(AudioStreamService.this));
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
        if(mAudioStreamProgram != null) {
            mAudioStreamProgram.stopStream();
            mAudioStreamProgram = null;
        }
    }
}
