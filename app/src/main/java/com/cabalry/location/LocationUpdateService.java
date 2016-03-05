package com.cabalry.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;

import com.cabalry.base.BindableService;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.MathUtil.*;
import static com.cabalry.util.MessageUtil.*;
import static com.cabalry.db.DataBase.*;

/**
 * LocationUpdateService
 */
public class LocationUpdateService extends BindableService implements LocationUpdateListener {
    private static final String TAG = "LocationUpdateService";

    public static final double LOCATION_THRESHOLD = 10;
    private static final int WAIT_TIME = 600000;
    private long startTime = System.currentTimeMillis();

    private LocationUpdateManager mLocationUpdateManager;
    private LatLng currentLocation, lastLocation;

    @Override
    public void onCreate() {
        mLocationUpdateManager = new LocationUpdateManager(this);
        mLocationUpdateManager.addUpdateListener(this);

        currentLocation = GetLocation(this);
        updateDBLocation();
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

        if (GetDistance(currentLocation, lastLocation) < LOCATION_THRESHOLD) {
            if (System.currentTimeMillis() - startTime >= WAIT_TIME) {
                startTime = System.currentTimeMillis();
                updateDBLocation();
            }

        } else updateDBLocation();

        Log.d(TAG, "onUpdateLocation(): " + location.toString());
    }

    private void updateDBLocation() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... voids) {

                JSONObject result = UpdateUserLocation(currentLocation.latitude, currentLocation.longitude,
                        GetUserID(LocationUpdateService.this), GetUserKey(LocationUpdateService.this));

                try {
                    result.getBoolean(REQ_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateManager.resetProvider(manager);

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    // Handler of incoming messages from clients.
    private class MessengerHandler extends BindableService.BaseMessengerHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected Handler getMessengerHandler() {
        return new MessengerHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationUpdateManager.dispose();
    }
}