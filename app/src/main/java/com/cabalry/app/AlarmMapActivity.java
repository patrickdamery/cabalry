package com.cabalry.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cabalry.R;
import com.cabalry.base.MapActivity;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.base.MapUser;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.TasksUtil.*;

/**
 * AlarmMapActivity
 */
public class AlarmMapActivity extends MapActivity {

    public static final int CAMERA_ZOOM = 15;
    public static final int TRANS_TIME = 1000;

    private MapFragment mMapFragment;

    private CollectUsersTask mCollectUsersTask;
    private CollectUserInfoTask mCollectUserInfoTask;

    private boolean mNearbyToggle = true;

    private MapUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_map);
        initialize();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Start location update service.
        if (!LocationUpdateService.isRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationUpdateService.class);
            startService(intent);
        }

        final boolean selfActivated = true;

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
                                    //ignoreAlarm();
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
    protected void onResume() {
        super.onResume();
        initialize();
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
        if (mNearbyToggle)
            collectNearbyUsers();
    }

    @Override
    public void onUpdateLocation(LatLng location) {
        if (mUser != null) {
            update(mUser, location);

            if (mNearbyToggle)
                collectNearbyUsers();
            else
                setCameraFocus(location, CAMERA_ZOOM, 0, TRANS_TIME);
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
                        new CheckPasswordTask(getApplicationContext(), value) {
                            @Override
                            protected void onPostExecute(Boolean result) {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        }.execute();
                    }
                });

        alert.setNegativeButton(getResources().getString(R.string.prompt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    private void collectNearbyUsers() {
        if (mCollectUsersTask == null) {

            mCollectUsersTask = getCollectUsersTask();
            mCollectUsersTask.setCollectInfo(GetUserID(this), GetUserKey(this));
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

    private final CollectUsersTask getCollectUsersTask() {
        return new CollectUsersTask() {
            @Override
            protected void onPostExecute(Vector<MapUser> users) {
                if (users != null) {
                    Vector<LatLng> targets = new Vector<>();
                    targets.add(mUser.getPosition());

                    for (MapUser user : users)
                        targets.add(user.getPosition());

                    setCameraFocus(targets, TRANS_TIME);

                    updateUsers(users);
                }

                mCollectUsersTask = null;
            }
        };
    }

    private final CollectUserInfoTask getCollectUserInfoTask() {
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
}
