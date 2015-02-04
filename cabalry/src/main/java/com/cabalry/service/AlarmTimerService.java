package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmTimerService extends Service {

    public Timer timer = null;

    @Override
    public void onCreate() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), AlarmService.class));
            }
        }, 0, 3000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}