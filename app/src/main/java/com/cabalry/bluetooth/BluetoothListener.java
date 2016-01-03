package com.cabalry.bluetooth;

import static com.cabalry.util.BluetoothUtils.*;

/**
 * Created by conor on 03/01/16.
 */
public interface BluetoothListener {

    void stateChange(BTState state);
    void messageRead(String msg);
    void deviceName(String deviceName);
    void messageToast(String msg);
}
