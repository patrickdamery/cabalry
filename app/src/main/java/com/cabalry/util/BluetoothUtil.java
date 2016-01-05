package com.cabalry.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BluetoothUtil
 */
public class BluetoothUtil {
    private static final String TAG = "BluetoothUtil";

    public static final int STATE_NOT_CONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

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
