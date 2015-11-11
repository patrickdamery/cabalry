package com.cabalry.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Vector;

import static com.cabalry.util.Utility.*;

/**
 * Created by conor on 18/01/15.
 */
public class LocationUpdateManager implements LocationListener {

    public enum UpdateProvider { NETWORK, GPS, GPS_NETWORK }
    private UpdateProvider mUpdateProvider = UpdateProvider.NETWORK;

    private LocationManager mLocationManager;
    private Location mCurrentBestLocation;

    private long mMinTime = 0;
    private float mMinDistance = 0;

    private static Vector<LocationUpdateListener> mLocationUpdateListenerVector = new Vector<>();

    public LocationUpdateManager(Context context) {
        // Acquire a reference to the system Location Manager.
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setUpdateProvider(UpdateProvider updateProvider) {
        mUpdateProvider = updateProvider;
    }

    public static void registerUpdateListener(LocationUpdateListener updateListener) {
        mLocationUpdateListenerVector.add(updateListener);
    }

    public void startLocationUpdates() {

        switch(mUpdateProvider) {
            case NETWORK :
                mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
            break;

            case GPS :
                mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
            break;

            case GPS_NETWORK :
                mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
                mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, mMinTime, mMinDistance, this);
            break;
        }
    }

    public void stopLocationUpdates() { mLocationManager.removeUpdates(this); }

    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        if (IsBetterLocation(location, mCurrentBestLocation)) {
            mCurrentBestLocation = location;
        }

        if(mLocationUpdateListenerVector != null && !mLocationUpdateListenerVector.isEmpty())
            for(LocationUpdateListener updateListener : mLocationUpdateListenerVector)
                updateListener.onUpdateLocation(mCurrentBestLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
}
