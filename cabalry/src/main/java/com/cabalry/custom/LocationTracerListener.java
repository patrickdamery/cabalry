package com.cabalry.custom;

import android.location.Location;

/**
 * Created by conor on 18/01/15.
 */
public interface LocationTracerListener {

    public void onStartLocationTracer();
    public void onUpdateLocation(Location location);
    public void onStopLocationTracer();
}
