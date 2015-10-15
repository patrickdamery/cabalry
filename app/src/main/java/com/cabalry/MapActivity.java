package com.cabalry;

import android.os.Bundle;

import com.cabalry.map.CabalryMap;
import com.cabalry.map.CabalryUser;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Vector;

import static com.cabalry.CabalryUtility.*;
import static com.cabalry.CabalryPrefs.*;

public class MapActivity extends CabalryMap {

    private SupportMapFragment mMapFragment;

    private final CollectUsersTask collectUsersTask = new CollectUsersTask() {
        @Override
        protected void onPostExecute(Vector<CabalryUser> users) {
            if(users != null)
                updateUsers(users);
            else
                System.out.println(getFailState());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        int id = GetUserID(this);
        String key = GetUserKey(this);

        collectUsersTask.setCollectInfo(id, key);
        collectUsersTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the fragment
        if(mMapFragment == null) {
            // Try to obtain the map from the SupportMapFragment
            mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            initializeMap(mMapFragment);
        }
    }
}
