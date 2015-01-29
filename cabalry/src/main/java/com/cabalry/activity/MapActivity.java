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
import com.google.android.gms.maps.model.Marker;
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
    private AudioStream audioStream;

    private LatLng currentLocation;
    private LatLng previousLocation;

    private boolean isAlarmEnabled = false;

    private boolean isNearbyEnabled = false;
    private boolean hasFinishedUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        currentLocation = getStoredLocation();
        previousLocation = currentLocation;

        audioStream = new AudioStream();

        initUI();
        initMap();
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

    private void initUI() {

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
    }

    private void initMap() {
        TracerListener tracerListener = new TracerListener() {
            @Override
            public void onUpdateLocation(Location location) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

        cabalryMap.setMarkerListener(new MarkerListener() {
            @Override
            public boolean onClick(Marker marker) {
                return true;
            }

            @Override
            public boolean onDoubleClick(Marker marker, String key, int id) {
                return true;
            }
        });
    }

    private void startAlarm() {

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void ... voids) {

                JSONObject result = DB.alarm(Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {
                        Preferences.setInt(GlobalKeys.ALARM_ID, result.getInt(GlobalKeys.ALARM_ID));

                        audioStream.startStream();
                    } else {
                        Logger.log("Could not start alarm!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                updateLocations();
            }
        }.execute();
    }

    private void stopAlarm() {

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void ... voids) {

                Logger.log("ALARM_ID = "+Preferences.getInt(GlobalKeys.ALARM_ID));
                JSONObject result = DB.stopAlarm(Preferences.getInt(GlobalKeys.ALARM_ID), Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS) == true) {
                        Preferences.setInt(GlobalKeys.ALARM_ID, 0);

                        audioStream.stopStream();
                    } else {
                        Logger.log("Could not stop alarm!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                updateLocations();
            }
        }.execute();
    }

    private void updateLocations() {

        if(!hasFinishedUpdate) return;
        hasFinishedUpdate = false;

        final ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void ... voids) {

                currentLocation = getPreferredLocation();

                // Stores current user location.
                Preferences.setFloat(GlobalKeys.LAT, (float) currentLocation.latitude);
                Preferences.setFloat(GlobalKeys.LNG, (float) currentLocation.longitude);

                JSONObject updateResult = DB.updateLocation(currentLocation.latitude, currentLocation.longitude,
                        Preferences.getID(), Preferences.getKey());

                try {
                    if(!updateResult.getBoolean(GlobalKeys.SUCCESS)) {
                        Logger.log("Could not update location on server!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(isAlarmEnabled) {

                    JSONObject alarmInfo = DB.getAlarmInfo(Preferences.getAlarmId(),
                            Preferences.getID(), Preferences.getKey());

                    try {
                        if(alarmInfo.getBoolean(GlobalKeys.SUCCESS)) {

                            Preferences.setString(GlobalKeys.IP, alarmInfo.getString(GlobalKeys.IP));
                            Preferences.setString(GlobalKeys.START, alarmInfo.getString(GlobalKeys.START));

                            JSONArray sentList = alarmInfo.getJSONArray(GlobalKeys.SENT);

                            JSONObject alertedLocation = DB.getLocation(alarmInfo.getInt(GlobalKeys.ID),
                                    Preferences.getID(), Preferences.getKey());

                            try {
                                if(!alertedLocation.getBoolean(GlobalKeys.SUCCESS)) {
                                    Logger.log("Alerted location not retrieved from server!");
                                }

                                // Add alerted user location.
                                locations.add(new CabalryLocation(alarmInfo.getInt(GlobalKeys.ID),
                                        new LatLng(alertedLocation.getDouble(GlobalKeys.LAT),
                                                alertedLocation.getDouble(GlobalKeys.LNG)),
                                        CabalryLocationType.USER_ALERT));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Add contacted user locations.
                            for (int i = 0; i < sentList.length(); i++) {
                                JSONObject location = sentList.getJSONObject(i);

                                int id = location.getInt(GlobalKeys.ID);
                                LatLng loc = new LatLng(location.getDouble(GlobalKeys.LAT),
                                        location.getDouble(GlobalKeys.LNG));

                                Logger.log(loc.toString());

                                locations.add(new CabalryLocation(id, loc, CabalryLocationType.USER_ALERTED));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Add user location.
                    locations.add(new CabalryLocation(Preferences.getID(), currentLocation, CabalryLocationType.USER));

                    // Add nearby locations.
                    if (isNearbyEnabled) {

                        JSONObject nearbyResult = DB.nearby(Preferences.getID(), Preferences.getKey());

                        try {
                            if (nearbyResult.getBoolean(GlobalKeys.SUCCESS)) {
                                JSONArray list = nearbyResult.getJSONArray(GlobalKeys.LOCATION);
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject location = list.getJSONObject(i);

                                    int id = location.getInt(GlobalKeys.ID);
                                    LatLng loc = new LatLng(location.getDouble(GlobalKeys.LAT),
                                            location.getDouble(GlobalKeys.LNG));

                                    locations.add(new CabalryLocation(id, loc, CabalryLocationType.USER_NEARBY));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            }

            public void onPostExecute(Void voids) {

                updateMap(locations);
                hasFinishedUpdate = true;
            }
        }.execute();
    }

    private void updateMap(ArrayList<CabalryLocation> locations) {

        float z = 15;
        float b = Util.getBearing(currentLocation, previousLocation);
        int t = 1000;

        if(isAlarmEnabled) {
            cabalryMap.updateCamera(locations, t);
        } else {
            if (isNearbyEnabled) {
                cabalryMap.updateCamera(locations, t);
            } else {
                cabalryMap.updateCamera(currentLocation, z, b, t);
            }
        }

        cabalryMap.updateMap(locations);

        previousLocation = currentLocation;
    }

    private LatLng getPreferredLocation() {

        LatLng storedLocation = getStoredLocation();

        if(Util.useCurrentLocation(currentLocation, storedLocation, CabalryMap.LOCATION_THRESHOLD)) {
            if(Util.useCurrentLocation(currentLocation, previousLocation, CabalryMap.LOCATION_THRESHOLD)) {
                return currentLocation;
            } else {
                return previousLocation;
            }
        } else {
            return getStoredLocation();
        }
    }

    private LatLng getStoredLocation() {
        return new LatLng(Preferences.getFloat(GlobalKeys.LAT), Preferences.getFloat(GlobalKeys.LNG));
    }
}