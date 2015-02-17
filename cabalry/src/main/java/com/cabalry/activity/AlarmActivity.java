package com.cabalry.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.cabalry.service.AudioPlaybackService;
import com.cabalry.service.AudioStreamService;
import com.cabalry.service.LocationTracerService;
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
 *
 * Activity that handles alarm events.
 * Extends CabalryMapActivity as base class which contains a cabalry map,
 * which displays all the locations of the users involved in the alarm.
 */
public class AlarmActivity extends CabalryMapActivity {

    // UI components.
    private Button bStop;

    // Alarm info fields.
    private int alarmID;        // Holds the alarm ID.
    private String start;       // Date of alarm event.
    private String ip;          // IP used for audio streaming.
    private int id;             // ID of user on alert.

    // True if this user called the alarm.
    private boolean selfActivated;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        bStop = (Button) findViewById(R.id.bStop);

        // Check if it's not self activated so we can
        // set the background images of buttons accordingly.
        if(!selfActivated) {
            // Set the stop button to ignore image
            // TODO: Replace with real ignore image
            bStop.setBackgroundResource(R.drawable.b_alarm);
        }

        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selfActivated) {
                    // Prompt to cancel activity.
                    Intent cancel = new Intent(getApplicationContext(), AlarmCancelActivity.class);
                    startActivity(cancel);
                } else {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    ignoreAlarm();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                    builder.setMessage("Are you sure you want to ignore alarm?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });

        // Alarm ID should be stored in preferences.
        alarmID = Preferences.getAlarmId();
        if(alarmID == 0) {
            finishAlarm();
        }

        // Get map fragment for cabalry map.
        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        // Listener for marker behavior.
        final CabalryLocationListener listener = new CabalryLocationListener() {
            @Override
            public boolean onClick(Marker marker, CabalryLocation location) {
                marker.showInfoWindow();
                return true;
            }

            @Override
            public boolean onInfoClick(Marker marker, CabalryLocation location) {
                // launch user info.
                Intent userInfo = new Intent(getApplicationContext(), UserInfoActivity.class);
                userInfo.putExtra("id", location.id);
                startActivity(userInfo);
                return true;
            }
        };

        /**
         * Get alarm info in separate thread.
         * After fetching it initialize cabalry map.
         */
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                // Result from DB call.
                JSONObject result = DB.getAlarmInfo(alarmID, Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS)) {

                        // Get alarm info.
                        id = result.getInt(GlobalKeys.ID);
                        start = result.getString(GlobalKeys.START);
                        ip = result.getString(GlobalKeys.IP);

                        Preferences.setIP(ip);

                        // Check if this user has activated the alarm for
                        // future checks.
                        if(id == Preferences.getID()) {
                            selfActivated = true;
                        }

                        String state = result.getString(GlobalKeys.STATE);

                        if(state.equals(GlobalKeys.STATE_FINISHED) || state.equals(GlobalKeys.STATE_LOST)) {
                            return true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean finished) {

                if(finished) {
                    finishAlarm();
                    return;
                }

                if(selfActivated) {
                    bStop.setText("Stop");

                    // Start audio stream service.
                    Intent streamer = new Intent(getApplicationContext(), AudioStreamService.class);
                    startService(streamer);
                } else {
                    bStop.setText("Ignore");

                    // Start audio playback service.
                    Intent playback = new Intent(getApplicationContext(), AudioPlaybackService.class);
                    startService(playback);
                }

                // Initializes cabalry map with fragment and listener.
                initMap(mapFragment, listener);
            }
        }.execute();
    }

    private void ignoreAlarm() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JSONObject result = DB.ignoreAlarm(alarmID, Preferences.getID(), Preferences.getKey());

                try {
                    if(!result.getBoolean(GlobalKeys.SUCCESS)) {
                        Logger.log("Could not ignore alarm on server!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        Preferences.setCachedAlarmId(Preferences.getAlarmId());
        Preferences.setAlarmId(0);

        AudioPlaybackService.stopAudioPlayback();
        AudioStreamService.stopAudioStream();

        stopService(new Intent(AlarmActivity.this, AudioStreamService.class));
        stopService(new Intent(AlarmActivity.this, AudioPlaybackService.class));

        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }

    private void finishAlarm() {
        Toast.makeText(getApplicationContext(), "The alarm has been stopped",
                Toast.LENGTH_LONG).show();

        Preferences.setCachedAlarmId(0);
        Preferences.setAlarmId(0);

        AudioPlaybackService.stopAudioPlayback();
        AudioStreamService.stopAudioStream();

        stopService(new Intent(AlarmActivity.this, AudioStreamService.class));
        stopService(new Intent(AlarmActivity.this, AudioPlaybackService.class));

        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }

    /**
     * Starts timer which updates map locations.
     * Called every 5000 milliseconds
     */
    @Override
    public void onResume() {
        super.onResume();
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
        // Minimize app.
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }

    /**
     * Fetches alerted locations and sorts them into an array list.
     */
    @Override
    protected ArrayList<CabalryLocation> updateCabalryLocations() {

        // Output list of location.
        ArrayList<CabalryLocation> locations = new ArrayList<CabalryLocation>();

        // Add this user in case of selfActivated
        if(selfActivated) {
            locations.add(new CabalryLocation(Preferences.getID(),
                    LocationTracerService.getCurrentLocation(), CabalryLocation.USER_ALERT));
        } else {

            JSONObject userAlert = DB.getLocation(id, Preferences.getID(), Preferences.getKey());
            try {
                if(userAlert.getBoolean(GlobalKeys.SUCCESS)) {

                    // fetch location.
                    LatLng alertLocation = new LatLng(userAlert.getDouble(GlobalKeys.LAT), userAlert.getDouble(GlobalKeys.LNG));
                    locations.add(new CabalryLocation(id, alertLocation, CabalryLocation.USER_ALERT));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Query DB for alerted locations.
        JSONObject alarmInfo = DB.getAlarmList(alarmID, Preferences.getID(), Preferences.getKey());

        try {
            if(alarmInfo.getBoolean(GlobalKeys.SUCCESS)) {

                String state = alarmInfo.getString(GlobalKeys.STATE);

                if(state.equals(GlobalKeys.STATE_FINISHED) || state.equals(GlobalKeys.STATE_LOST)) {
                    return null;
                }

                // Fetch raw array of locations.
                JSONArray list = alarmInfo.getJSONArray(GlobalKeys.SENT);

                // Parse each location and add it.
                for (int i = 0; i < list.length(); i++) {
                    JSONObject location = list.getJSONObject(i);

                    // Location info.
                    int id = location.getInt(GlobalKeys.ID);
                    LatLng loc = new LatLng(location.getDouble(GlobalKeys.LAT),
                            location.getDouble(GlobalKeys.LNG));

                    // Check if user is in list.
                    if(id == Preferences.getID()) {

                        // Add as user in case id's match.
                        locations.add(new CabalryLocation(id, loc, CabalryLocation.USER));
                    } else {

                        // Add as alerted user.
                        locations.add(new CabalryLocation(id, loc, CabalryLocation.USER_ALERTED));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locations;
    }

    /**
     * Updates cabalry map locations.
     */
    @Override
    protected void updateMapLocations(ArrayList<CabalryLocation> locations) {

        if(locations == null) {
            finishAlarm();
            return;
        }

        if(!selfActivated) {

            LatLng userLocation = null;
            LatLng alertLocation = null;

            for(CabalryLocation location : locations) {
                if(userLocation != null && alertLocation != null) break;
                if(location.id == Preferences.getID()) {
                    userLocation = location.location;
                } else if(location.id == id) {
                    alertLocation = location.location;
                }
            }
        }

        // Updates map to fit every location.
        int transTime = 1000;       // Transition time in millis.
        updateMap(locations, transTime);
    }
}
