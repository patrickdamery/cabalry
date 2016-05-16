package com.cabalry.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.alarm.AlarmTimerService;
import com.cabalry.alarm.SilentAlarmService;
import com.cabalry.audio.AudioPlaybackService;
import com.cabalry.audio.AudioStreamService;
import com.cabalry.base.BindableService;
import com.cabalry.bluetooth.BluetoothService;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.util.MessageUtil;
import com.cabalry.util.TasksUtil;

import java.util.concurrent.ExecutionException;

import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.LogoutUser;
import static com.cabalry.util.PreferencesUtil.SetAlarmID;
import static com.cabalry.util.PreferencesUtil.SetAlarmIP;
import static com.cabalry.util.PreferencesUtil.SetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.SetDrawerLearned;
import static com.cabalry.util.PreferencesUtil.SetGPSChecked;
import static com.cabalry.util.PreferencesUtil.SetRegistrationID;

/**
 * CabalryAppService
 */
public class CabalryAppService extends BindableService {
    private static final String TAG = "CabalryAppService";

    private static boolean active = false;
    private static boolean started = false;

    private static Intent mCabalryAppIntent;
    private static Intent mAudioStreamIntent;
    private static Intent mAudioPlaybackIntent;
    private static Intent mLocationUpdateIntent;
    private static Intent mBluetoothIntent;

    private static boolean alarmStarted = false;

    private static void startAppServices(final Context context) {
        if (started) return;
        Log.i(TAG, "startAppServices");

        mLocationUpdateIntent = new Intent(context, LocationUpdateService.class);
        context.startService(mLocationUpdateIntent);

        mBluetoothIntent = new Intent(context, BluetoothService.class);
        context.startService(mBluetoothIntent);

        started = true;
    }

    private static void stopAppServices(final Context context) {
        Log.i(TAG, "stopAppServices");

        context.stopService(new Intent(context, AlarmTimerService.class));
        context.stopService(new Intent(context, SilentAlarmService.class));

        if (BluetoothService.isRunning() || mBluetoothIntent != null) {

            try {
                context.stopService(mBluetoothIntent);
            } catch (NullPointerException e) {
                Log.e(TAG, "Error service not stopped, mBluetoothIntent is null!");
            }
        }

        // Make sure an alarm is not active, otherwise don't stop services!
        if (GetAlarmID(context) == 0) {

            if (LocationUpdateService.isRunning() || mLocationUpdateIntent != null) {

                try {
                    context.stopService(mLocationUpdateIntent);
                } catch (NullPointerException e) {
                    Log.e(TAG, "Error service not stopped, mLocationUpdateIntent is null!");
                }
            }

            if (active || mCabalryAppIntent != null) {

                try {
                    context.stopService(mCabalryAppIntent);
                } catch (NullPointerException e) {
                    Log.e(TAG, "Error service not stopped, mCabalryAppIntent is null!");
                }
            }
        }

        started = false;
    }

    private static void performLogout(final Context context) {
        Log.i(TAG, "performLogout");

        new TasksUtil.CheckNetworkTask(context) {

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    new TasksUtil.UserLogoutTask(context) {
                        @Override
                        protected void onResult(final Boolean success) {
                            if (success) {
                                stopAppServices(context);

                                SetAlarmID(context, 0);
                                SetAlarmUserID(context, 0);
                                SetGPSChecked(context, false);
                                SetDrawerLearned(context, false);
                                SetRegistrationID(context, "");
                                LogoutUser(context);

                                Bundle data = new Bundle();
                                sendMessageToActivity(MessageUtil.MSG_LOGOUT, data);

                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.error_logout),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();

                } else {
                    stopAppServices(context);

                    SetAlarmID(context, 0);
                    SetAlarmUserID(context, 0);
                    SetGPSChecked(context, false);
                    SetDrawerLearned(context, false);
                    SetRegistrationID(context, "");
                    LogoutUser(context);

                    Bundle data = new Bundle();
                    sendMessageToActivity(MessageUtil.MSG_LOGOUT, data);
                }
            }
        }.execute();
    }

