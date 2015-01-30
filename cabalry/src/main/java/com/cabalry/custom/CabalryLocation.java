package com.cabalry.custom;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by conor on 19/01/15.
 */
public class CabalryLocation {

    public static final int USER = 0;
    public static final int USER_NEARBY = 1;
    public static final int USER_WARNING_LOW = 2;
    public static final int USER_WARNING_HIGH = 3;
    public static final int USER_ALERT = 4;
    public static final int USER_ALERTED = 5;

    public int id;
    public LatLng location;
    public int type;

    public CabalryLocation(int id, LatLng location, int type) {
        set(id, location, type);
    }

    public void set(int id, LatLng location, int type) {
        this.id = id;
        this.location = location;
        this.type = type;
    }

    public static MarkerOptions getMarkerOptions(int id, int type, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .snippet("ID:"+id)
                .draggable(false)
                .title(getTitle(type))
                .icon(BitmapDescriptorFactory.defaultMarker(getHUE(type)));

        return markerOptions;
    }

    public static float getHUE(int type) {
        switch(type) {
            case USER:
                return BitmapDescriptorFactory.HUE_AZURE;

            case USER_NEARBY:
                return BitmapDescriptorFactory.HUE_GREEN;

            case USER_WARNING_LOW:
                return BitmapDescriptorFactory.HUE_YELLOW;

            case USER_WARNING_HIGH:
                return BitmapDescriptorFactory.HUE_ORANGE;

            case USER_ALERT:
                return BitmapDescriptorFactory.HUE_RED;

            case USER_ALERTED:
                return BitmapDescriptorFactory.HUE_VIOLET;
        }

        return 0;
    }

    public static String getTitle(int type) {
        switch(type) {
            case USER:
                return "User";

            case USER_NEARBY:
                return "NearBy";

            case USER_WARNING_LOW:
                return "Warning";

            case USER_WARNING_HIGH:
                return "Warning!";

            case USER_ALERT:
                return "Alert!!";

            case USER_ALERTED:
                return "Alerted";
        }

        return "";
    }
}
