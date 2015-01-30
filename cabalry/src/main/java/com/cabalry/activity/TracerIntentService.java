package com.cabalry.activity;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import com.cabalry.custom.Logger;
import com.cabalry.custom.TraceLevel;
import com.cabalry.custom.TracerListener;
import com.cabalry.custom.TracerProgram;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by conor on 29/01/15.
 */
public class TracerIntentService extends Service {

    private TracerProgram tracerProgram;

    private static LatLng currentLocation = new LatLng(0, 0);
    private static LatLng previousLocation = currentLocation;

    @Override
    public void onCreate() {

        TracerListener tracerListener = new TracerListener() {
            @Override
            public void onUpdateLocation(Location location) {

                previousLocation = currentLocation;
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    public Void doInBackground(Void... voids) {

                        //currentLocation = Util.getPreferredLocation(getCurrentLocation(), getStoredLocation(), getPreviousLocation());

                        // Stores current user location.
                        /*Preferences.setFloat(GlobalKeys.LAT, (float) currentLocation.latitude);
                        Preferences.setFloat(GlobalKeys.LNG, (float) currentLocation.longitude);

                        JSONObject updateResult = DB.updateLocation(currentLocation.latitude, currentLocation.longitude,
                                Preferences.getID(), Preferences.getKey());

                        try {
                            if (!updateResult.getBoolean(GlobalKeys.SUCCESS)) {
                                Logger.log("Could not update location on server!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        return null;
                    }
                }.execute();
            }
            @Override
            public void onStartLocationTracer() { }
            @Override
            public void onStopLocationTracer() { }
        };

        tracerProgram = new TracerProgram(this, tracerListener);
        tracerProgram.startLocationUpdates(TraceLevel.GPS_NETWORK, 0, 0);

        currentLocation = new LatLng(0, 0);
        previousLocation = currentLocation;
    }

    @Override
    public void onDestroy() {
        tracerProgram.stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private LatLng getStoredLocation() {
        return null;
        /*JSONObject location = DB.getLocation(Preferences.getID(),
                Preferences.getID(), Preferences.getKey());
        try {
            if(!location.getBoolean(GlobalKeys.SUCCESS)) {
                Logger.log("Alerted location not retrieved from server!");
            }
            return new LatLng(location.getDouble(GlobalKeys.LAT),
                    location.getDouble(GlobalKeys.LNG));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new LatLng(Preferences.getFloat(GlobalKeys.LAT), Preferences.getFloat(GlobalKeys.LNG));*/
    }

    public static LatLng getCurrentLocation() {
        return currentLocation;
    }

    public static LatLng getPreviousLocation() {
        return previousLocation;
    }
}