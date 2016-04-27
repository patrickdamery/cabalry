package com.cabalry.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.app.AlarmMapActivity;
import com.cabalry.audio.AudioPlaybackService;
import com.cabalry.audio.AudioStreamService;
import com.cabalry.base.BindableService;
import com.cabalry.util.MessageUtil;
import com.cabalry.util.TasksUtil;

import static com.cabalry.util.PreferencesUtil.GetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.SetAlarmIP;

/**
 * AlarmService
 */
public class AlarmService extends BindableService {
    private static final String TAG = "AlarmService";

    static boolean active = false;

    static Intent mAlarmIntent;
    static Intent mAudioStreamIntent;
    static Intent mAudioPlaybackIntent;

    static boolean alarmStarted = false;

    private static void startAlarm(final Context context) {
        if (alarmStarted) return;
        alarmStarted = true;

        new TasksUtil.CheckBillingTask(context) {
            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_billing),
                            Toast.LENGTH_LONG).show();

                } else {
                    new TasksUtil.StartAlarmTask(context) {
                        @Override
                        protected void onResult(Boolean result) {
                            if (result) {

                                final boolean selfActivated = GetAlarmUserID(context) == GetUserID(context);

                                new TasksUtil.GetAlarmInfoTask(context) {
                                    @Override
                                    protected void onPostExecute(Boolean result) {

                                        if (result) {
                                            SetAlarmIP(context, getIP());

                                            Bundle data = new Bundle();
                                            sendMessageToActivity(MessageUtil.MSG_ALARM_START, data);

                                            if (selfActivated) {
                                                // Start audio stream service
                                                mAudioStreamIntent = new Intent(context, AudioStreamService.class);
                                                context.startService(mAudioStreamIntent);

                                            } else {
                                                // Start audio playback service
                                                mAudioPlaybackIntent = new Intent(context, AudioPlaybackService.class);
                                                context.startService(mAudioPlaybackIntent);
                                            }

                                        } else {
                                            Log.e(TAG, "Error no alarm info!");
                                            alarmStarted = false;
                                        }
                                    }
                                }.execute();

                                Intent alarm = new Intent(context, AlarmMapActivity.class);
                                alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(alarm);

                            } else {
                                // TODO handle case were alarm couldn't start, start sms mode
                                Log.e(TAG, "Error couldn't start alarm!");
                            }
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    private static void stopAlarm(final Context context) {
        new TasksUtil.StopAlarmTask(context).execute();
        alarmStarted = false;

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_STOP, data);

        stopServices(context);
    }

    private static void ignoreAlarm(final Context context) {
        new TasksUtil.IgnoreAlarmTask(context).execute();
        alarmStarted = false;

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_IGNORE, data);

        stopServices(context);
    }

    private static void stopServices(final Context context) {
        if (AudioPlaybackService.isRunning() || mAudioPlaybackIntent != null) {
            AudioPlaybackService.stopAudioPlayback();

            try {
                context.stopService(mAudioPlaybackIntent);
            } catch (NullPointerException e) {
                Log.e(TAG, "Error service not stopped, mAudioPlaybackIntent is null!");
            }
        }

        if (AudioStreamService.isRunning() || mAudioStreamIntent != null) {
            AudioStreamService.stopAudioStream();

            try {
                context.stopService(mAudioStreamIntent);
            } catch (NullPointerException e) {
                Log.e(TAG, "Error service not stopped, mAudioStreamIntent is null!");
            }
        }

        if (active || mAlarmIntent != null) {

            try {
                context.stopService(mAlarmIntent);
            } catch (NullPointerException e) {
                Log.e(TAG, "Error service not stopped, mAlarmIntent is null!");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        active = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        active = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
    }

    public static class StartAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "StartAlarmReceiver onReceive");

            if (!active || mAlarmIntent == null) {
                mAlarmIntent = new Intent(context, AlarmService.class);
                context.startService(mAlarmIntent);
            }

            startAlarm(context);
        }
    }

    public static class StopAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "StopAlarmReceiver onReceive");

            if (!active || mAlarmIntent == null) {
                mAlarmIntent = new Intent(context, AlarmService.class);
                context.startService(mAlarmIntent);
            }

            stopAlarm(context);
        }
    }

    public static class IgnoreAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "IgnoreAlarmReceiver onReceive");

            if (!active || mAlarmIntent == null) {
                mAlarmIntent = new Intent(context, AlarmService.class);
                context.startService(mAlarmIntent);
            }

            ignoreAlarm(context);
        }
    }
}