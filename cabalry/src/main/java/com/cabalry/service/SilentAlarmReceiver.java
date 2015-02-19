package com.cabalry.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class SilentAlarmReceiver extends WakefulBroadcastReceiver {

    private static boolean lock = false;
    private static int count = 0;
    private static TimerTask taskReset;
    private static TimerTask taskLock;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Preferences.initialize(context);
        if(!Preferences.getBoolean(GlobalKeys.SILENT) || lock) return;
        lock = true;
        count++;

        if (count > 5) {
            count = 0;
            taskReset.cancel();
            taskLock.cancel();
            count = 0;
            lock = false;

            startSilentAlarm(context);

            return;
        }

        if(taskReset != null) {
            taskReset.cancel();
        }

        taskReset = new TimerTask() {
            @Override
            public void run() {
                count = 0;
            }
        };

        taskLock = new TimerTask() {
            @Override
            public void run() {
                lock = false;
            }
        };

        Timer timerReset = new Timer();
        timerReset.schedule(taskReset, 5000);

        Timer timerLock = new Timer();
        timerLock.schedule(taskLock, 500);
    }

    private void startSilentAlarm(Context context) {

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, new Intent(context, SilentAlarmService.class));
    }
}