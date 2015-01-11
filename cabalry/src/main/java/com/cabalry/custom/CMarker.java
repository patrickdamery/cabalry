package com.cabalry.custom;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 11/01/15.
 */
public class CMarker {

    public static final float ALARM_HUE = BitmapDescriptorFactory.HUE_RED;
    public static final float NEARBY_HUE = BitmapDescriptorFactory.HUE_GREEN;
    public static final float USER_HUE = BitmapDescriptorFactory.HUE_BLUE;

    private String name;
    private int id;
    private float hue = NEARBY_HUE;
    private LatLng location;

    public CMarker(String name, int id, float hue, LatLng location) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.hue = hue;
    }

    // Getters.
    public String getName() {
        return name;
    }
    public int getID() {
        return id;
    }
    public LatLng getLocation() {
        return location;
    }
    public float getHue() {
        return  hue;
    }

    // Setters.
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
    }
    public void setHue(float hue) {
        this.hue = hue;
    }
}
