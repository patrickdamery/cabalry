package com.cabalry.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class SilentAlarmReceiver extends WakefulBroadcastReceiver {

    public static boolean isReceived; // this is made true and false after each timer clock
    public static Timer timer = null;
    public static int i;

    private final int TIME = 3000;
    private final int MAX_ITERATION = 15;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        isReceived = true; // Make this true whenever isReceived called
        Preferences.initialize(context);
        if(timer == null /*&& Preferences.getBoolean(GlobalKeys.SILENT)*/) {

            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    if(isReceived) {
                        // If its true it means user is still pressing the button.
                        i++;

                    } else {
                        // In this case user must has released the button so we have to reset the timer.
                        cancel();
                        timer.cancel();
                        timer.purge();
                        timer = null;
                        i = 0;
                    }

                    if(i >= MAX_ITERATION) {
                        // In this case we had successfully detected the long press event.
                        // it is called after 3 seconds
                        cancel();
                        timer.cancel();
                        timer.purge();

                        timer = null;
                        i = 0;
                        startSilentAlarm(context);
                    }

                    isReceived = false; // Make this false every time a timer iterates.
                }
            }, 0, TIME/MAX_ITERATION);
        }
    }

    private void startSilentAlarm(Context context) {

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, new Intent(context, SilentAlarmService.class));
    }
}