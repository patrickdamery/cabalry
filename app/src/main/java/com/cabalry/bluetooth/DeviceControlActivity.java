package com.cabalry.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cabalry.R;
import static com.cabalry.util.BluetoothUtils.*;

public final class DeviceControlActivity extends BaseActivity {
    private static final String TAG = "DeviceControlActivity";

    private static DeviceConnector mConnector;
    private static final BluetoothListener mBTListener = new BluetoothListener() {
        @Override
        public void stateChange(BTState state) {
            switch(state) {
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

            for(String m : msgs) {
                if(m.length() > 4) {
                    String sig = m.substring(0,3);
                    String state = m.substring(3,4);
                    String power = m.substring(4);

                    Log.i(TAG, "Sig: "+sig);
                    Log.i(TAG, "State: "+state);
                    Log.i(TAG, "Power: "+power);
                }
            }
        }

        @Override
        public void deviceName(String deviceName) { }

        @Override
        public void messageToast(String msg) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_control);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean isConnected() {
        return (mConnector != null) && (mConnector.getState() == BTState.CONNECTED);
    }

    private void stopConnection() {
        if (mConnector != null) {
            mConnector.stop();
            mConnector = null;
        }
    }

    private void startDeviceListActivity() {
        stopConnection();
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public boolean onSearchRequested() {
        if (super.isAdapterReady()) startDeviceListActivity();
        return false;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_control_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_search:
                if (super.isAdapterReady()) {
                    if (isConnected()) stopConnection();
                    else startDeviceListActivity();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    if (super.isAdapterReady() && (mConnector == null)) setupConnector(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                super.pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Log.i(TAG, "BT not enabled");
                }
                break;
        }
    }

    private void setupConnector(BluetoothDevice connectedDevice) {
        stopConnection();
        try {
            String emptyName = getString(R.string.empty_device_name);
            DeviceData data = new DeviceData(connectedDevice, emptyName);
            mConnector = new DeviceConnector(data, mBTListener);
            mConnector.connect();
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "setupConnector failed: " + e.getMessage());
        }
    }

    public void btButton(View view) {
        if (super.isAdapterReady()) {
            if (isConnected()) stopConnection();
            else startDeviceListActivity();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}