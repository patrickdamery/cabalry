package com.cabalry.custom;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by conor on 30/01/15.
 */
public abstract class CabalryMapActivity extends Activity {

    private CabalryMap cabalryMap;

    private Timer timer;
    private TimerTask timerTask;

    // We are going to use a handler to be able to run in our TimerTask.
    private final Handler handler = new Handler();

    private boolean hasFinishedUpdate = true;

    protected abstract ArrayList<CabalryLocation> updateCabalryLocations();
    protected abstract void updateMapLocations(ArrayList<CabalryLocation> locations);

    protected void initMap(MapFragment mapFragment, CabalryLocationListener listener) {

        cabalryMap = new CabalryMap(mapFragment);
        cabalryMap.setMarkerListener(listener);

        //update();
    }

    protected void startTimer(long delay, long time) {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        };

        timer.schedule(timerTask, delay, time);
    }

    protected void stopTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected void update() {

        if(!hasFinishedUpdate) return;
        hasFinishedUpdate = false;

        new AsyncTask<Void, Void, ArrayList<CabalryLocation>>() {
            @Override
            public ArrayList<CabalryLocation> doInBackground(Void... voids) {
                return updateCabalryLocations();
            }

            @Override
            protected void onPostExecute(ArrayList<CabalryLocation> locations) {
                updateMapLocations(locations);
                hasFinishedUpdate = true;
            }
        }.execute();
    }

    protected CabalryMap getMap() {
        return cabalryMap;
    }

    protected void updateMap(ArrayList<CabalryLocation> locations,
                          LatLng target, float zoom, float bearing, int transTime) {

        cabalryMap.updateCamera(target, zoom, bearing, transTime);
        cabalryMap.updateMap(locations);
    }

    protected void updateMap(ArrayList<CabalryLocation> locations, int transTime) {

        cabalryMap.updateCamera(locations, transTime);
        cabalryMap.updateMap(locations);
    }
}