package com.cabalry.custom;

import android.graphics.Color;
import android.os.AsyncTask;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMap implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private UiSettings settings;

    private Map<Integer,Marker> markers;
    private Map<Integer,CabalryLocation> locations;
    private Map<Marker,CabalryLocation> markerLocations;

    private CabalryLocationListener markerListener;

    private Polyline currentRoute;

    public CabalryMap(MapFragment mapFragment) {

        if(mapFragment == null) {
            Logger.log("MapFragment can't be null!");
            throw new NullPointerException();
        }

        markers = new ConcurrentHashMap<Integer, Marker>();
        locations = new HashMap<Integer, CabalryLocation>();
        markerLocations = new HashMap<Marker, CabalryLocation>();

        this.mapFragment = mapFragment;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        settings = googleMap.getUiSettings();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(markerListener != null) {
                    markerListener.onClick(marker, markerLocations.get(marker));
                    return true;
                }
                return false;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(markerListener != null) {
                    markerListener.onInfoClick(marker, markerLocations.get(marker));
                }
            }
        });

        // Load map settings.
        loadSettings();
    }

    private void loadSettings() {

        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
        settings.setScrollGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setIndoorLevelPickerEnabled(false);
        settings.setCompassEnabled(false);
    }

    public void updateMap(ArrayList<CabalryLocation> updatedLocations) {

        for(Iterator<Integer> keys = markers.keySet().iterator(); keys.hasNext();) {
            int id = keys.next();

            boolean contains = false;
            for(CabalryLocation location : updatedLocations) {
                if(location.id == id) {

                    if(location.type != getLocation(id).type) {
                        getMarker(id).setIcon(CabalryLocation.getIcon(location.type));
                    }

                    moveMarker(location);
                    updatedLocations.remove(location);
                    contains = true;
                    break;
                }
            }

            if(!contains) {
                removeMarker(id);
            }
        }

        for(CabalryLocation marker : updatedLocations) {
            addMarker(marker);
        }
    }

    public void updateCamera(LatLng target, float zoom, float bearing, int transTime) {

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(target)
                .zoom(zoom)
                .bearing(bearing)
                .build();

        // Animate the change in camera view over some time.
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                transTime, null);
    }

    public void updateCamera(ArrayList<CabalryLocation> locations, int transTime) {

        if(locations.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (CabalryLocation location : locations) {
            builder.include(location.location);
        }
        LatLngBounds bounds = builder.build();

        int padding = 64; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // Animate the change in camera view over some time.
        googleMap.animateCamera(cu, transTime, null);
    }

    public Marker getMarker(int id) {
        return markers.get(id);
    }

    public CabalryLocation getLocation(int id) {
        return locations.get(id);
    }

    public Marker addMarker(final CabalryLocation location) {

        final Marker marker = googleMap.addMarker(
                CabalryLocation.getMarkerOptions(
                        location.id, location.type, location.location));

        setMarkerInfo(marker, location);

        locations.put(location.id, location);
        markerLocations.put(marker, location);
        return markers.put(location.id, marker);
    }

    private void setMarkerInfo(final Marker marker, final CabalryLocation location) {
        Logger.log("Step 1");
        new AsyncTask<Void, Void, String[]>(){

            @Override
            protected String[] doInBackground(Void... voids) {
                Logger.log("Step 2");
                String[] names = new String[2];
                Logger.log("Step 3");
                JSONObject result = DB.userInfo(location.id, Preferences.getID(), Preferences.getKey());
                Logger.log("Step 4");
                try {
                    Logger.log("Step 5");
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {
                        Logger.log("Step 6");
                        names[0] = result.getString("name");
                        names[1] = result.getString("color")+" "+result.getString("make");
                        Logger.log("Success get user info id = "+location.id);

                        return names;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String[] names) {
                if(names != null) {
                    marker.setTitle(names[0]);
                    marker.setSnippet(names[1]);
                }
            }

        }.execute();
    }

    public void removeMarker(int id) {
        Marker marker = markers.remove(id);
        locations.remove(id);
        markerLocations.remove(marker);
        marker.remove();
    }

    public void moveMarker(CabalryLocation location) {
        getMarker(location.id).setPosition(location.location);
        locations.put(location.id, location);
    }

    public void removeRoute() {
        if(currentRoute != null) {
            currentRoute.remove();
            currentRoute = null;
        }
    }

    public void setMarkerListener(CabalryLocationListener markerListener) { this.markerListener = markerListener; }
    public UiSettings getSettings() { return settings; }
    public GoogleMap getGoogleMap() { return googleMap; }
    public MapFragment getMapFragment() { return mapFragment; }
}