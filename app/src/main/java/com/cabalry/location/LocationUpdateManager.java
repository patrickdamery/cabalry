package com.cabalry.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import static com.cabalry.util.PreferencesUtil.TWO_MINUTES;

/**
 * LocationUpdateManager
 */
public class LocationUpdateManager implements LocationListener {

    private static LocationUpdateManager mInstance;
    private UpdateProvider mUpdateProvider = UpdateProvider.NETWORK;

    private LocationManager mLocationManager;
    private Location mCurrentBestLocation;

    private long mMinTime = 5000;
    private float mMinDistance = 5;

    private LocationUpdateListener mUpdateListener;

    public LocationUpdateManager(Context context) {
        // Acquire a reference to the system Location Manager.
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix.
     *
     * @param location            The new Location that you want to evaluate.
     * @param currentBestLocation The current Location fix, to which you want to compare the new one.
     */
    public static boolean IsBetterLocation(Location location, Location currentBestLocation) {
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
        boolean isFromSameProvider = IsSameProvider(location.getProvider(),
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

    /**
     * Checks whether two providers are the same
     */
    public static boolean IsSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public void setUpdateProvider(UpdateProvider updateProvider) {
        mUpdateProvider = updateProvider;
    }

    public void setUpdateListener(LocationUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public void startLocationUpdates() {

        switch (mUpdateProvider) {
            case NETWORK:
                mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
                break;

            case GPS:
                mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
                break;

            case GPS_NETWORK:
                mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
                mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
                break;
        }
    }

    public void stopLocationUpdates() {
        mLocationManager.removeUpdates(this);
    }

    public void dispose() {
        stopLocationUpdates();
        mUpdateListener = null;
    }

    public void resetProvider(LocationManager manager) {
        stopLocationUpdates();

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                setUpdateProvider(LocationUpdateManager.UpdateProvider.GPS_NETWORK);
            } else {
                setUpdateProvider(LocationUpdateManager.UpdateProvider.GPS);
            }

        } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            setUpdateProvider(LocationUpdateManager.UpdateProvider.NETWORK);
        }

        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        if (IsBetterLocation(location, mCurrentBestLocation)) {
            mCurrentBestLocation = location;
        }

        if (mUpdateListener != null)
            mUpdateListener.onUpdateLocation(mCurrentBestLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public enum UpdateProvider {NETWORK, GPS, GPS_NETWORK}
}
