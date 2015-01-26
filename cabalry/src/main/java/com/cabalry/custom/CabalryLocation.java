package com.cabalry.custom;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 19/01/15.
 */
public class CabalryLocation {

    public int id;
    public String key;
    public LatLng location;
    public int type;

    public CabalryLocation(int id, String key, LatLng location, int type) {
        set(id, key, location, type);
    }

    public void set(int id, String key, LatLng location, int type) {
        this.id = id;
        this.key = key;
        this.location = location;
        this.type = type;
    }
}
