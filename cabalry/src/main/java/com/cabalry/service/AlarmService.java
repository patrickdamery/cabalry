package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.cabalry.activity.MapActivity;
import com.cabalry.utils.Logger;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmService extends Service {

    @Override
    public void onCreate() {
        Logger.log("Alarm activated!");

        /*Intent alarm = new Intent(getApplicationContext(), MapActivity.class);
        alarm.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarm);*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
