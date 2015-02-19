package com.cabalry.custom;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by conor on 19/01/15.
 */
public interface CabalryLocationListener {

    public boolean onClick(Marker marker, CabalryLocation location);
    public boolean onInfoClick(Marker marker, CabalryLocation location);
}
