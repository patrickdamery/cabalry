package com.cabalry.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Timer;
import java.util.TimerTask;

import static com.cabalry.util.PreferencesUtil.*;

/**
 * SilentAlarmReceiver
 */
public class SilentAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "SilentAlarmReceiver";

    private static boolean lock = false;
    private static int count = 0;
    private static TimerTask taskReset;
    private static TimerTask taskLock;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (GetAlarmID(context) != 0 || !IsSilent(context) || lock) return;
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

        if (taskReset != null) {
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
        timerLock.schedule(taskLock, 100);
    }

    private void startSilentAlarm(Context context) {

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, new Intent(context, SilentAlarmService.class));
    }
}