package com.cabalry.alarm;

import android.app.IntentService;
import android.content.Intent;

import com.cabalry.app.TimerCheckActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.cabalry.util.PreferencesUtil.GetTimerTime;
import static com.cabalry.util.PreferencesUtil.SetTimerEnabled;

/**
 * AlarmTimerService
 */
public class AlarmTimerService extends IntentService {
    private static final String TAG = "AlarmTimerService";

    private static TimerTask timerTask = null;

    public AlarmTimerService() {
        super(TAG);
    }

    public static void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int timerTime = GetTimerTime(getApplicationContext()) * 1000;

        timerTask = new TimerTask() {
            @Override
            public void run() {
                SetTimerEnabled(getApplicationContext(), false);

                Intent timer = new Intent(getApplicationContext(), TimerCheckActivity.class);
                timer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(timer);

                timerTask = null;
                stopSelf();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, timerTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}