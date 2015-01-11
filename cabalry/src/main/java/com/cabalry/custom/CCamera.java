package com.cabalry.custom;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 11/01/15.
 */
public class CCamera {

    private CMap map;
    private CMarker target = null;
    private float zoomLevel = 10;
    private float bearing = 90;

    private int transTime = 1000;

    public CCamera(CMap map) {
        this.map = map;
    }

    public void updateCamera() {

        if(target != null) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(target.getLocation())
                    .zoom(zoomLevel)
                    .bearing(bearing)
                    .build();

            // Animate the change in camera view over some time.
            map.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    transTime, null);
        }
    }

    public void setTarget(CMarker target) {
        this.target = target;
    }

    public void setZoomLevel(float zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setTransTime(int millis) {
        transTime = millis;
    }

    public CMarker getTarget() { return target; }
    public float getZoomLevel() { return zoomLevel; }
    public float getBearing() { return bearing; }
    public int getTransTime() { return transTime; }
}