package com.cabalry.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetUserID;

/**
 * SilentAlarmService
 */
public class SilentAlarmService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "SilentAlarmService";
    private NotificationManager mNotificationManager;

    public SilentAlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (GetUserID(getApplicationContext()) != 0 && GetAlarmID(getApplicationContext()) != 0) {
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate two rounds of 600 millis and sleep 1000 in between.
            long[] pattern = {0, 100, 50, 100};

            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            v.vibrate(pattern, -1);

            // Start alarm.
            Intent alarmIntent = new Intent();
            alarmIntent.setAction("com.cabalry.action.ALARM_START");
            sendBroadcast(alarmIntent);
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        SilentAlarmReceiver.completeWakefulIntent(intent);
    }
}
