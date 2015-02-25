package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.cabalry.R;
import com.cabalry.utils.Preferences;

/**
 * Created by conor on 24/02/15.
 */
public class AlarmSoundService extends Service {
    MediaPlayer mMediaPlayer = null;

    @Override
    public void onCreate() {
        Preferences.initialize(getApplicationContext());
        if(Preferences.getAlarmId() == 0) return;

        mMediaPlayer = MediaPlayer.create(this, R.raw.fx_alarm);
        mMediaPlayer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
