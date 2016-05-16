package com.cabalry.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.app.CabalryApp;
import com.cabalry.base.BindableService;
import com.google.android.gms.maps.model.LatLng;

import static com.cabalry.util.MathUtil.GetDistance;
import static com.cabalry.util.MessageUtil.MSG_LOCATION_UPDATE;
import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.GetLocation;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.StoreLocation;
import static com.cabalry.util.TasksUtil.CheckNetworkTask;
import static com.cabalry.util.TasksUtil.UpdateLocationTask;

/**
 * LocationUpdateService
 */
public class LocationUpdateService extends BindableService implements LocationUpdateListener {
    public static final double LOCATION_THRESHOLD = 10;
    private static final String TAG = "LocationUpdateService";
    private static final int WAIT_TIME = 600000;
    private static LocationUpdateManager mLocationUpdateManager;
    private long startTime = System.currentTimeMillis();
    private LatLng currentLocation, lastLocation;

    public static void resetProvider(LocationUpdateManager.UpdateProvider provider) {
        mLocationUpdateManager.stopLocationUpdates();
        mLocationUpdateManager.setUpdateProvider(provider);
        mLocationUpdateManager.startLocationUpdates();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mLocationUpdateManager = new LocationUpdateManager(this);
        mLocationUpdateManager.setUpdateListener(this);

        currentLocation = GetLocation(this);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateManager.resetProvider(manager);
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
        if (!CabalryApp.isApplicationRunning() && GetAlarmID(getApplicationContext()) == 0) {
            stopSelf();
        } else if (GetUserID(getApplicationContext()) == 0) {
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

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

    public static class GPSLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (isRunning()) {
                    final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        resetProvider(LocationUpdateManager.UpdateProvider.GPS);

                    } else {
                        resetProvider(LocationUpdateManager.UpdateProvider.GPS_NETWORK);
                    }
                }
            }
        }
    }
}