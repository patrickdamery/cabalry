package com.cabalry.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.custom.CabalryLocation;
import com.cabalry.custom.CabalryLocationListener;
import com.cabalry.custom.CabalryMapActivity;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Util;
import com.cabalry.utils.Preferences;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by conor on 11/01/15.
 *
 * Activity that displays normal cabalry map.
 */
public class MapActivity extends CabalryMapActivity {

    private Button bNearby;
    private Button bAlarm;

    private boolean isNearbyEnabled = false;

    private LatLng currentLocation;
    private LatLng previousLocation;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }

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

                update();
            }
        });

        bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarm();
            }
        });

        // Get map fragment for cabalry map.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        // Listener for marker behavior.
        CabalryLocationListener listener = new CabalryLocationListener() {
            @Override
            public boolean onClick(Marker marker, CabalryLocation location) {
                marker.showInfoWindow();
                return true;
            }

            @Override
            public boolean onInfoClick(Marker marker, CabalryLocation location) {
                // launch user info.
                Intent userInfo = new Intent(getApplicationContext(), UserInfoActivity.class);
                startActivity(userInfo);
                return true;
            }
        };

        // Initialize locations.
        currentLocation = TracerLocationService.getStoredLocation();
        previousLocation = currentLocation;

        // Initializes cabalry map with fragment and listener.
        initMap(mapFragment, listener);
    }

    private void startAlarm() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                JSONObject result = DB.alarm(Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {

                        int alarmID = result.getInt(GlobalKeys.ALARM_ID);
                        Preferences.setAlarmId(alarmID);
                        return true;

                    } else {
                        Preferences.setAlarmId(0);
                        Logger.log("Could not start alarm!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    // Start alarm activity.
                    Intent alarm = new Intent(getApplicationContext(), AlarmActivity.class);
                    startActivity(alarm);
                }
            }
        }.execute();
    }

    /**
     * Starts timer which updates map locations.
     * Called every 5000 milliseconds
     */
    @Override
    public void onResume() {
        super.onResume();

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }

        startTimer(0, 5000);
    }

    /**
     * Stop unused timer.
     */
    @Override
    public void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }

    /**
     * Fetches nearby locations and sorts them into an array list.
     */
    @Override
    protected ArrayList<CabalryLocation> updateCabalryLocations() {

        // Output list of location.
        ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

        // Load locations.
        currentLocation = TracerLocationService.getCurrentLocation();
        previousLocation = TracerLocationService.getPreviousLocation();

        // Add user location.
        locations.add(new CabalryLocation(Preferences.getID(), currentLocation, CabalryLocation.USER));

        // Add nearby locations.
        if (isNearbyEnabled) {

            // Query DB for nearby locations.
            JSONObject nearbyResult = DB.nearby(Preferences.getID(), Preferences.getKey());

            try {
                if (nearbyResult.getBoolean(GlobalKeys.SUCCESS)) {

                    // Fetch raw array of locations.
                    JSONArray list = nearbyResult.getJSONArray(GlobalKeys.LOCATION);

                    // Parse each location and add it.
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject location = list.getJSONObject(i);

                        // Location info.
                        int id = location.getInt(GlobalKeys.ID);
                        LatLng loc = new LatLng(location.getDouble(GlobalKeys.LAT),
                                location.getDouble(GlobalKeys.LNG));

                        // Add location to list.
                        locations.add(new CabalryLocation(id, loc, CabalryLocation.USER_NEARBY));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return locations;
    }

    /**
     * Updates cabalry map locations.
     */
    @Override
    protected void updateMapLocations(ArrayList<CabalryLocation> locations) {

        int transTime = 1000;

        if(isNearbyEnabled) {
            updateMap(locations, transTime);
        } else {
            float zoom = 15;
            float bearing = Util.getBearing(currentLocation, previousLocation);
            updateMap(locations, currentLocation, zoom, bearing, transTime);
        }
    }
}