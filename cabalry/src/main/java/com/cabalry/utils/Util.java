package com.cabalry.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by conor on 29/01/15.
 */
public class Util {

    public static final double LOCATION_THRESHOLD = 10;

    public static boolean hasActiveInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

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

        if(Util.useCurrentLocation(a, b, Util.LOCATION_THRESHOLD)) {
            if(Util.useCurrentLocation(a, c, Util.LOCATION_THRESHOLD)) {
                return a;
            } else {
                return c;
            }
        } else {
            return b;
        }
    }
}
