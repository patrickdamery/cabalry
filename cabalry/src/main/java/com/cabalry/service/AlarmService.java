package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.cabalry.utils.Logger;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmService extends Service {

    @Override
    public void onCreate() {
        Logger.log("Alarm activated!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
