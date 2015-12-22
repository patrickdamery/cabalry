package com.cabalry.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.cabalry.location.LocationUpdateManager;

import java.util.Set;

/**
 * Created by conor on 22/12/15.
 */
public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";

    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");

        BluetoothProgram bluetoothProgram = new BluetoothProgram(getApplicationContext(), null);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        bluetoothProgram.connect(null, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationUpdateManager.Instance(this).resetProvider(manager);

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() { LocationUpdateManager.Instance(this).dispose(); }
}
