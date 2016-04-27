package com.cabalry.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.cabalry.alarm.AlarmTimerService;
import com.cabalry.base.CabalryActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 18/02/15.
 */
public class TimerCheckActivity extends CabalryActivity {
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
                Intent alarmIntent = new Intent();
                alarmIntent.setAction("com.cabalry.action.ALARM_START");
                sendBroadcast(alarmIntent);
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

                // Start alarm.
                Intent alarmIntent = new Intent();
                alarmIntent.setAction("com.cabalry.action.ALARM_START");
                sendBroadcast(alarmIntent);
            }
        });

        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}