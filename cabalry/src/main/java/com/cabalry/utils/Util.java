package com.cabalry.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by conor on 29/01/15.
 */
public class Util {

    public static final double LOCATION_THRESHOLD = 10;

    /*public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Logger.log("Error checking internet connection");
            }
        } else {
            Logger.log("No network available!");
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/

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
