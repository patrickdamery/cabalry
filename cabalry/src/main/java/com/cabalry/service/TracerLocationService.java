package com.cabalry.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
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
public class TracerLocationService extends Service {

    private TracerLocationProgram tracerProgram;
    private static LatLng currentLocation = new LatLng(0, 0);
    private static LatLng previousLocation = new LatLng(0, 0);

    private boolean running = false;

    @Override
    public void onCreate() {
        if(running) return;
        running = true;

        // Initialize preferences.
        Preferences.initialize(getApplicationContext());

        TracerLocationListener tracerListener = new TracerLocationListener() {
            @Override
            public void onUpdateLocation(Location location) {
                previousLocation = currentLocation;
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Store location.
                Preferences.setStoredLocation(currentLocation);

                if(Util.getDistance(currentLocation, previousLocation) < Util.LOCATION_THRESHOLD) return;

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
            public void onStartLocationTracer() { }

            @Override
            public void onStopLocationTracer() { }
        };

        tracerProgram = new TracerLocationProgram(this, tracerListener);
        tracerProgram.startLocationUpdates(TracerLocationProgram.GPS_NETWORK, 0, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        running = false;
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