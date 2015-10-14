package com.cabalry;

import android.os.Bundle;

import com.cabalry.map.CabalryMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends CabalryMap {

    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
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
