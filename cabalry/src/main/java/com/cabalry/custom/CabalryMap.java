package com.cabalry.custom;

import com.cabalry.db.GlobalKeys;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMap implements OnMapReadyCallback {

    public static final double LOCATION_THRESHOLD = 10;

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private UiSettings settings;

    private Map<Integer,Marker> markers;
    private Map<Integer,CabalryLocation> locations;

    private MarkerListener markerListener;
    private Marker lastMarkerClicked;

    public CabalryMap(MapFragment mapFragment) {

        if(mapFragment == null) {
            Logger.log("MapFragment can't be null!");
            throw new NullPointerException();
        }

        markers = new ConcurrentHashMap<Integer, Marker>();
        locations = new HashMap<Integer, CabalryLocation>();

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
                /*if(markerListener != null) {

                    int id = -1;
                    Iterator it = markers.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry)it.next();

                        if(pairs.getValue() == marker) id = (Integer)pairs.getKey();
                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    if(id > 0) {
                        if(lastMarkerClicked == marker) {
                            lastMarkerClicked = marker;
                            return markerListener.onDoubleClick(marker, getLocation(id));
                        } else {
                            lastMarkerClicked = marker;
                            return markerListener.onClick(marker, getLocation(id));
                        }
                    }

                    return true;
                }*/
                return true;
            }
        });

        // Load cabalry map settings.
        //loadSettings();
        resetSettings(false);
    }

    public void updateMap(final ArrayList<CabalryLocation> updatedLocations) {

        for(Iterator<Integer> keys = markers.keySet().iterator(); keys.hasNext();) {
            int id = keys.next();

            boolean contains = false;
            for(CabalryLocation location : updatedLocations) {
                if(location.id == id) {

                    if(location.type != getLocation(id).type) {
                        getMarker(id).setIcon(BitmapDescriptorFactory.defaultMarker(
                                CabalryLocationType.getHUE(location.type)));
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

    public void updateCamera(final ArrayList<CabalryLocation> locations, int transTime) {

        if(locations.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (CabalryLocation location : locations) {
            builder.include(location.location);
        }
        LatLngBounds bounds = builder.build();

        int padding = 20; // offset from edges of the map in pixels
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

    private Marker addMarker(CabalryLocation location) {
        Marker marker = googleMap.addMarker(
                CabalryLocationType.getMarkerOptions(
                        location.id, location.type, location.location));
        locations.put(location.id, location);
        return markers.put(location.id, marker);
    }

    private void removeMarker(int id) {
        locations.remove(id);
        markers.remove(id).remove();
    }

    private void moveMarker(CabalryLocation location) {
        getMarker(location.id).setPosition(location.location);
        locations.put(location.id, location);
    }

    public void setMarkerListener(MarkerListener markerListener) { this.markerListener = markerListener; }

    public void resetSettings(boolean b) {

        // Default cabalry map settings.
        settings.setZoomControlsEnabled(b);
        settings.setZoomGesturesEnabled(b);
        settings.setScrollGesturesEnabled(b);
        settings.setTiltGesturesEnabled(b);
        settings.setRotateGesturesEnabled(b);
        settings.setMapToolbarEnabled(b);
        settings.setIndoorLevelPickerEnabled(b);
        settings.setCompassEnabled(b);
    }

    public void loadSettings() {

        settings.setZoomControlsEnabled(Preferences.getBoolean(GlobalKeys.ZOOM_CONTROLS));
        settings.setZoomGesturesEnabled(Preferences.getBoolean(GlobalKeys.ZOOM_GESTURES));
        settings.setScrollGesturesEnabled(Preferences.getBoolean(GlobalKeys.SCROLL_GESTURES));
        settings.setTiltGesturesEnabled(Preferences.getBoolean(GlobalKeys.TILT_GESTURES));
        settings.setRotateGesturesEnabled(Preferences.getBoolean(GlobalKeys.ROTATE_GESTURES));
        settings.setMapToolbarEnabled(Preferences.getBoolean(GlobalKeys.MAP_TOOLBAR));
        settings.setIndoorLevelPickerEnabled(Preferences.getBoolean(GlobalKeys.INDOOR_LEVEL_PICKER));
        settings.setCompassEnabled(Preferences.getBoolean(GlobalKeys.COMPASS));
    }

    public void saveSettings() {

        Preferences.setBoolean(GlobalKeys.ZOOM_CONTROLS, settings.isZoomControlsEnabled());
        Preferences.setBoolean(GlobalKeys.ZOOM_GESTURES, settings.isZoomGesturesEnabled());
        Preferences.setBoolean(GlobalKeys.SCROLL_GESTURES, settings.isScrollGesturesEnabled());
        Preferences.setBoolean(GlobalKeys.TILT_GESTURES, settings.isTiltGesturesEnabled());
        Preferences.setBoolean(GlobalKeys.ROTATE_GESTURES, settings.isRotateGesturesEnabled());
        Preferences.setBoolean(GlobalKeys.MAP_TOOLBAR, settings.isMapToolbarEnabled());
        Preferences.setBoolean(GlobalKeys.INDOOR_LEVEL_PICKER, settings.isIndoorLevelPickerEnabled());
        Preferences.setBoolean(GlobalKeys.COMPASS, settings.isCompassEnabled());
    }

    public UiSettings getSettings() { return settings; }
    public GoogleMap getGoogleMap() { return googleMap; }
    public MapFragment getMapFragment() { return mapFragment; }
}