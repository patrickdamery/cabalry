package com.cabalry.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cabalry.R;
import com.cabalry.app.AlarmHistoryActivity;
import com.cabalry.app.AlarmMapActivity;
import com.cabalry.app.CabalryAppService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import static com.cabalry.net.CabalryServer.ALARM_ACTION_START;
import static com.cabalry.net.CabalryServer.ALARM_ACTION_STOP;
import static com.cabalry.net.CabalryServer.ALARM_GCM_ACTION;
import static com.cabalry.net.CabalryServer.ALARM_ID;
import static com.cabalry.net.CabalryServer.ALARM_USERID;
import static com.cabalry.util.PreferencesUtil.SetAlarmID;
import static com.cabalry.util.PreferencesUtil.SetAlarmUserID;

/**
 * GCMIntentService
 */
public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GCMIntentService";
    private static MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.fx_alarm);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "Deleted messages on server: " +
                        extras.toString());

                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String action = extras.getString(ALARM_GCM_ACTION);

                if (action != null && !action.isEmpty()) {
                    if (action.equals(ALARM_ACTION_START)) {

                        int alarmID = Integer.parseInt(extras.getString(ALARM_ID));
                        int userID = Integer.parseInt(extras.getString(ALARM_USERID));

                        SetAlarmID(getApplicationContext(), alarmID);
                        SetAlarmUserID(getApplicationContext(), userID);

                        if (mMediaPlayer == null)
                            mMediaPlayer = MediaPlayer.create(this, R.raw.fx_alarm);

                        if (mMediaPlayer.isPlaying())
                            mMediaPlayer.stop();
                        mMediaPlayer.start();

                        // Post notification of received message.
                        sendNotification(alarmID);

                    } else if (action.equals(ALARM_ACTION_STOP)) {

                        Intent stopIntent = new Intent();
                        stopIntent.setAction("com.cabalry.action.ALARM_STOP");
                        sendBroadcast(stopIntent);
                    }

                }
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(int alarmID) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AlarmMapActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alert)
                        .setContentTitle(getResources().getString(R.string.prompt_alarm_title))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getResources().getString(R.string.prompt_alarm) + alarmID))
                        .setContentText(getResources().getString(R.string.prompt_alarm) + alarmID);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
