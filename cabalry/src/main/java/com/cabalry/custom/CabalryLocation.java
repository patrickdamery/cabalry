package com.cabalry.custom;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 19/01/15.
 */
public class CabalryLocation {

    public int id;
    public LatLng location;
    public int type;

    public CabalryLocation(int id, LatLng location, int type) {
        set(id, location, type);
    }

    public void set(int id, LatLng location, int type) {
        this.id = id;
        this.location = location;
        this.type = type;
    }
}
