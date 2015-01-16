package com.cabalry;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.cabalry.custom.CMap;
import com.cabalry.custom.CMarker;
import com.cabalry.db.DB;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends Activity {
    public static void launch(Activity currentActivity) {
        Intent indent = new Intent(currentActivity, MapActivity.class);
        currentActivity.startActivity(indent);
    }

    private CMap map;

    private LatLng userLocation;

    private boolean nearbyUpdateFinished = true;
    private boolean nearbyEnabled = false;
    private ArrayList<CMarker> nearbyMarkers = new ArrayList<CMarker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeTracer();
        initializeMap();
    }

    private void initializeMap() {

        map = new CMap(this);
    }

    private void initializeTracer() {

        // Acquire a reference to the system Location Manager.
        LocationManager locationManager = (LocationManager)
                this.getSystemService(getApplicationContext().LOCATION_SERVICE);

        // Define a listener that responds to location updates.
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                updateLocation(location);
            }

            // Unused.
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        // Register the listener with the Location Manager to receive location updates.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mHome:
                HomeActivity.launch(this);
                break;
            case R.id.mFocus:
                //focusOnTarget(userMarker);
                break;
            case R.id.mNearby:
                nearbyEnabled = !nearbyEnabled;
                updateMap();
                break;
            case R.id.mSettings:
                break;
        }
        return true;
    }

    // Updates current login user's latitude and longitude.
    private void updateLocation(Location location) {

        userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if(nearbyEnabled) {
            updateNearbyMarkers();
        }

        updateMap();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DB.updateLocation(userLocation.latitude, userLocation.longitude, getID(), getKey());
                return null;
            }
        }.execute();
    }

    private void updateMap() {

        map.getMarkers().clear();

        if(userLocation != null) {
            map.getMarkers().add(new CMarker("User", 0, CMarker.USER_HUE,
                    userLocation));
        }

        if(nearbyEnabled) {

            for(CMarker marker : nearbyMarkers) {
                map.getMarkers().add(marker);
            }
        }

        map.updateMap();
    }

    private void updateNearbyMarkers() {

        if(nearbyUpdateFinished) {
            nearbyUpdateFinished = false;
            nearbyMarkers.clear();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    JSONObject result = null;

                    try {
                        result = DB.nearby(getID(), getKey());

                        try {
                            boolean success = result.getBoolean(DB.SUCCESS);

                            if (success) {

                                JSONArray list = result.getJSONArray("location");

                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject c = list.getJSONObject(i);

                                    nearbyMarkers.add(new CMarker("Cabalry", c.getInt("id"), CMarker.NEARBY_HUE,
                                            new LatLng(c.getDouble("lat"), c.getDouble("lon"))));
                                }
                            } else {

                                // TODO: Handle fail.
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void voids) {
                    nearbyUpdateFinished = true;
                }
            }.execute();
        }
    }

    private void startAlarm() {

    }

    private void stopAlarm() {

    }

    private int getID() { return Preferences.get(DB.ID, 0);}
    private String getKey() { return Preferences.get(DB.KEY, ""); }
}