package com.cabalry.custom;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by conor on 18/01/15.
 */
public class LocationTracerProgram {

    public static final int NETWORK = 0;
    public static final int GPS = 1;
    public static final int GPS_NETWORK = 2;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private LocationTracerListener tracerListener;

    private LocationManager locationManager;
    private LocationListener gpsListener;
    private LocationListener netListener;

    private int level = -1;

    private static Location currentBestLocation;

    public LocationTracerProgram(Context context, LocationTracerListener tracerListener) {
        // Acquire a reference to the system Location Manager.
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if(tracerListener == null) {
            throw new NullPointerException();
        }
        this.tracerListener = tracerListener;

        // Define a listener that responds to location updates.
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                try {
                    updateLocation(location);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        gpsListener = locationListener;
        netListener = locationListener;
    }

    private void updateLocation(Location location) {
        if(isBetterLocation(location, currentBestLocation)) {
            currentBestLocation = location;
        }
        tracerListener.onUpdateLocation(getCurrentBestLocation());
    }

    public void startLocationUpdates(int level, long minTime, float minDistance) {

        this.level = level;

        switch(level) {
            case LocationTracerProgram.NETWORK:
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, netListener);
            break;

            case LocationTracerProgram.GPS:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            break;

            case LocationTracerProgram.GPS_NETWORK:
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, netListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            break;
        }

        tracerListener.onStartLocationTracer();
    }

    public void stopLocationUpdates() {
        if(level > 0) {

            tracerListener.onStopLocationTracer();

            // Remove the listener you previously added.
            switch(level) {
                case LocationTracerProgram.NETWORK:
                    locationManager.removeUpdates(netListener);
                break;

                case LocationTracerProgram.GPS:
                    locationManager.removeUpdates(gpsListener);
                break;

                case LocationTracerProgram.GPS_NETWORK:
                    locationManager.removeUpdates(gpsListener);
                    locationManager.removeUpdates(netListener);
                break;
            }

            level = -1;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix.
     * @param location  The new Location that you want to evaluate.
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one.
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location.
            return true;
        }

        // Check whether the new location fix is newer or older.
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate.
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider.
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy.
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public Location getCurrentBestLocation() {
        return currentBestLocation;
    }
}
