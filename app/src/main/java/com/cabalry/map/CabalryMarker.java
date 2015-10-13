package com.cabalry.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 13/10/15.
 */
public class CabalryMarker {

    public enum State {  }
    private State mState;

    public final int mID;
    public double latitude;
    public double longitude;

    private Marker mMarker;

    public CabalryMarker(final int id, double lat, double lng, State state) {
        mID = id;
        latitude = lat;
        longitude = lng;

        mState = state;
    }

    public void setMarker(final Marker marker) { mMarker = marker; }
}
