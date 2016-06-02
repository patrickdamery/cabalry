package com.cabalry.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.cabalry.alarm.TimerAlarmService;
import com.cabalry.base.CabalryActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.cabalry.util.PreferencesUtil.SetTimerEnabled;

/**
 * TimerCheckActivity
 */
public class TimerCheckActivity extends CabalryActivity {
    private static final String TAG = "TimerCheckActivity";

    private static final int TIME = 30000;
    private AlertDialog alertDialog;
    private TimerTask timerTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                alertDialog.cancel();

                SetTimerEnabled(getApplicationContext(), false);
                stopService(new Intent(getApplicationContext(), TimerAlarmService.class));

                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate two rounds of 600 millis and sleep 1000 in between.
                long[] pattern = {0, 100, 50, 100};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                v.vibrate(pattern, -1);

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
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Timer Check");
        //alertDialog.setMessage("");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Alert", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SetTimerEnabled(getApplicationContext(), false);
                stopService(new Intent(getApplicationContext(), TimerAlarmService.class));

                // Start alarm.
                Intent alarmIntent = new Intent();
                alarmIntent.setAction("com.cabalry.action.ALARM_START");
                sendBroadcast(alarmIntent);
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SetTimerEnabled(getApplicationContext(), true);
                startService(new Intent(getApplicationContext(), TimerAlarmService.class));
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Stop", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SetTimerEnabled(getApplicationContext(), false);
                stopService(new Intent(getApplicationContext(), TimerAlarmService.class));
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        alertDialog.show();
    }
}