package com.cabalry.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.IsSilent;

/**
 * SilentAlarmReceiver
 */
public class SilentAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "SilentAlarmReceiver";

    private static int count = 0;
    private static TimerTask taskReset;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, "count: " + count);
        if (GetAlarmID(context) == 0 && IsSilent(context)) {
            count++;

            if (count > 10) {
                taskReset.cancel();
                count = 0;

                Log.i(TAG, "startSilentAlarm");
                startAlarm(context);
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

            Timer timerReset = new Timer();
            timerReset.schedule(taskReset, 5000);
        }
    }

    private void startAlarm(Context context) {
        if (GetUserID(context) != 0) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate two rounds of 600 millis and sleep 1000 in between.
            long[] pattern = {0, 100, 50, 100};

            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            v.vibrate(pattern, -1);

            // Start alarm.
            Intent alarmIntent = new Intent();
            alarmIntent.setAction("com.cabalry.action.ALARM_START");
            context.sendBroadcast(alarmIntent);
        }
    }
}