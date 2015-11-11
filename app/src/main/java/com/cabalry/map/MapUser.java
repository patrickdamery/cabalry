package com.cabalry.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 14/10/15.
 */
public class MapUser {

    private final int mID;
    private final String mName;
    private final String mCar;
    private final String mColor;

    public enum UserType { USER, NEARBY, ALARM }
    private UserType mType;
    private double mLat;
    private double mLng;

    public MapUser(final int id, String name, String car, String color,
                   double lat, double lng, UserType type) {
        mID = id;
        mName = name;
        mCar = car;
        mColor = color;
        mType = type;

        updatePosition(lat, lng);
    }

    public void updatePosition(final LatLng position) {
        mLat = position.latitude;
        mLng = position.longitude;
    }

    public void updatePosition(double lat, double lng) {
        mLat = lat;
        mLng = lng;
    }

    public LatLng getPosition() { return new LatLng(mLat, mLng); }
    public double getLat() { return mLat; }
    public double getLng() { return mLng; }

    public int getID() { return mID; }
    public String getName() { return mName; }
    public String getCar() { return mCar; }
    public String getColor() { return mColor; }
}
