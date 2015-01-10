package com.cabalry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.cabalry.db.DB;
import org.json.JSONException;
import org.json.JSONObject;


public class LoggedIn extends ActionBarActivity {

    public static final String PREFS_NAME = "Cabalry";
    public double longitude, latitude;
    public int id;
    public String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            key = prefs.getString("key", "");
            id = prefs.getInt("id", 0);
            System.out.println("Key is: "+ key);
            System.out.println("Id is: "+ id);

        updateLatLon();
    }

    private void updateLatLon() {
        System.out.println("here");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void makeUseOfNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        System.out.println("Lat: "+ latitude + " Lon: "+ longitude);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JSONObject result = new DB().updateLocation(latitude, longitude, id, key);
                try {
                    int success = result.getInt("success");

                    if (success == 1) {
                        //save key and id
                        System.out.println("Succesful update!");

                    } else {
                        //Return some sort of error message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
