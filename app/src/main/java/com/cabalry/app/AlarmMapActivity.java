package com.cabalry.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.alarm.AlarmService;
import com.cabalry.audio.AudioPlaybackService;
import com.cabalry.audio.AudioStreamService;
import com.cabalry.base.MapActivity;
import com.cabalry.base.MapUser;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.net.CabalryServer;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Vector;

import static com.cabalry.util.MessageUtil.MSG_ALARM_STOP;
import static com.cabalry.util.MessageUtil.MSG_REGISTER_CLIENT;
import static com.cabalry.util.MessageUtil.MSG_UNREGISTER_CLIENT;
import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.GetFakePassword;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;
import static com.cabalry.util.PreferencesUtil.SetAlarmIP;
import static com.cabalry.util.PreferencesUtil.SetFakeActive;
import static com.cabalry.util.TasksUtil.CheckPasswordTask;
import static com.cabalry.util.TasksUtil.CollectUserInfoTask;
import static com.cabalry.util.TasksUtil.CollectUsersTask;
import static com.cabalry.util.TasksUtil.GetAlarmInfoTask;

/**
 * AlarmMapActivity
 */
public class AlarmMapActivity extends MapActivity {

    public static final int CAMERA_ZOOM = 15;
    public static final int TRANS_TIME = 1000;

    private MapFragment mMapFragment;

    private CollectUsersTask mCollectUsersTask;
    private CollectUserInfoTask mCollectUserInfoTask;

    private MapUser mUser;
    private int mPassFailedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_map);

        if (GetAlarmID(this) == 0 || GetAlarmUserID(this) == 0) {
            stopAlarm();
        }

        if (!LocationUpdateService.isRunning()) {
            startService(new Intent(this, LocationUpdateService.class));
        }

        initialize();

        // noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        final boolean selfActivated = GetAlarmUserID(this) == GetUserID(this);

        new GetAlarmInfoTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(Boolean result) {

                if (result) {
                    SetAlarmIP(getApplicationContext(), getIP());

                    if (selfActivated) {
                        // Start audio stream service
                        startService(new Intent(getApplicationContext(), AudioStreamService.class));

                    } else {
                        // Start audio playback service
                        startService(new Intent(getApplicationContext(), AudioPlaybackService.class));
                    }

                } else {
                    Log.e(TAG, "Error no alarm info!");
                }
            }
        }.execute();

        Button bCancel = (Button) findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selfActivated) {
                    // Prompt password.
                    promptPassword();
                } else {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ignoreAlarm();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmMapActivity.this);
                    builder.setMessage(getResources().getString(R.string.prompt_ignore))
                            .setPositiveButton(getResources().getString(R.string.prompt_yes), dialogClickListener)
                            .setNegativeButton(getResources().getString(R.string.prompt_no), dialogClickListener)
                            .show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        bindToService(LocationUpdateService.class, new MessengerHandler(),
                MSG_REGISTER_CLIENT, MSG_UNREGISTER_CLIENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unbindFromService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent userInfo = new Intent(getApplicationContext(), UserInfoActivity.class);
        userInfo.putExtra("id", getUserID(marker));
        userInfo.putExtra("parent", "alarm");
        startActivity(userInfo);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initialize() {

        // Do a null check to confirm that we have not already instantiated the fragment
        if (mMapFragment == null) {
            // Try to obtain the map from the SupportMapFragment
            mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            initializeMap(mMapFragment);
        }

        collectUserInfo();
        collectNearbyUsers();
    }

    @Override
    public void onUpdateLocation(LatLng location) {
        if (mUser != null) {
            update(mUser, location);
            collectNearbyUsers();
        }
    }

    private void promptPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(getResources().getString(R.string.prompt_password));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        alert.setView(input);

        alert.setPositiveButton(getResources().getString(R.string.prompt_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String value = input.getText().toString();

                        if (value.isEmpty()) {
                            mPassFailedCount++;
                            if (mPassFailedCount > 3) {
                                // Fake stop alarm.
                                Log.i(TAG, "Alarm fake stopped");
                                SetFakeActive(getApplicationContext(), true);
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            } else {
                                promptPassword();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_wrong_password),
                                        Toast.LENGTH_LONG).show();
                            }

                        } else if (value.equals(GetFakePassword(getApplicationContext()))) {
                            // Fake stop alarm.
                            Log.i(TAG, "Alarm fake stopped");
                            SetFakeActive(getApplicationContext(), true);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        } else {

                            new CheckPasswordTask(getApplicationContext(), value) {
                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if (result) {
                                        Log.i(TAG, "Alarm stopped");
                                        SetFakeActive(getApplicationContext(), false);
                                        stopAlarm();

                                    } else {
                                        mPassFailedCount++;
                                        if (mPassFailedCount > 3) {
                                            // Fake stop alarm.
                                            Log.i(TAG, "Alarm fake stopped");
                                            SetFakeActive(getApplicationContext(), true);
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        } else {
                                            promptPassword();
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_wrong_password),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }.execute();
                        }
                    }
                });

        alert.setNegativeButton(getResources().getString(R.string.prompt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    private void stopAlarm() {
        AlarmService.stopAlarm(getApplicationContext());
        stopService(new Intent(this, AlarmService.class));

        // return to home.
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private void ignoreAlarm() {
        AlarmService.ignoreAlarm(getApplicationContext());
        stopService(new Intent(this, AlarmService.class));

        // return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private void collectNearbyUsers() {
        if (mCollectUsersTask == null) {

            mCollectUsersTask = getCollectUsersTask();
            mCollectUsersTask.setType(CabalryServer.UserRequestType.ALARM);
            mCollectUsersTask.execute();
        }
    }

    private void collectUserInfo() {
        if (mCollectUserInfoTask == null) {
            mCollectUserInfoTask = getCollectUserInfoTask();

            int id = GetUserID(this);
            mCollectUserInfoTask.set(id, id, GetUserKey(this));
            mCollectUserInfoTask.execute();
        }
    }

    private CollectUsersTask getCollectUsersTask() {
        return new CollectUsersTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(Vector<MapUser> users) {
                if (users != null) {
                    Vector<LatLng> targets = new Vector<>();
                    targets.add(mUser.getPosition());
                    users.add(mUser);

                    for (MapUser user : users)
                        targets.add(user.getPosition());

                    setCameraFocus(targets, TRANS_TIME);

                    updateUsers(users);
                }

                mCollectUsersTask = null;
            }
        };
    }

    private CollectUserInfoTask getCollectUserInfoTask() {
        return new CollectUserInfoTask() {
            @Override
            protected void onPostExecute(MapUser user) {
                if (user == null)
                    throw new NullPointerException("CABARLY - user is null, STATE: " + getFailState());
                else {
                    mUser = user;
                    add(mUser);
                    setCameraFocus(mUser.getPosition(), CAMERA_ZOOM, 0, TRANS_TIME);
                }
            }
        };
    }

    /**
     * MessengerHandler
     */
    private class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data == null)
                throw new NullPointerException("data is null");

            switch (msg.what) {
                case MSG_ALARM_STOP:
                    Log.i(TAG, "MSG_ALARM_STOP");
                    if (isRunning) {
                        // return to home.
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
