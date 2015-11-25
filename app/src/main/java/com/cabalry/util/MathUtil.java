package com.cabalry.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 24/11/15.
 */
public class MathUtil {

    public static double GetDistance(LatLng a, LatLng b) {
        if(a == null || b == null) return 0;

        int R = 6371000; // metres
        double lat1 = Math.toRadians(a.latitude);
        double lat2 = Math.toRadians(b.latitude);
        double dLng = Math.toRadians(b.longitude - a.longitude);

        return Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(dLng)) * R;
    }
}
