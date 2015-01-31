package com.cabalry.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cabalry.R;
import com.cabalry.custom.CabalryLocation;
import com.cabalry.custom.CabalryLocationListener;
import com.cabalry.custom.CabalryMapActivity;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.MathUtil;
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
 */
public class MapActivity extends CabalryMapActivity {

    private Button bNearby;
    private Button bAlarm;

    private boolean isNearbyEnabled = false;

    private LatLng currentLocation;
    private LatLng previousLocation;

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

                update();
            }
        });

        bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        CabalryLocationListener listener = new CabalryLocationListener() {
            @Override
            public boolean onClick(Marker marker, CabalryLocation location) {
                return true;
            }

            @Override
            public boolean onDoubleClick(Marker marker, CabalryLocation location) {
                return true;
            }
        };

        currentLocation = TracerLocationService.getStoredLocation();
        previousLocation = currentLocation;

        initMap(mapFragment, listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer(0, 5000);
    }

    @Override
    public void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    protected ArrayList<CabalryLocation> updateCabalryLocations() {

        currentLocation = TracerLocationService.getCurrentLocation();
        previousLocation = TracerLocationService.getPreviousLocation();

        ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

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

        return locations;
    }

    @Override
    protected void updateMapLocations(ArrayList<CabalryLocation> locations) {

        int transTime = 1000;

        if(isNearbyEnabled) {
            updateMap(locations, transTime);
        } else {
            float zoom = 15;
            float bearing = MathUtil.getBearing(currentLocation, previousLocation);
            updateMap(locations, currentLocation, zoom, bearing, transTime);
        }
    }
}