    private static void startAlarm(final Context context) {
        if (alarmStarted && GetAlarmID(context) != 0) {
            Intent alarm = new Intent(context, AlarmMapActivity.class);
            alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarm);

        } else {
            Log.i(TAG, "startAlarm");

            alarmStarted = true;

            new TasksUtil.CheckBillingTask(context) {
                @Override
                protected void onPostExecute(Boolean result) {
                    Log.i(TAG, "CheckBillingTask result: " + result);

                    if (!result) {
                        Toast.makeText(context, context.getResources().getString(R.string.error_billing),
                                Toast.LENGTH_LONG).show();

                        Log.i(TAG, "CheckBillingTask no billing!");

                    } else {
                        new TasksUtil.StartAlarmTask(context) {
                            @Override
                            protected void onResult(Boolean result) {
                                Log.i(TAG, "StartAlarmTask");

                                if (result) {
                                    new TasksUtil.GetAlarmInfoTask(context) {
                                        @Override
                                        protected void onPostExecute(Boolean result) {
                                            Log.i(TAG, "GetAlarmInfoTask");

                                            if (result) {
                                                Log.i(TAG, "Alarm started");

                                                SetAlarmIP(context, getIP());

                                                Bundle data = new Bundle();
                                                sendMessageToActivity(MessageUtil.MSG_ALARM_START, data);

                                                startAlarmServices(context);

                                                Intent alarm = new Intent(context, AlarmMapActivity.class);
                                                alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(alarm);

                                            } else {
                                                Log.e(TAG, "Error couldn't start alarm, no alarm info!");
                                                alarmStarted = false;
                                            }
                                        }
                                    }.execute();

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
    }

    private static void joinAlarm(final Context context, int alarmId, int userId) {
        if (alarmId == 0 || userId == 0) {
            Log.e(TAG, "Error could not join alarm, alarmId: " + alarmId + ", userId: " + userId);
            return;
        }

        if (GetAlarmID(context) != 0) {
            if (GetAlarmUserID(context) == GetUserID(context)) {
                // User has an alarm active
                // Go to alarm activity
                Intent alarm = new Intent(context, AlarmMapActivity.class);
                alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarm);
                return;

            } else if (GetUserID(context) == userId) {
                // Go to alarm activity
                Intent alarm = new Intent(context, AlarmMapActivity.class);
                alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarm);
                return;

            } else {
                // User was listening to an alarm, so it'll be ignored
                try {
                    new TasksUtil.IgnoreAlarmTask(context).execute().get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        SetAlarmID(context, alarmId);
        SetAlarmUserID(context, userId);

        new TasksUtil.GetAlarmInfoTask(context) {
            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    SetAlarmIP(context, getIP());
                    startAlarmServices(context);

                    // Go to alarm activity
                    Intent alarm = new Intent(context, AlarmMapActivity.class);
                    alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(alarm);

                } else {
                    Log.e(TAG, "Error joinAlarm failed, no alarm info!");
                }
            }
        }.execute();
    }

    private static void startAlarmServices(final Context context) {
        Log.i(TAG, "startAlarmServices");

        if (GetAlarmUserID(context) == GetUserID(context)) {
            // Start audio stream service
            mAudioStreamIntent = new Intent(context, AudioStreamService.class);
            context.startService(mAudioStreamIntent);

        } else {
            // Start audio playback service
            mAudioPlaybackIntent = new Intent(context, AudioPlaybackService.class);
            context.startService(mAudioPlaybackIntent);
        }
    }

    private static void stopAlarm(final Context context) {
        Log.i(TAG, "stopAlarm");

        new TasksUtil.StopAlarmTask(context).execute();

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_STOP, data);

        stopAlarmServices(context);
        alarmStarted = false;
    }

    private static void ignoreAlarm(final Context context) {
        Log.i(TAG, "ignoreAlarm");

        new TasksUtil.IgnoreAlarmTask(context).execute();

        Bundle data = new Bundle();
        sendMessageToActivity(MessageUtil.MSG_ALARM_IGNORE, data);

        stopAlarmServices(context);
        alarmStarted = false;
    }

    private static void stopAlarmServices(final Context context) {
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
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        active = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

        active = false;
    }

    public static class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "LoginReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            startAppServices(context);
        }
    }

    public static class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "LogoutReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            stopAppServices(context);
            performLogout(context);
        }
    }

    public static class AppStartedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "AppStartedReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            startAppServices(context);
        }
    }

    public static class AppClosedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "AppClosedReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            stopAppServices(context);
        }
    }

    public static class StartAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "StartAlarmReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            startAlarm(context);
        }
    }

    public static class JoinAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "JoinAlarmReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            Bundle extras = intent.getExtras();
            joinAlarm(context, extras.getInt("alarmId", 0), extras.getInt("userId", 0));
        }
    }

    public static class StopAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "StopAlarmReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            stopAlarm(context);
        }
    }

    public static class IgnoreAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "IgnoreAlarmReceiver onReceive");

            if (!active || mCabalryAppIntent == null) {
                mCabalryAppIntent = new Intent(context, CabalryAppService.class);
                context.startService(mCabalryAppIntent);
            }

            ignoreAlarm(context);
        }
    }
}