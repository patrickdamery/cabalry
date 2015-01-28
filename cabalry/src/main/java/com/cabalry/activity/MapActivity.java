package com.cabalry.activity;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.custom.*;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by conor on 11/01/15.
 */
public class MapActivity extends Activity {

    private Button bNearby;
    private Button bAlarm;

    private TracerProgram tracerProgram;
    private CabalryMap cabalryMap;

    private Location userLocation;

    private boolean isAlarmEnabled = false;

    private boolean isNearbyEnabled = false;
    private boolean hasFinishedUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bNearby = (Button) findViewById(R.id.bNearby);
        bNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNearbyEnabled = !isNearbyEnabled;

                if(isNearbyEnabled) {
                    bNearby.setText("Hide");
                } else {
                    bNearby.setText("NearBy");
                }

                updateLocations();
            }
        });

        bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAlarmEnabled = !isAlarmEnabled;

                if(isAlarmEnabled) {
                    startAlarm();
                    bAlarm.setText("Stop");
                } else {
                    stopAlarm();
                    bAlarm.setText("Alarm");
                }
            }
        });

        TracerListener tracerListener = new TracerListener() {
            @Override
            public void onUpdateLocation(Location location) {
                userLocation = location;
                updateLocations();
            }

            @Override
            public void onStartLocationTracer() { }
            @Override
            public void onStopLocationTracer() { }
        };

        tracerProgram = new TracerProgram(this, tracerListener);
        tracerProgram.startLocationUpdates(TraceLevel.GPS_NETWORK, 0, 0);

        cabalryMap = new CabalryMap((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map));
    }

    private void startAlarm() {

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void ... voids) {

                    JSONObject result = DB.alarm(getID(), getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS) == true) {
                        System.err.println(result.getInt(GlobalKeys.ALARM_ID)+"");
                        Preferences.setInt(GlobalKeys.ALARM_ID, result.getInt(GlobalKeys.ALARM_ID));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void stopAlarm() {

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void ... voids) {

                JSONObject result = DB.stopAlarm(Preferences.getInt(GlobalKeys.ALARM_ID), Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS) == true) {
                        Preferences.setInt(GlobalKeys.ALARM_ID, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        tracerProgram.stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        tracerProgram.stopLocationUpdates();
        super.onPause();
    }

    private void updateLocations() {

        if(!hasFinishedUpdate) return;
        hasFinishedUpdate = false;

        final LatLng currentLocation = getPreferredLocation();
        final ArrayList<CabalryLocation> updatedLocation = new ArrayList<CabalryLocation>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void ... voids) {

                // Stores current user location.
                Preferences.setFloat(GlobalKeys.LAT, (float)currentLocation.latitude);
                Preferences.setFloat(GlobalKeys.LNG, (float)currentLocation.longitude);
                DB.updateLocation(currentLocation.latitude, currentLocation.longitude, getID(), getKey());

                // Add user location.
                updatedLocation.add(new CabalryLocation(getID(), currentLocation, CabalryLocationType.USER));

                // Add nearby locations.
                if(isNearbyEnabled) {

                    JSONObject result = null;
                    try {
                        result = DB.nearby(getID(), getKey());
                        try {
                            boolean success = result.getBoolean(GlobalKeys.SUCCESS);
                            if (success) {
                                JSONArray list = result.getJSONArray(GlobalKeys.LOCATION);
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject c = list.getJSONObject(i);

                                    System.err.println(
                                            new LatLng(c.getDouble(GlobalKeys.LAT),
                                                    c.getDouble(GlobalKeys.LNG)).toString());

                                    updatedLocation.add(new CabalryLocation(
                                            c.getInt(GlobalKeys.ID),
                                            new LatLng(c.getDouble(GlobalKeys.LAT), c.getDouble(GlobalKeys.LNG)),
                                            CabalryLocationType.USER_NEARBY));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            public void onPostExecute(Void voids) {
                cabalryMap.updateMap(updatedLocation);
                //cabalryMap.updateCamera(currentLocation, getZoom(),
                //        getBearing(currentLocation, storedLocation), getTransitionTime());

                hasFinishedUpdate = true;
            }
        }.execute();
    }

    private LatLng getPreferredLocation() {

        LatLng currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        LatLng storedLocation = getStoredLocation();

        if(useStoredLocation(currentLocation, storedLocation, CabalryMap.LOCATION_THRESHOLD)) {
            return storedLocation;
        } else {
            return currentLocation;
        }
    }

    private boolean useStoredLocation(LatLng currentLocation, LatLng storedLocation, double threshold) {

        if(currentLocation.latitude > storedLocation.latitude+threshold ||
                currentLocation.latitude < storedLocation.latitude-threshold) {

            if(currentLocation.longitude > storedLocation.longitude+threshold ||
                    currentLocation.longitude < storedLocation.longitude-threshold) {

                return true;
            }
        }

        return false;
    }

    private LatLng getStoredLocation() {
        return new LatLng(Preferences.getFloat(GlobalKeys.LAT), Preferences.getFloat(GlobalKeys.LNG));
    }

    private float getBearing(LatLng current, LatLng previous) {
        float dLan = (float)(previous.latitude - current.latitude);
        float dLng = (float)(previous.  longitude - current.longitude);

        return (float)(Math.toDegrees(Math.atan2(dLng, dLan)) - 90);
    }

    private float getZoom() { return 16; }
    private int getTransitionTime() { return 1000; }
    private int getID() { return Preferences.getInt(GlobalKeys.ID); }
    private String getKey() { return Preferences.getString(GlobalKeys.KEY); }
}