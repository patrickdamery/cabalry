package com.cabalry.custom;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 29/01/15.
 */
public class Util {

    public static float getBearing(LatLng a, LatLng b) {
        return (float)(Math.toDegrees(Math.atan2(b.latitude-a.latitude, b.longitude-a.longitude)) - 90);
    }

    public static boolean useCurrentLocation(LatLng current, LatLng previous, double threshold) {

        if(current.latitude > previous.latitude+threshold ||
                current.latitude < previous.latitude-threshold) {

            if(current.longitude > previous.longitude+threshold ||
                    current.longitude < previous.longitude-threshold) {

                return false;
            }
        }

        return true;
    }
}
