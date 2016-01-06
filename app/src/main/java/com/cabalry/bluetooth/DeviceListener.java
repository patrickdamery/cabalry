package com.cabalry.bluetooth;

import android.content.Context;

/**
 * DeviceListener
 */
public interface DeviceListener {

    void onDeviceButtonPressed(Context context);

    void onDeviceLowBattery(Context context);

    void onDeviceDisconnected(Context context);
}
