package com.cabalry.map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cabalry.*;
import com.cabalry.R;
import com.cabalry.db.DB;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private CabalryMarker mUserMaker;
    private CabalryMarker mAlarmMaker;
    private Vector<CabalryMarker> mMarkers = new Vector<>();

    public void setupMap(SupportMapFragment mapFragment) {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                //if(markerListener != null) {
                //    markerListener.onClick(marker, markerLocations.get(marker));
                //    return true;
                //}
                return false;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //if(markerListener != null) {
                //    markerListener.onInfoClick(marker, markerLocations.get(marker));
                //}
            }
        });

        // Load map settings.
        //loadSettings();
    }

    public void onUpdateLocation() {

    }

    public void addMarker(final CabalryMarker marker) {
        mMarkers.add(marker);
    }

    public void updateMarkers() {

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