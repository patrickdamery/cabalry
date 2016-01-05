package com.cabalry.app;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cabalry.app.HomeActivity;
import com.cabalry.location.LocationUpdateListener;
import com.cabalry.location.LocationUpdateManager;
import com.cabalry.map.MapUser;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Vector;

/**
 * MapActivity
 */
public abstract class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationUpdateListener {

    public static final int MAP_PADDING = 128;
    public static final int CAM_MIN_ZOOM = 17;
    public static final int CAM_MAX_ZOOM = 15;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private HashMap<Integer, Marker> mMarkerMap = new HashMap<>();
    private Vector<MapUser> mUsers = new Vector<>();

    private final MarkerListener mMarkerListener = new MarkerListener();
    private final CameraListener mCameraListener = new CameraListener();

    public void initializeMap(SupportMapFragment mapFragment) {
        LocationUpdateManager.Instance(this).addUpdateListener(this);

        // This is to call the onMapReady callback
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationUpdateManager.Instance(this).resetProvider(manager);
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationUpdateManager.Instance(this).removeUpdateListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(mMarkerListener);
        mMap.setOnInfoWindowClickListener(mMarkerListener);
        mMap.setOnCameraChangeListener(mCameraListener);

        loadGoogleMapSettings();
    }

    public void loadGoogleMapSettings() {
        UiSettings settings = mMap.getUiSettings();

        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
        settings.setScrollGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setIndoorLevelPickerEnabled(false);
        settings.setCompassEnabled(false);
    }

    public Marker createMarker(final MapUser user) {
        LatLng position = user.getPosition();
        String title = user.getName() + " " + user.getColor() + " " + user.getCar();

        return mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title));
    }

    @Override
    public abstract void onUpdateLocation(Location location);

    public Marker getMarker(int id) {
        return mMarkerMap.get(id);
    }

    public void add(final MapUser user) {
        mMarkerMap.put(user.getID(), createMarker(user));
    }

    public void update(final MapUser oldUsr, final MapUser newUsr) {
        oldUsr.updatePosition(newUsr.getPosition());
        mMarkerMap.get(oldUsr.getID()).setPosition(newUsr.getPosition());
    }

    public void update(final MapUser oldUsr, final LatLng position) {
        oldUsr.updatePosition(position);
        mMarkerMap.get(oldUsr.getID()).setPosition(position);
    }

    public void remove(final MapUser user) {
        mMarkerMap.remove(user.getID()).remove();
    }

    public void setCameraFocus(LatLng target, float zoom, float bearing, int transTime) {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(target)
                .zoom(zoom)
                .bearing(bearing)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                transTime, mCameraListener);
    }

    public void setCameraFocus(Vector<LatLng> targets, int transTime) {
        if (targets.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : targets)
            builder.include(latLng);

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);

        mMap.animateCamera(cameraUpdate, transTime, mCameraListener);
    }

    /**
     * Algorithm that safely removes, inserts and updates
     * new users with the current list users and map markers.
     */
    public void updateUsers(final Vector<MapUser> newList) {
        if (newList == null)
            throw new NullPointerException("newList can't be null!");

        Vector<MapUser> removeList = new Vector<>();

        /**
         * First pass compare and update
         */
        for (int i = 0; i < mUsers.size(); i++) {
            MapUser oldUsr = mUsers.get(i);
            boolean exit = false;
            int index = 0; // Only used if exit is true

            for (int j = 0; j < newList.size() && !exit; j++) {
                MapUser newUsr = newList.get(j);

                // Compare id's
                if (oldUsr.getID() == newUsr.getID()) {
                    update(oldUsr, newUsr); // ID already exists, update

                    exit = true;
                    index = j;
                }
            }

            // Check output
            if (exit)
                newList.remove(index); // Updated, remove from new
            else
                removeList.add(oldUsr); // Outdated, queue for remove
        }

        /**
         * Second pass remove
         */
        for (int i = 0; i < removeList.size(); i++) {
            MapUser remove = removeList.get(i);
            mUsers.remove(remove);
            remove(remove);
        }

        /**
         * Third pass add
         */
        for (int i = 0; i < newList.size(); i++) {
            MapUser add = newList.get(i);
            mUsers.add(add);
            add(add);
        }
    }

    /**
     * Class implementation for marker related callbacks
     */
    private class MarkerListener implements GoogleMap.OnInfoWindowClickListener,
            GoogleMap.OnMarkerClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {

        }

        @Override
        public boolean onMarkerClick(Marker marker) {

            return false;
        }
    }

    /**
     * Class implementation for camera animation callbacks
     */
    private class CameraListener implements GoogleMap.CancelableCallback, GoogleMap.OnCameraChangeListener {

        @Override
        public void onFinish() {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
        }
    }
}