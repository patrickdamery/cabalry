package com.cabalry.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 13/10/15.
 */
public class CabalryMarker {

    private final int mID;
    private Marker mMarker;
    private double mLat;
    private double mLng;

    public enum UserType { USER, NEARBY, ALARM }
    private UserType mType;

    public CabalryMarker(final int id, double lat, double lng, UserType type) {
        mID = id;
        mType = type;
        updateLocation(lat, lng);
    }

    public void setMarker(final Marker marker) { mMarker = marker; }
    public void removeMarker() { mMarker.remove(); }

    public void updateLocation(double lat, double lng) {
        mLat = lat;
        mLng = lng;
    }

    public LatLng getLocation() { return new LatLng(mLat, mLng); }

    public int getID() { return mID; }
}
