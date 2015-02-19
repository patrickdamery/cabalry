package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import com.cabalry.activity.AlarmActivity;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmService extends Service {

    @Override
    public void onCreate() {
        Preferences.initialize(getApplicationContext());
        Logger.log("ALARM ACTIVATED!");

        if(Preferences.getAlarmId() != 0) {
            // Start alarm activity.
            launchAlarm();
            return;
        }
        startAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startAlarm() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                JSONObject result = DB.alarm(Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {

                        int alarmID = result.getInt(GlobalKeys.ALARM_ID);
                        Preferences.setCachedAlarmId(Preferences.getAlarmId());
                        Preferences.setAlarmId(alarmID);
                        return true;

                    } else {
                        Preferences.setCachedAlarmId(0);
                        Preferences.setAlarmId(0);
                        Logger.log("Could not start alarm!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    // Start alarm activity.
                    launchAlarm();
                }
            }
        }.execute();
    }

    private void launchAlarm() {
        // Start alarm activity.
        Intent alarm = new Intent(getApplicationContext(), AlarmActivity.class);
        alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarm);
    }
}
