package com.cabalry.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import com.cabalry.R;
import com.cabalry.activity.MapActivity;

/**
 * Created by conor on 04/02/15.
 */
public class SilentAlarmService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public SilentAlarmService() {
        super("SilentAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate two rounds of 600 millis and sleep 1000 in between.
        long[] pattern = {0, 600, 1000, 600};

        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        v.vibrate(pattern, -1);

        sendNotification();

        // Start alarm.
        startService(new Intent(getApplicationContext(), AlarmService.class));

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        SilentAlarmReceiver.completeWakefulIntent(intent);
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
