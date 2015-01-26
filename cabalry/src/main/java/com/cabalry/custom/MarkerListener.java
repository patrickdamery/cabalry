package com.cabalry.custom;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 19/01/15.
 */
public interface MarkerListener {

    public boolean onClick(Marker marker);
    public boolean onDoubleClick(Marker marker, String key, int id);
}
