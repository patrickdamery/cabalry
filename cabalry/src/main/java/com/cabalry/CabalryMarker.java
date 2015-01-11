package com.cabalry;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryMarker {

    private String name;
    private int id;
    private LatLng location;

    private Marker marker;

    public CabalryMarker() {
        this("", 0, null);
    }

    public CabalryMarker(String name, int id, LatLng location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setLocation(double lat, double lng) {
        setLocation(new LatLng(lat, lng));
    }

    public void setLocation(LatLng location) {
        this.location = location;
        if(marker != null) {
            marker.setPosition(location);
        }
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
