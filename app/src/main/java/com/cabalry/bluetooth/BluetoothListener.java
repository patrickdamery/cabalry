package com.cabalry.bluetooth;

import static com.cabalry.util.BluetoothUtils.*;

/**
 * BluetoothListener
 */
public interface BluetoothListener {

    void onStateChange(DeviceState state);

    void onStatusUpdate(String sig, String state, String charge);

    void onDeviceName(String deviceName);

    void onMessageToast(String msg);
}
