package com.cabalry.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.cabalry.R;
import com.cabalry.activity.MapActivity;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 04/02/15.
 */
public class AlarmTimerService extends Service {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private Timer timer = null;

    @Override
    public void onCreate() {

        Preferences.initialize(getApplicationContext());
        int timerTime = Preferences.getInt(GlobalKeys.TIMER) * 60 * 1000;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendNotification();
                startService(new Intent(getApplicationContext(), AlarmService.class));
            }
        }, timerTime, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Cabalry Alarm")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("An alarm has been triggered! AlarmID : "))
                        .setContentText("An alarm has been triggered! AlarmID : ");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}