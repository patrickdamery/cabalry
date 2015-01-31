package com.cabalry.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 29/01/15.
 */
public class MathUtil {

    public static final double LOCATION_THRESHOLD = 10;

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

    public static LatLng getPreferredLocation(LatLng a, LatLng b, LatLng c) {

        if(MathUtil.useCurrentLocation(a, b, MathUtil.LOCATION_THRESHOLD)) {
            if(MathUtil.useCurrentLocation(a, c, MathUtil.LOCATION_THRESHOLD)) {
                return a;
            } else {
                return c;
            }
        } else {
            return b;
        }
    }
}
