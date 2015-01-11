package com.cabalry.custom;

import android.app.Activity;
import android.content.Context;
import com.cabalry.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;

/**
 * Created by conor on 11/01/15.
 */
public class CMap implements OnMapReadyCallback {

    private Activity activity;
    private MapFragment mapFragment;
    private GoogleMap mMap;
    private UiSettings settings;

    private CMarkerManager markerManager;
    private CCamera camera;

    private ArrayList<CMarker> cMarkers;

    public CMap(Activity activity) {

        this.activity = activity;

        MapFragment mapFragment = (MapFragment) activity.getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        markerManager = new CMarkerManager(this);
        camera = new CCamera(this);

        cMarkers = new ArrayList<CMarker>();
    }

    public void updateMap() {

        markerManager.updateMarkers(cMarkers);
        camera.updateCamera();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        settings = map.getUiSettings();

        // Default settings.

        /*
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
        settings.setScrollGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setIndoorLevelPickerEnabled(false);
        settings.setCompassEnabled(false);
        */
    }

    public UiSettings getSettings() {
        return settings;
    }
    public GoogleMap getMap() { return mMap; }
    public MapFragment getMapFragment() { return mapFragment; }
    public ArrayList<CMarker> getMarkers() { return cMarkers; }
    public CCamera getCamera() { return camera; }
}
