package com.cabalry.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.base.BindableService;
import com.google.android.gms.maps.model.LatLng;

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.MathUtil.*;
import static com.cabalry.util.MessageUtil.*;
import static com.cabalry.util.TasksUtil.*;

/**
 * LocationUpdateService
 */
public class LocationUpdateService extends BindableService implements LocationUpdateListener {
    private static final String TAG = "LocationUpdateService";

    public static final double LOCATION_THRESHOLD = 10;
    private static final int WAIT_TIME = 600000;
    private long startTime = System.currentTimeMillis();

    private static LocationUpdateManager mLocationUpdateManager;
    private LatLng currentLocation, lastLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationUpdateManager = new LocationUpdateManager(this);
        mLocationUpdateManager.setUpdateListener(this);

        currentLocation = GetLocation(this);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateManager.resetProvider(manager);
    }

    public static void updateListenerProvider(LocationUpdateManager.UpdateProvider provider) {
        mLocationUpdateManager.stopLocationUpdates();
        mLocationUpdateManager.setUpdateProvider(provider);
        mLocationUpdateManager.startLocationUpdates();
    }

    @Override
    public void onUpdateLocation(Location location) {
        lastLocation = currentLocation;
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Notify bounded activity
        Bundle data = new Bundle();
        data.putDouble("lat", location.getLatitude());
        data.putDouble("lng", location.getLongitude());
        sendMessageToActivity(MSG_LOCATION_UPDATE, data);

        // Store location.
        StoreLocation(LocationUpdateService.this, currentLocation);

        if (System.currentTimeMillis() - startTime >= WAIT_TIME) {
            startTime = System.currentTimeMillis();
            updateLocation();

        } else if (GetDistance(currentLocation, lastLocation) < LOCATION_THRESHOLD) {
            if (System.currentTimeMillis() - startTime >= WAIT_TIME) {
                startTime = System.currentTimeMillis();
                updateLocation();
            }

        } else updateLocation();

        Log.d(TAG, "onUpdateLocation(): " + location.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateManager.resetProvider(manager);

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationUpdateManager.dispose();
    }

    private void updateLocation() {
        new CheckNetworkTask(getApplicationContext()) {

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    new UpdateLocationTask(getApplicationContext(), currentLocation).execute();

                } else {
                    // handle no network
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_no_network),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}