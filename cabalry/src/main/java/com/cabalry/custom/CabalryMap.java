package com.cabalry.custom;

import com.cabalry.db.GlobalKeys;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMap implements OnMapReadyCallback {

    public static final double LOCATION_THRESHOLD = 100;

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private UiSettings settings;

    private Map<Integer,Marker> markers;

    private MarkerListener markerListener;
    private Marker lastMarkerClicked;

    public CabalryMap(MapFragment mapFragment) {

        if(mapFragment == null) {
            System.err.println("MapFragment can't be null!");
            throw new NullPointerException();
        }

        markers = new ConcurrentHashMap<Integer, Marker>();

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
                    if(lastMarkerClicked == marker) {
                        return markerListener.onDoubleClick(marker, "", 0);
                    } else {
                        return markerListener.onClick(marker);
                    }
                }

                lastMarkerClicked = marker;
                return false;
            }
        });

        // Load cabalry map settings.
        //loadSettings();
        resetSettings(true);
    }

    public void updateMap(final ArrayList<CabalryLocation> updatedLocations) {

        for(Iterator<Integer> keys = markers.keySet().iterator(); keys.hasNext();) {
            Integer id = keys.next();

            boolean contains = false;
            for(CabalryLocation marker : updatedLocations) {
                if(marker.id == id) {
                    moveMarker(marker.location, marker.id);
                    updatedLocations.remove(marker);
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

    public Marker getMarker(int id) {
        return markers.get(id);
    }

    private Marker addMarker(CabalryLocation cabalryLocation) {
        Marker marker = googleMap.addMarker(
                CabalryLocationType.getMarkerOptions(
                        cabalryLocation.id, cabalryLocation.type, cabalryLocation.location));
        return markers.put(cabalryLocation.id, marker);
    }

    private void removeMarker(int id) {
        markers.remove(id).remove();
    }

    private void moveMarker(LatLng newPosition, int id) {
        getMarker(id).setPosition(newPosition);
    }

    private void fixCameraScale(LatLng target, LatLng[] positions) { }

    private void setMarkerListener(MarkerListener markerListener) {
        this.markerListener = markerListener;
    }

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