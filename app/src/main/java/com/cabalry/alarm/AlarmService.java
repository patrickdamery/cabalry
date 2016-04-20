package com.cabalry.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

    @Override
    public void onCreate() {
        super.onCreate();

        startAlarm(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO set correct flag to stay alive when app closes
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    public static void stopAlarm(final Context context) {
        if (AudioPlaybackService.isRunning())
            AudioPlaybackService.stopAudioPlayback();

        if (AudioStreamService.isRunning())
            AudioStreamService.stopAudioStream();

        context.stopService(new Intent(context, AudioStreamService.class));
        context.stopService(new Intent(context, AudioPlaybackService.class));

        SetAlarmID(context, 0);
        SetAlarmUserID(context, 0);

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_STOP, data);

        new TasksUtil.StopAlarmTask(context).execute();
    }
}