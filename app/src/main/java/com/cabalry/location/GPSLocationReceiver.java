package com.cabalry.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * GPSLocationReceiver
 */
public class GPSLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            if (LocationUpdateService.isRunning()) {
                final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationUpdateService.resetProvider(LocationUpdateManager.UpdateProvider.GPS);

                } else {
                    LocationUpdateService.resetProvider(LocationUpdateManager.UpdateProvider.GPS_NETWORK);
                }
            }
        }
    }
}
