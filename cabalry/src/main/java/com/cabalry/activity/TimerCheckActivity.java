package com.cabalry.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.AlarmService;
import com.cabalry.service.AlarmTimerService;
import com.cabalry.utils.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 18/02/15.
 */
public class TimerCheckActivity extends Activity {

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
                launchAlarm();
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
                Preferences.setBoolean(GlobalKeys.TIMER_ENABLED, true);
                timerTask.cancel();
                onBackPressed();
            }

        });

        builder.setNegativeButton("Alarm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preferences.setBoolean(GlobalKeys.TIMER_ENABLED, false);
                timerTask.cancel();
                launchAlarm();
            }
        });

        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void launchAlarm() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                JSONObject result = DB.checkBilling(Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS))
                        return true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result) {
                    Toast.makeText(getApplicationContext(), "Cannot start alarm please check your billing.",
                            Toast.LENGTH_LONG).show();
                } else {
                    startService(new Intent(getApplicationContext(), AlarmService.class));
                }
            }
        }.execute();
    }
}