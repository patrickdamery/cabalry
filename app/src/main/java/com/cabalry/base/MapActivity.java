package com.cabalry.base;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cabalry.R;
import com.cabalry.app.HomeActivity;
import com.cabalry.location.LocationUpdateListener;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.map.MapUser;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Vector;

import static com.cabalry.util.MessageUtil.*;

/**
 * MapActivity
 */
public abstract class MapActivity extends BindableActivity
        implements OnMapReadyCallback {
    public static final String TAG = "MapActivity";

    public static final int MAP_PADDING = 128;

    private MapFragment mMapFragment;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private HashMap<Integer, Marker> mMarkerMap = new HashMap<>();
    private Vector<MapUser> mUsers = new Vector<>();

    private final MarkerListener mMarkerListener = new MarkerListener();
    private final CameraListener mCameraListener = new CameraListener();

    public abstract void onUpdateLocation(LatLng location);

    public void initializeMap(MapFragment mapFragment) {
        if (mapFragment == null)
            throw new NullPointerException("mapFragment is null!");

        // This is to call the onMapReady callback
        mMapFragment = mapFragment;
        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindToService(LocationUpdateService.class, new MessengerHandler(),
                MSG_REGISTER_CLIENT, MSG_UNREGISTER_CLIENT);
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindFromService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
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
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromResource(getMarkerIcon(user.getType())))
        );
    }

    public Marker getMarker(int id) {
        return mMarkerMap.get(id);
    }

    public void add(final MapUser user) {
        mMarkerMap.put(user.getID(), createMarker(user));
    }

    public void update(final MapUser oldUsr, final MapUser newUsr) {
        oldUsr.updatePosition(newUsr.getPosition());
        Marker marker = mMarkerMap.get(oldUsr.getID());
        marker.setPosition(newUsr.getPosition());
        marker.setIcon(BitmapDescriptorFactory.fromResource(getMarkerIcon(newUsr.getType())));
    }

    public void update(final MapUser oldUsr, final LatLng position) {
        oldUsr.updatePosition(position);
        mMarkerMap.get(oldUsr.getID()).setPosition(position);
    }

    public void remove(final MapUser user) {
        mMarkerMap.remove(user.getID()).remove();
    }

    private int getMarkerIcon(MapUser.UserType type) {

        int icon = 0;
        switch (type) {
            case USER:
                icon = R.drawable.ic_user;
                break;
            case NEARBY:
                icon = R.drawable.ic_nearby;
                break;
            case ALERT:
                icon = R.drawable.ic_alert;
                break;
            case ALERTED:
                icon = R.drawable.ic_alerted;
                break;
        }

        return icon;
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
     * MessengerHandler
     */
    private class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data == null)
                throw new NullPointerException("data is null");

            switch (msg.what) {
                case MSG_LOCATION_UPDATE:
                    double lat = data.getDouble("lat");
                    double lng = data.getDouble("lng");
                    onUpdateLocation(new LatLng(lat, lng));
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
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