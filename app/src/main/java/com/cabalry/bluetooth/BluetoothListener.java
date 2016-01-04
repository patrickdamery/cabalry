package com.cabalry.bluetooth;

import static com.cabalry.util.BluetoothUtils.*;

/**
 * BluetoothListener
 */
public interface BluetoothListener {

    void stateChange(DeviceState state);

    void messageRead(String msg);

    void deviceName(String deviceName);

    void messageToast(String msg);
}
