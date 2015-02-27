package com.cabalry.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.*;
import com.cabalry.custom.*;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Util;
import com.cabalry.utils.Preferences;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by conor on 29/01/15.
 */
public class LocationTracerService extends Service {

    private LocationTracerProgram tracerProgram;
    private static LatLng currentLocation = new LatLng(0, 0);
    private static LatLng previousLocation = new LatLng(0, 0);

    private static boolean firstUpdate = false;

    private static final int WAIT_TIME = 1000*60*10;
    private long startTime = System.currentTimeMillis();

    @Override
    public void onCreate() {
        // Initialize preferences.
        Preferences.initialize(getApplicationContext());

        Logger.log("Device has GPS "+Util.hasGPSDevice(getApplicationContext()));

        final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationTracerListener tracerListener = new LocationTracerListener() {
            @Override
            public void onUpdateLocation(Location location) {
                previousLocation = currentLocation;
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Store location.
                Preferences.setStoredLocation(currentLocation);

                if(!firstUpdate) {
                    updateDBLocation();
                    firstUpdate = true;

                } else if(Util.getDistance(currentLocation, previousLocation) < Util.LOCATION_THRESHOLD) {
                    if(System.currentTimeMillis() - startTime >= WAIT_TIME) {
                        startTime = System.currentTimeMillis();
                        updateDBLocation();
                    }

                } else {
                    updateDBLocation();
                }
            }

            @Override
            public void onStartLocationTracer() { }

            @Override
            public void onStopLocationTracer() { }
        };

        tracerProgram = new LocationTracerProgram(this, tracerListener);

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Logger.log("GPS");
            tracerProgram.startLocationUpdates(LocationTracerProgram.GPS, 0, 0);
        } else {
            Logger.log("GPS_NET");
            tracerProgram.startLocationUpdates(LocationTracerProgram.GPS_NETWORK, 0, 0);
        }
    }

    private void updateDBLocation() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... voids) {

                JSONObject result = DB.updateLocation(currentLocation.latitude, currentLocation.longitude,
                        Preferences.getID(), Preferences.getKey());

                try {
                    if(!result.getBoolean(GlobalKeys.SUCCESS)) {
                        Logger.log("Could not update db location!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Logger.log("GPS");
            tracerProgram.startLocationUpdates(LocationTracerProgram.GPS, 0, 0);
        } else {
            Logger.log("GPS_NET");
            tracerProgram.startLocationUpdates(LocationTracerProgram.GPS_NETWORK, 0, 0);
        }

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        tracerProgram.stopLocationUpdates();
    }

    public static LatLng getCurrentLocation() {
        return currentLocation;
    }
    public static LatLng getPreviousLocation() {
        return previousLocation;
    }
    public static LatLng getStoredLocation() {
        return Preferences.getStoredLocation();
    }
}