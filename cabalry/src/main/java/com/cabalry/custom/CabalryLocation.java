package com.cabalry.custom;

import com.cabalry.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by conor on 19/01/15.
 */
public class CabalryLocation {

    public static final int USER = 0;
    public static final int USER_NEARBY = 1;
    public static final int USER_ALERT = 2;
    public static final int USER_ALERTED = 3;

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

    public static MarkerOptions getMarkerOptions(int id, int type, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .snippet("ID:"+id)
                .draggable(false)
                .title(getTitle(type))
                .icon(getIcon(type));

        return markerOptions;
    }

    public static BitmapDescriptor getIcon(int type) {
        switch(type) {
            case USER:
                return BitmapDescriptorFactory.fromResource(R.drawable.m_user);

            case USER_NEARBY:
                return BitmapDescriptorFactory.fromResource(R.drawable.m_near);

            case USER_ALERT:
                return BitmapDescriptorFactory.fromResource(R.drawable.m_alert);

            case USER_ALERTED:
                return BitmapDescriptorFactory.fromResource(R.drawable.m_alerted);
        }

        return BitmapDescriptorFactory.fromResource(R.drawable.m_user);
    }

    public static String getTitle(int type) {
        switch(type) {
            case USER:
                return "User";

            case USER_NEARBY:
                return "NearBy";

            case USER_ALERT:
                return "Alert!";

            case USER_ALERTED:
                return "Alerted";
        }

        return "";
    }
}
