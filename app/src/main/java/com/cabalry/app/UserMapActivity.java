package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
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

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.TasksUtil.*;

/**
 * UserMapActivity
 */
public class UserMapActivity extends MapActivity {
    private static final String TAG = "UserMapActivity";

    public static final int CAMERA_ZOOM = 15;
    public static final int TRANS_TIME = 1000;

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
            }
        });

        Button bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlarm();
            }
        });
    }

    private void launchAlarm() {
        new StartAlarmTask(getApplicationContext()) {
            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    startActivity(new Intent(getApplicationContext(), AlarmMapActivity.class));
                }
            }
        }.execute();
    }

    @Override
    protected void onResume() {
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
            else
                setCameraFocus(location, CAMERA_ZOOM, 0, TRANS_TIME);
        }

        Log.i(TAG, "onUpdateLocation location: " + location.toString());
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
                    Log.i(TAG, "CollectUsersTask - users size: " + users.size());

                    Vector<LatLng> targets = new Vector<>();
                    targets.add(mUser.getPosition());

                    for (MapUser user : users)
                        targets.add(user.getPosition());

                    setCameraFocus(targets, TRANS_TIME);

                    updateUsers(users);
                } else {
                    Log.i(TAG, "CollectUsersTask - users is null! fail state: " + getFailState());
                    setCameraFocus(mUser.getPosition(), CAMERA_ZOOM, 0, TRANS_TIME);
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

                mCollectUserInfoTask = null;
            }
        };
    }
}
