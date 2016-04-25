package com.cabalry.base;

import com.google.android.gms.maps.model.LatLng;

/**
 * MapUser
 */
public class MapUser {

    private final int mID;
    private final String mName;
    private final String mCar;
    private final String mColor;
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

    public LatLng getPosition() {
        return new LatLng(mLat, mLng);
    }

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String getCar() {
        return mCar;
    }

    public String getColor() {
        return mColor;
    }

    public UserType getType() {
        return mType;
    }

    public enum UserType {USER, NEARBY, ALERT, ALERTED}
}
