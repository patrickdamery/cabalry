package com.cabalry.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothUtils {
    private static final String TAG = "BluetoothUtils";

    public enum BTState {
        NOT_CONNECTED, CONNECTING, CONNECTED
    }

    public static BluetoothSocket CreateRfcommSocket(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        try {
            Class class1 = device.getClass();
            Class aclass[] = new Class[1];
            aclass[0] = Integer.TYPE;
            Method method = class1.getMethod("createRfcommSocket", aclass);
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(1);

            tmp = (BluetoothSocket) method.invoke(device, aobj);
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
