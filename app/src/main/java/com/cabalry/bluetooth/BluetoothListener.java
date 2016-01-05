package com.cabalry.bluetooth;

/**
 * BluetoothListener
 */
public interface BluetoothListener {

    void onStateChange(int state);

    void onStatusUpdate(String sig, String state, String charge);

    void onDeviceName(String deviceName);

    void onMessageToast(String msg);
}
