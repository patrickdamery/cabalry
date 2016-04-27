package com.cabalry.alarm;

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
import static com.cabalry.util.PreferencesUtil.SetAlarmID;
import static com.cabalry.util.PreferencesUtil.SetAlarmIP;
import static com.cabalry.util.PreferencesUtil.SetAlarmUserID;

/**
 * AlarmService
 */
public class AlarmService extends BindableService {
    private static final String TAG = "AlarmService";

    static Intent selfIntent;

    public static void startAlarm(final Context context) {
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

                                            if (selfActivated) {
                                                // Start audio stream service
                                                context.startService(new Intent(context, AudioStreamService.class));

                                            } else {
                                                // Start audio playback service
                                                context.startService(new Intent(context, AudioPlaybackService.class));
                                            }

                                        } else {
                                            Log.e(TAG, "ERROR no alarm info!");
                                        }
                                    }
                                }.execute();

                                Intent alarm = new Intent(context, AlarmMapActivity.class);
                                alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(alarm);

                            } else {
                                // handle case were alarm couldn't start
                                Log.e(TAG, "ERROR couldn't start alarm!");
                            }
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    public static Intent getServiceIntent() {
        return selfIntent;
    }

    public static void stopAlarm(final Context context) {
        Log.i(TAG, "STOP ALARM");

        if (AudioPlaybackService.isRunning()) {
            AudioPlaybackService.stopAudioPlayback();

            try {
                context.stopService(AudioPlaybackService.getServiceIntent());
            } catch (NullPointerException e) {
                Log.e(TAG, "getServiceIntent is null!");
            }
        }

        if (AudioStreamService.isRunning()) {
            AudioStreamService.stopAudioStream();

            try {
                context.stopService(AudioStreamService.getServiceIntent());
            } catch (NullPointerException e) {
                Log.e(TAG, "Error getServiceIntent is null!");
            }
        }

        SetAlarmID(context, 0);
        SetAlarmUserID(context, 0);

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_STOP, data);

        new TasksUtil.StopAlarmTask(context).execute();
    }

    public static void ignoreAlarm(final Context context) {
        Log.i(TAG, "IGNORE ALARM");

        if (AudioPlaybackService.isRunning()) {
            AudioPlaybackService.stopAudioPlayback();

            try {
                context.stopService(AudioPlaybackService.getServiceIntent());
            } catch (NullPointerException e) {
                Log.e(TAG, "getServiceIntent is null!");
            }
        }

        if (AudioStreamService.isRunning()) {
            AudioStreamService.stopAudioStream();

            try {
                context.stopService(AudioStreamService.getServiceIntent());
            } catch (NullPointerException e) {
                Log.e(TAG, "Error getServiceIntent is null!");
            }
        }

        SetAlarmID(context, 0);
        SetAlarmUserID(context, 0);

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_IGNORE, data);

        new TasksUtil.IgnoreAlarmTask(context).execute();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startAlarm(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            selfIntent = intent;
            Log.i(TAG, "selfIntent set");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(selfIntent);
    }
}