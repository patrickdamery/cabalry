package com.cabalry.app;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.cabalry.R;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.map.MapActivity;
import com.cabalry.map.MapUser;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Vector;

import static com.cabalry.util.PrefsUtil.*;
import static com.cabalry.util.TasksUtil.*;

public class UserMapActivity extends MapActivity {

    public static final int CAMERA_ZOOM = 15;
    public static final int TRANS_TIME = 1000;

    private SupportMapFragment mMapFragment;

    private CollectUsersTask mCollectUsersTask;
    private CollectUserInfoTask mCollectUserInfoTask;

    private boolean mNearbyToggle = true;

    private MapUser mUser;

    private final CollectUsersTask getCollectUsersTask() {
        return new CollectUsersTask() {
            @Override
            protected void onPostExecute(Vector<MapUser> users) {
                if(users != null) {
                    Vector<LatLng> targets = new Vector<>();
                    targets.add(mUser.getPosition());

                    for(MapUser user : users)
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
                if(user == null) throw new NullPointerException("CABARLY - user is null, STATE: "+getFailState());
                else {
                    mUser = user; add(mUser);
                    setCameraFocus(mUser.getPosition(), CAMERA_ZOOM, 0, TRANS_TIME);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        initialize();

        // Start location update service.
        Intent intent = new Intent(getApplicationContext(), LocationUpdateService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {

        // Do a null check to confirm that we have not already instantiated the fragment
        if(mMapFragment == null) {
            // Try to obtain the map from the SupportMapFragment
            mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            initializeMap(mMapFragment);
        }

        collectUserInfo();
        if(mNearbyToggle)
            collectNearbyUsers();
    }

    @Override
    public void onUpdateLocation(Location location) {
        LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());

        if(mUser != null) {
            update(mUser, userPosition);

            if(mNearbyToggle)
                collectNearbyUsers();
            else
                setCameraFocus(userPosition, CAMERA_ZOOM, 0, TRANS_TIME);

        }
    }

    private void collectNearbyUsers() {
        if(mCollectUsersTask == null) {

            mCollectUsersTask = getCollectUsersTask();
            mCollectUsersTask.setCollectInfo(GetUserID(this), GetUserKey(this));
            mCollectUsersTask.execute();
        }
    }

    private void collectUserInfo() {
        if(mCollectUserInfoTask == null) {
            mCollectUserInfoTask = getCollectUserInfoTask();

            int id = GetUserID(this);
            mCollectUserInfoTask.set(id, id, GetUserKey(this));
            mCollectUserInfoTask.execute();
        }
    }
}
