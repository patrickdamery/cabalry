package com.cabalry.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import com.cabalry.R;
import com.cabalry.custom.CabalryLocation;
import com.cabalry.custom.CabalryLocationListener;
import com.cabalry.custom.CabalryMapActivity;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by conor on 29/01/15.
 */
public class AlarmActivity extends CabalryMapActivity {

    private boolean selfActivated;

    private int alarmID;
    private String start;
    private String ip;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmID = Preferences.getAlarmId();

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        final CabalryLocationListener listener = new CabalryLocationListener() {
            @Override
            public boolean onClick(Marker marker, CabalryLocation location) {
                return true;
            }

            @Override
            public boolean onDoubleClick(Marker marker, CabalryLocation location) {
                return true;
            }
        };

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                JSONObject result = DB.getAlarmInfo(alarmID, Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {

                        // Get alarm info.
                        id = result.getInt(GlobalKeys.ID);
                        start = result.getString(GlobalKeys.START);
                        ip = result.getString(GlobalKeys.ID);

                        if(id == Preferences.getID()) {
                            selfActivated = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void voids) {
                initMap(mapFragment, listener);
            }
        }.execute();
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

        ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

        JSONObject result = DB.getAlarmInfo(alarmID, Preferences.getID(), Preferences.getKey());

        if(selfActivated) locations.add(new CabalryLocation(Preferences.getID(), TracerLocationService.getCurrentLocation(), CabalryLocation.USER));

        try {
            if(result.getBoolean(GlobalKeys.SUCCESS)) {

                JSONArray list = result.getJSONArray(GlobalKeys.SENT);
                for (int i = 0; i < list.length(); i++) {
                    JSONObject location = list.getJSONObject(i);

                    int id = location.getInt(GlobalKeys.ID);
                    LatLng loc = new LatLng(location.getDouble(GlobalKeys.LAT),
                            location.getDouble(GlobalKeys.LNG));

                    if(id == Preferences.getID()) {
                        locations.add(new CabalryLocation(id, loc, CabalryLocation.USER));
                    } else {
                        locations.add(new CabalryLocation(id, loc, CabalryLocation.USER_ALERTED));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locations;
    }

    @Override
    protected void updateMapLocations(ArrayList<CabalryLocation> locations) {

        int transTime = 1000;
        updateMap(locations, transTime);
    }
}
