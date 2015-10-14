package com.cabalry.map;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

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

    private CabalryMarker mUserMaker;
    private CabalryMarker mAlarmMaker;

    private HashMap<Integer, Marker> mMarkerMap;
    private Vector<CabalryUser> mPastUsers = new Vector<>();

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

    public void addMarker(final CabalryMarker marker) {

    }

    public Marker getMarker(int id) { return mMarkerMap.get(id); }

    public void updateUsers(final Vector<CabalryUser> newUsers) {

        int[] rmvArray = new int[mPastUsers.size()];
        for(int i = 0; i < mPastUsers.size(); i++) {
            boolean exit = false;

            for(int j = 0; j < newUsers.size() || exit; j++) {

                // Compare id's
                if(mPastUsers.get(i).getID() == newUsers.get(j).getID())
                    exit = true;
            }

            if(!exit)
                // ID is obsolete, will be removed later
                rmvArray[i] = mPastUsers.get(i).getID();
        }

        // Remove all obsolete markers
        for(int i = 0; i < rmvArray.length; i++)
            if(rmvArray[i] != 0)
                mPastUsers.remove(i);
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

    /**
     * Represents an asynchronous task that collects locations
     * of nearby cabalry members
     */
    private class CollectNearby extends AsyncTask<Void, Void, Vector<CabalryMarker>> {

        private final int mID;
        private final String mKey;

        public CollectNearby(int id, String key) {
            mID = id; mKey = key;
        }

        @Override
        protected Vector<CabalryMarker> doInBackground(Void... params) {
            Vector<CabalryMarker> nearbyMarkers = null;

            JSONObject result;
            boolean success = false;

            try {
                result = DB.getNearby(mID, mKey);
                try {
                    success = result.getBoolean(DB.SUCCESS);

                    if(success) {
                        nearbyMarkers = new Vector<>();

                        // Get locations array
                        JSONArray locations = result.getJSONArray(DB.LOCATION);

                        for(int i = 0; i < locations.length(); i++) {
                            JSONObject location = locations.getJSONObject(i);

                            int id = location.getInt(DB.USER_ID);
                            double lat = location.getDouble(DB.LATITUDE);
                            double lng = location.getDouble(DB.LONGITUDE);

                            // Add location to list
                            nearbyMarkers.add(new CabalryMarker(id, lat, lng, null));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return nearbyMarkers;
        }
    }
}