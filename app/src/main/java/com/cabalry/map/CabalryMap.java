package com.cabalry.map;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.cabalry.CabalryUtility;
import com.cabalry.db.DB;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private CabalryUser mUser;
    private CabalryUser mAlarm;

    private HashMap<Integer, Marker> mMarkerMap;
    private Vector<CabalryUser> mUsers = new Vector<>();

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

    public Marker createMarker(final CabalryUser user) {
        return mMap.addMarker(new MarkerOptions()
                .position(user.getPosition())
                .title("Marker"));
    }

    public Marker getMarker(int id) { return mMarkerMap.get(id); }

    public void add(final CabalryUser user) {
        mUsers.add(user);
        //mMarkerMap.put(user.getID(), createMarker(user));
    }

    public void update(final CabalryUser oldUsr, final CabalryUser newUsr) {
        oldUsr.updatePosition(newUsr.getPosition());
    }

    public void remove(final CabalryUser user) {
        mUsers.remove(user);
        //mMarkerMap.remove(user.getID()).remove();
    }

    /**
     * Algorithm that safely removes, inserts and updates
     * new users with the current list users and map markers.
     */
    public void updateUsers(final Vector<CabalryUser> newUsers) {
        if(newUsers == null)
            throw new NullPointerException("newUsers can't be null!");

        Vector<CabalryUser> removeList = new Vector<>();

        /**
         * First pass compare and update
         */
        for(int i = 0; i < mUsers.size(); i++) {
            CabalryUser oldUsr = mUsers.get(i);
            boolean exit = false;
            int index = 0; // Only used if exit is true

            for(int j = 0; j < newUsers.size() && !exit; j++) {
                CabalryUser newUsr = newUsers.get(j);

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

        CabalryUtility.PrintCabalryUserList(mUsers);
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