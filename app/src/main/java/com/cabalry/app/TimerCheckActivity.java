package com.cabalry.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.cabalry.alarm.AlarmTimerService;
import com.cabalry.alarm.StartAlarmService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 18/02/15.
 */
public class TimerCheckActivity extends Activity {
    private static final String TAG = "TimerCheckActivity";

    private static final int TIME = 30000;
    private AlertDialog alert;
    private TimerTask timerTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                alert.cancel();
                // Start alarm.
                startService(new Intent(getApplicationContext(), StartAlarmService.class));
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, TIME);

        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Time is up! Are you ok?");

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startService(new Intent(getApplicationContext(), AlarmTimerService.class));
                //SetTimerEnabled(getApplicationContext(), true);
                timerTask.cancel();
                onBackPressed();
            }

        });

        builder.setNegativeButton("Alarm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SetTimerEnabled(getApplicationContext(), false);
                timerTask.cancel();
                //launchAlarm();
                //startService(new Intent(getApplicationContext(), StartAlarmService.class));
            }
        });

        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}