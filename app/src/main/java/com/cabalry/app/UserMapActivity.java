package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.cabalry.R;
import com.cabalry.base.MapActivity;
import com.cabalry.base.MapUser;
import com.cabalry.location.LocationUpdateService;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Vector;

import static com.cabalry.util.MessageUtil.MSG_ALARM_START;
import static com.cabalry.util.MessageUtil.MSG_REGISTER_CLIENT;
import static com.cabalry.util.MessageUtil.MSG_UNREGISTER_CLIENT;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;
import static com.cabalry.util.TasksUtil.CheckNetworkTask;
import static com.cabalry.util.TasksUtil.CollectUserInfoTask;
import static com.cabalry.util.TasksUtil.CollectUsersTask;

/**
 * UserMapActivity
 */
public class UserMapActivity extends MapActivity {
    public static final int CAMERA_ZOOM = 15;
    public static final int TRANS_TIME = 1000;
    private static final String TAG = "UserMapActivity";
    private MapFragment mMapFragment;

    private CollectUsersTask mCollectUsersTask;
    private CollectUserInfoTask mCollectUserInfoTask;

    private boolean mNearbyToggle = false;

    private MapUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        if (!LocationUpdateService.isRunning()) {
            startService(new Intent(this, LocationUpdateService.class));
        }

        initialize();

        final ToggleButton bNearby = (ToggleButton) findViewById(R.id.bNearby);
        bNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNearbyToggle = bNearby.isChecked();
                onUpdateLocation(mUser.getPosition());

                if (mNearbyToggle)
                    progressBar.show();
            }
        });

        Button bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarm();
            }
        });
    }

    private void startAlarm() {
        Log.i(TAG, "startAlarm");

        bindToService(CabalryAppService.class, new MessengerHandler(),
                MSG_REGISTER_CLIENT, MSG_UNREGISTER_CLIENT);

        progressBar.show();

        Intent intent = new Intent();
        intent.setAction("com.cabalry.action.ALARM_START");
        sendBroadcast(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
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
        userInfo.putExtra("parent", "map");
        startActivity(userInfo);
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
            mUser.updatePosition(location);

            if (mNearbyToggle)
                collectNearbyUsers();
            else {
                if (mCollectUsersTask != null)
                    mCollectUsersTask.cancel(true);

                updateUser(mUser);
                setCameraFocus(location, CAMERA_ZOOM, 0, TRANS_TIME);
            }
        } else {
            collectUserInfo();
        }

        Log.d(TAG, "users in map: " + getMapUsersCount());
        Log.d(TAG, "onUpdateLocation location: " + location.toString());
    }

    private void collectNearbyUsers() {
        if (mCollectUsersTask == null) {

            new CheckNetworkTask(getApplicationContext()) {

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        mCollectUsersTask = getCollectUsersTask();
                        mCollectUsersTask.execute();

                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                }
            }.execute();
        }
    }

    private void collectUserInfo() {
        if (mCollectUserInfoTask == null) {
            new CheckNetworkTask(getApplicationContext()) {

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        mCollectUserInfoTask = getCollectUserInfoTask();

                        int id = GetUserID(getApplicationContext());
                        mCollectUserInfoTask.set(id, id, GetUserKey(getApplicationContext()));
                        mCollectUserInfoTask.execute();

                    } else {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                }
            }.execute();
        }
    }

    private CollectUsersTask getCollectUsersTask() {
        return new CollectUsersTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(Vector<MapUser> users) {
                progressBar.dismiss();

                if (users != null) {
                    Log.i(TAG, "CollectUsersTask - users size: " + users.size());

                    Vector<LatLng> targets = new Vector<>();
                    targets.add(mUser.getPosition());
                    users.add(mUser);

                    for (MapUser user : users)
                        targets.add(user.getPosition());

                    setCameraFocus(targets, TRANS_TIME);

                    updateUsers(users);
                    Log.i(TAG, "CollectUsersTask - updated");
                } else {
                    Log.e(TAG, "CollectUsersTask - users is null! fail state: " + getFailState());
                    setCameraFocus(mUser.getPosition(), CAMERA_ZOOM, 0, TRANS_TIME);
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
                    add(user);
                    setCameraFocus(user.getPosition(), CAMERA_ZOOM, 0, TRANS_TIME);
                }

                mCollectUserInfoTask = null;
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
                case MSG_ALARM_START:
                    Log.i(TAG, "MSG_ALARM_START");
                    if (isRunning) {
                        progressBar.dismiss();
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
