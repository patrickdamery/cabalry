package com.cabalry.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import com.cabalry.activity.TimerCheckActivity;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmTimerService extends IntentService {

    private static TimerTask timerTask = null;

    public AlarmTimerService() {
        super("AlarmTimerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Preferences.initialize(getApplicationContext());
        int timerTime = Preferences.getInt(GlobalKeys.TIMER) * 1000;

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Preferences.setBoolean(GlobalKeys.TIMER_ENABLED, false);

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

    public static void stopTimer() {
        timerTask.cancel();
    }
}