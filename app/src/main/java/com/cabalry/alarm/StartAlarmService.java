package com.cabalry.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cabalry.app.AlarmMapActivity;
import com.cabalry.util.TasksUtil;

/**
 * StartAlarmService
 */
public class StartAlarmService extends Service {
    private static final String TAG = "StartAlarmService";

    @Override
    public void onCreate() {
        super.onCreate();

        launchAlarm();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void launchAlarm() {
        new TasksUtil.StartAlarmTask(getApplicationContext()) {
            @Override
            protected void onResult(Boolean result) {
                if (result) {
                    Intent alarm = new Intent(getApplicationContext(), AlarmMapActivity.class);
                    alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(alarm);
                }
            }
        }.execute();
    }
}