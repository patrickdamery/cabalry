package com.cabalry.alarm;

import android.content.Intent;
import android.util.Log;

import com.cabalry.app.TimerCheckActivity;
import com.cabalry.base.BindableService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimerAlarmService
 */
public class TimerAlarmService extends BindableService {
    private static final String TAG = "TimerAlarmService";

    private static TimerTask taskCheck;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        taskCheck = new TimerTask() {
            @Override
            public void run() {
                // Open check activity
                Intent check = new Intent(getApplicationContext(), TimerCheckActivity.class);
                check.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(check);
                taskCheck = null;

                stopSelf();
            }
        };

        Timer timerCheck = new Timer();
        timerCheck.schedule(taskCheck, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (taskCheck != null) {
            taskCheck.cancel();
            taskCheck = null;
        }
    }
}