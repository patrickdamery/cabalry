package com.cabalry.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BluetoothUtils
 */
public class BluetoothUtils {
    private static final String TAG = "BluetoothUtils";

    public static final String DEVICE_STATUS_SIGNATURE = "sig";

    public enum DeviceState {
        NOT_CONNECTED, CONNECTING, CONNECTED
    }

    public static BluetoothSocket CreateRfcommSocket(BluetoothDevice device) {
        BluetoothSocket tmp = null;

        try {
            Method createRfcommSocket = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) createRfcommSocket.invoke(device, 1);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(TAG, "CreateRfcommSocket() failed", e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.e(TAG, "CreateRfcommSocket() failed", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "CreateRfcommSocket() failed", e);
        }
        return tmp;
    }
}
