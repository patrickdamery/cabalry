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
import com.cabalry.db.DB;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {
    public static void launch(Activity currentActivity) {
        Intent indent = new Intent(currentActivity, MapActivity.class);
        currentActivity.startActivity(indent);
    }

    public static final float ALARM_HUE = BitmapDescriptorFactory.HUE_RED;
    public static final float NEARBY_HUE = BitmapDescriptorFactory.HUE_GREEN;
    public static final float USER_HUE = BitmapDescriptorFactory.HUE_BLUE;

    private GoogleMap mMap = null;

    private Marker userMarker = null;

    private ArrayList<Marker> nearbyMarkers = new ArrayList<Marker>();
    private int nearbyCount = 5;
    private double radius = 100;

    private LatLng followTarget = null;

    private LatLng userPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeTracer();
        initializeMap();
    }

    private void initializeMap() {

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        UiSettings settings = map.getUiSettings();

        /*
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
        settings.setScrollGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
        settings.setIndoorLevelPickerEnabled(false);
        settings.setCompassEnabled(false);
        */
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
                focusOnTarget(userMarker);
                break;
            case R.id.mNearby:
                //displayNearby();
                break;
            case R.id.mSettings:
                break;
        }
        return true;
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

    // Updates current login user's latitude and longitude.
    private void updateLocation(Location location) {

        userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        System.out.println(userPosition);

        updateNearCabalry();

        if(userMarker == null) {
            userMarker = addMarker("User", userPosition, USER_HUE);
            //focusOnTarget(userMarker);
        } else {
            userMarker.setPosition(userPosition);
            //updateCamera(200);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DB.updateLocation(userPosition.latitude, userPosition.longitude, getID(), getKey());
                return null;
            }
        }.execute();
    }

    private Marker addMarker(String title, LatLng pos, float hue) {

        return mMap.addMarker(new MarkerOptions()
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(hue))
                .position(pos)
                .draggable(false));
    }

    public void startAlarm() {
        DB.alarm(getID(), getKey());
    }

    public void stopAlarm() {

    }

    public void focusOnTarget(Marker target) {
        followTarget = target.getPosition();
        updateCamera(2000);
    }

    private void displayNearby(ArrayList<LatLng> nearbyLocations) {

        for(Marker m : nearbyMarkers) {
            m.remove();
        }

        nearbyMarkers.clear();

        for(LatLng l : nearbyLocations) {
            nearbyMarkers.add(addMarker("Cabalry", l, NEARBY_HUE));
        }
    }

    private void updateNearCabalry() {

        final ArrayList<LatLng> nearbyLocations = new ArrayList<LatLng>();

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

                            for(int i = 0; i < list.length(); i++) {
                                JSONObject c = list.getJSONObject(i);

                                int id = c.getInt("id");
                                double lat = c.getDouble("lat");
                                double lng = c.getDouble("lon");

                                nearbyLocations.add(new LatLng(lat, lng));
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
                displayNearby(nearbyLocations);
            }
        }.execute();
    }

    private void updateCamera(int millis) {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(followTarget)
                .zoom(calculateZoomLevel())
                .bearing(90)
                .build();

        // Animate the change in camera view over some time.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                millis, null);
    }

    private float calculateZoomLevel() {
        return 10;
    }

    public int getID() { return Preferences.get(DB.ID, 0);}
    public String getKey() { return Preferences.get(DB.KEY, ""); }
}