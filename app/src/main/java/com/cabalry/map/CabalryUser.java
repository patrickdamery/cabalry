package com.cabalry.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 14/10/15.
 */
public class CabalryUser {

    private final int mID;
    private double mLat;
    private double mLng;

    public enum UserType { USER, NEARBY, ALARM }
    private UserType mType;

    public CabalryUser(final int id, double lat, double lng, UserType type) {
        mID = id;
        mType = type;
        updateLocation(lat, lng);
    }

    public void updateLocation(double lat, double lng) {
        mLat = lat;
        mLng = lng;
    }

    public LatLng getLocation() { return new LatLng(mLat, mLng); }

    public int getID() { return mID; }
}
