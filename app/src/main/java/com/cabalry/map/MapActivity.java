package com.cabalry.map;

import android.support.v4.app.FragmentActivity;

import com.cabalry.util.Utility;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by conor on 11/01/15.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private MapUser mUser;
    private MapUser mAlarm;

    private HashMap<Integer, Marker> mMarkerMap = new HashMap<>();
    private Vector<MapUser> mUsers = new Vector<>();

    public void initializeMap(SupportMapFragment mapFragment) {
        // This is to call the onMapReady callback
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MarkerListener markerListener = new MarkerListener();
        mMap.setOnMarkerClickListener(markerListener);
        mMap.setOnInfoWindowClickListener(markerListener);
    }

    public void onUpdateLocation() {

    }

    public Marker createMarker(final MapUser user) {
        LatLng position = user.getPosition();
        String title = user.getName() +" "+ user.getCar() +" "+ user.getColor();

        return mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title));
    }

    public Marker getMarker(int id) { return mMarkerMap.get(id); }

    public void add(final MapUser user) {
        mUsers.add(user);
        mMarkerMap.put(user.getID(), createMarker(user));
    }

    public void update(final MapUser oldUsr, final MapUser newUsr) {
        oldUsr.updatePosition(newUsr.getPosition());
    }

    public void remove(final MapUser user) {
        mUsers.remove(user);
        mMarkerMap.remove(user.getID()).remove();
    }

    /**
     * Algorithm that safely removes, inserts and updates
     * new users with the current list users and map markers.
     */
    public void updateUsers(final Vector<MapUser> newUsers) {
        if(newUsers == null)
            throw new NullPointerException("newUsers can't be null!");

        Vector<MapUser> removeList = new Vector<>();

        /**
         * First pass compare and update
         */
        for(int i = 0; i < mUsers.size(); i++) {
            MapUser oldUsr = mUsers.get(i);
            boolean exit = false;
            int index = 0; // Only used if exit is true

            for(int j = 0; j < newUsers.size() && !exit; j++) {
                MapUser newUsr = newUsers.get(j);

                // Compare id's
                if(oldUsr.getID() == newUsr.getID()) {
                    update(oldUsr, newUsr); // ID already exists, update

                    exit = true;
                    index = j;
                }
            }

            // Check output
            if(exit)
                newUsers.remove(index); // Updated, remove from new
            else
                removeList.add(oldUsr); // Outdated, queue for remove
        }

        /**
         * Second pass remove
         */
        for(int i = 0; i < removeList.size(); i++) {
            remove(removeList.get(i));
        }

        /**
         * Third pass add
         */
        for(int i = 0; i < newUsers.size(); i++) {
            add(newUsers.get(i));
        }

        Utility.PrintCabalryUserList("Updated", mUsers);
    }

    /**
     * Class implementation for marker related callbacks
     */
    private class MarkerListener implements
            GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            //if(markerListener != null) {
            //    markerListener.onInfoClick(marker, markerLocations.get(marker));
            //}
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            //if(markerListener != null) {
            //    markerListener.onClick(marker, markerLocations.get(marker));
            //    return true;
            //}
            return false;
        }
    }
}