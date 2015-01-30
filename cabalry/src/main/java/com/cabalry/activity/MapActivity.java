package com.cabalry.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 11/01/15.
 */
public class MapActivity extends Activity {

    private Button bNearby;
    private Button bAlarm;

    private CabalryMap cabalryMap;

    private boolean isNearbyEnabled = false;
    private boolean hasFinishedUpdate = true;

    private Timer updateTimer;

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

            }
        });

        cabalryMap = new CabalryMap((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map));

        cabalryMap.setMarkerListener(new MarkerListener() {
            @Override
            public boolean onClick(Marker marker, CabalryLocation location) {
                return true;
            }

            @Override
            public boolean onDoubleClick(Marker marker, CabalryLocation location) {
                return true;
            }
        });

        updateLocations();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateLocations();
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onPause() {
        updateTimer.cancel();
        super.onPause();
    }

    private void updateLocations() {

        if(!hasFinishedUpdate) return;
        hasFinishedUpdate = false;

        final ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void ... voids) {

                LatLng currentLocation = TracerIntentService.getCurrentLocation();

                // Add user location.
                locations.add(new CabalryLocation(Preferences.getID(), currentLocation, CabalryLocation.USER));

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

                                locations.add(new CabalryLocation(id, loc, CabalryLocation.USER_NEARBY));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        float b = Util.getBearing(TracerIntentService.getCurrentLocation(),
                TracerIntentService.getPreviousLocation());
        int t = 1000;

        if (isNearbyEnabled) {
            cabalryMap.updateCamera(locations, t);
        } else {
            cabalryMap.updateCamera(TracerIntentService.getCurrentLocation(), z, b, t);
        }

        cabalryMap.updateMap(locations);
    }
}