package com.cabalry.custom;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by conor on 11/01/15.
 */
public class CMarkerManager {

    private CMap map;
    private HashMap<Integer,Marker> markers;

    private ArrayList<CMarker> pastMarkers;

    public CMarkerManager(CMap map) {
        this.map = map;
        markers = new HashMap<Integer,Marker>();
        pastMarkers = new ArrayList<CMarker>();
    }

    public void updateMarkers(ArrayList<CMarker> cMarkers) {

        if(false) {
            map.getMap().clear();

            for (CMarker m : cMarkers) {
                addMarker(m);
            }
        } else {

            for(CMarker pm : pastMarkers) {
                for(CMarker cm : cMarkers) {

                    if(pm.getID() == cm.getID()) {
                        moveMarker(pm, cm);

                    } else {
                        if (!pastMarkers.contains(cm)) {
                            addMarker(cm);
                        }

                        if (!cMarkers.contains(pm)) {
                            removeMarker(pm);
                        }
                    }
                }
            }

            pastMarkers.clear();
            pastMarkers = cMarkers;
        }
    }

    private void moveMarker(CMarker a, CMarker b) {
        Marker am = markers.get(a.getID());
        if(am == null) {
            System.out.println("ASDASDASD");
        }

        if(b == null) {
            System.out.println("AAAAAAA");
        }

        markers.get(a.getID()).setPosition(b.getLocation());
    }

    private void addMarker(CMarker cMarker) {
        markers.put(cMarker.getID(), map.getMap().addMarker(new MarkerOptions()
                .title(cMarker.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(cMarker.getHue()))
                .position(cMarker.getLocation())
                .draggable(false)));
    }

    private void removeMarker(CMarker cMarker) {
        markers.get(cMarker.getID()).remove();
        markers.remove(cMarker.getID());
    }
}