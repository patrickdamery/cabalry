package com.cabalry.custom;

import com.cabalry.utils.Logger;
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

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private UiSettings settings;

    private Map<Integer,Marker> markers;
    private Map<Integer,CabalryLocation> locations;

    private CabalryLocationListener markerListener;
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
                if(markerListener != null) {
                    return true;
                }
                return false;
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
                        getMarker(id).setIcon(BitmapDescriptorFactory.defaultMarker(
                                CabalryLocation.getHUE(location.type)));
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
                CabalryLocation.getMarkerOptions(
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

    public void setMarkerListener(CabalryLocationListener markerListener) { this.markerListener = markerListener; }
    public UiSettings getSettings() { return settings; }
    public GoogleMap getGoogleMap() { return googleMap; }
    public MapFragment getMapFragment() { return mapFragment; }
}