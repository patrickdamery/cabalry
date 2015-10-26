package com.cabalry;

import android.os.Bundle;

import com.cabalry.map.MapActivity;
import com.cabalry.map.MapUser;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Vector;

import static com.cabalry.util.Utility.*;

public class UserMapActivity extends MapActivity {

    private SupportMapFragment mMapFragment;

    private CollectUsersTask collectUsersTask;

    private final CollectUsersTask getCollectUsersTask() {
        return new CollectUsersTask() {
            @Override
            protected void onPostExecute(Vector<MapUser> users) {
                if(users != null)
                    updateUsers(users);
                collectUsersTask = null;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        initialize();

        // Start tracer service.
        //Intent tracer = new Intent(getApplicationContext(), LocationUpdateService.class);
        //startService(tracer);
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

        if(collectUsersTask == null) {

            collectUsersTask = getCollectUsersTask();
            collectUsersTask.setCollectInfo(GetUserID(this), GetUserKey(this));
            collectUsersTask.execute();
        }
    }
}
