package com.cabalry;

import android.os.Bundle;

import com.cabalry.map.CabalryMap;
import com.cabalry.map.CabalryUser;
import com.google.android.gms.games.internal.constants.RequestType;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Vector;

import static com.cabalry.CabalryUtility.*;

public class MapActivity extends CabalryMap {

    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        CabalryPrefs.begin(this);
        int id = CabalryPrefs.getUserID();
        String key = CabalryPrefs.getUserKey();
        CollectUsersTask collectUsers = new CollectUsersTask(id, key, UserRequestType.NEARBY, 0) {
            @Override
            protected void onPostExecute(Vector<CabalryUser> users) {
                if(users == null)
                    System.out.println("User is null 2");
                else
                    PrintCabalryUserList(users);
            }
        };

        collectUsers.execute();
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
