package com.cabalry.custom;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by conor on 11/01/15.
 */
public class CabalryLocationType {

    public static final int USER = 0;
    public static final int USER_NEARBY = 1;
    public static final int USER_WARNING_LOW = 2;
    public static final int USER_WARNING_HIGH = 3;
    public static final int USER_ALERT = 4;

    public static MarkerOptions getMarkerOptions(int id, int type, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .snippet("ID:"+id)
                .draggable(false);

        switch(type) {
            case USER:
                markerOptions
                        .title("User")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            break;

            case USER_NEARBY:
                markerOptions
                        .title("NearBy")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            break;

            case USER_WARNING_LOW:
                markerOptions
                        .title("Warning!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            break;

            case USER_WARNING_HIGH:
                markerOptions
                        .title("Warning!!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            break;

            case USER_ALERT:
                markerOptions
                        .title("ALERT!!!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            break;
        }

        return markerOptions;
    }
}
