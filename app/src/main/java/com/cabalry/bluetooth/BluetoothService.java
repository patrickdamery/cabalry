package com.cabalry.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cabalry.R;

import static com.cabalry.util.BluetoothUtils.*;

/**
 * BluetoothService
 */
public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";

    private static String NO_DEVICE;

    private static DeviceConnector mConnector;
    private static final BluetoothListener mBTListener = new BluetoothListener() {
        @Override
        public void stateChange(DeviceState state) {
            switch (state) {
                case NOT_CONNECTED:
                    Log.i(TAG, "State not connected");
                    break;

                case CONNECTING:
                    Log.i(TAG, "State connecting");
                    break;

                case CONNECTED:
                    Log.i(TAG, "State connected");
                    break;
            }
        }

        @Override
        public void messageRead(String msg) {
            String[] msgs = msg.split("\\s+");

            for (String m : msgs) {
                if (m.length() > 4) {
                    String sig = m.substring(0, 3);
                    String state = m.substring(3, 4);
                    String power = m.substring(4);

                    Log.i(TAG, "Sig: " + sig);
                    Log.i(TAG, "State: " + state);
                    Log.i(TAG, "Power: " + power);
                }
            }
        }

        @Override
        public void deviceName(String deviceName) {
        }

        @Override
        public void messageToast(String msg) {
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        NO_DEVICE = getString(R.string.no_device_name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConnection();

        Log.d(TAG, "Service destroyed");
    }

    public static boolean isConnected() {
        return (mConnector != null) && (mConnector.getState() == DeviceState.CONNECTED);
    }

    public static boolean hasConnector() {
        return mConnector != null;
    }

    public static void stopConnection() {
        if (mConnector != null) {
            mConnector.stop();
            mConnector = null;
        }
    }

    public static void setupConnector(BluetoothDevice connectedDevice) {
        Log.i(TAG, "Setting up connector");
        stopConnection();
        try {
            mConnector = new DeviceConnector(connectedDevice, mBTListener);
            mConnector.connect();

        } catch (IllegalArgumentException e) {
            Log.i(TAG, "setupConnector failed: " + e.getMessage());
        }
    }
}
