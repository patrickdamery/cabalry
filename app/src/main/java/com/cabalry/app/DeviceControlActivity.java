package com.cabalry.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cabalry.R;
import com.cabalry.bluetooth.BluetoothListener;
import com.cabalry.bluetooth.BluetoothService;
import com.cabalry.base.BindableActivity;

import static com.cabalry.util.BluetoothUtil.*;
import static com.cabalry.util.MessageUtil.*;
import static com.cabalry.util.PreferencesUtil.*;

/**
 * DeviceControlActivity
 */
public final class DeviceControlActivity extends BindableActivity
        implements BluetoothListener {
    private static final String TAG = "DeviceControlActivity";

    // Intent request codes
    static final int REQUEST_CONNECT_DEVICE = 1;
    static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBTAdapter;

    private static final String SAVED_PENDING_REQUEST_ENABLE_BT = "PENDING_REQUEST_ENABLE_BT";
    // Do not resend request to enable Bluetooth
    // if there is a request already in progress
    // See: https://code.google.com/p/android/issues/detail?id=24931#c1
    private boolean pendingRequestEnableBt = false;

    private TextView mDeviceStateText, mDeviceChargeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BluetoothService.isRunning()) {
            startService(new Intent(this, BluetoothService.class));
        }

        if (savedInstanceState != null) {
            pendingRequestEnableBt = savedInstanceState.getBoolean(SAVED_PENDING_REQUEST_ENABLE_BT);
        }

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) {
            final String no_bluetooth = getString(R.string.no_bt_support);
            showAlertDialog(no_bluetooth);
            Log.e(TAG, no_bluetooth);
        }

        setContentView(R.layout.activity_device_control);
        mDeviceStateText = (TextView) findViewById(R.id.deviceStateText);
        mDeviceChargeText = (TextView) findViewById(R.id.deviceChargeText);

        restoreSavedInstance(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindToService(BluetoothService.class, new MessengerHandler(this),
                MSG_REGISTER_CLIENT, MSG_UNREGISTER_CLIENT);

        if (mBTAdapter != null) {
            if (!mBTAdapter.isEnabled() && !pendingRequestEnableBt) {
                pendingRequestEnableBt = true;
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_PENDING_REQUEST_ENABLE_BT, pendingRequestEnableBt);

        outState.putString("deviceState", mDeviceStateText.getText().toString());
        outState.putString("deviceCharge", mDeviceChargeText.getText().toString());
    }

    private void restoreSavedInstance(Bundle state) {
        if (state != null) {
            mDeviceStateText.setText(state.getString("deviceState"));
            mDeviceChargeText.setText(state.getString("deviceCharge"));
        }

        onStateChange(BluetoothService.getState());
    }

    @Override
    public void onStateChange(int state) {
        switch (state) {
            case STATE_NOT_CONNECTED:
                mDeviceChargeText.setText(" - %");
                mDeviceStateText.setText(getString(R.string.msg_not_connected));
                break;

            case STATE_CONNECTING:
                mDeviceChargeText.setText(" - %");
                mDeviceStateText.setText(getString(R.string.msg_connecting));
                break;

            case STATE_CONNECTED:
                int charge = GetDeviceCharge(getApplicationContext());
                mDeviceChargeText.setText(" " + charge + "%");
                mDeviceStateText.setText(getString(R.string.msg_connected));
                break;

            case STATE_CONNECTION_FAILED:
                mDeviceChargeText.setText(" - %");
                mDeviceStateText.setText(getString(R.string.msg_not_connected));
                break;
        }
    }

    @Override
    public void onStatusUpdate(String sig, String status, String charge) {
        mDeviceChargeText.setText(" " + charge + "%");
    }

    @Override
    public void onDeviceName(String deviceName) {
    }

    @Override
    public void onMessageToast(String msg) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindFromService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
    }

    @Override
    public boolean onSearchRequested() {
        if (isAdapterReady()) startDeviceListActivity();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
                    if (isAdapterReady()) {
                        SetCachedAddress(getApplicationContext(), address);
                        BluetoothService.setupConnector(device);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                pendingRequestEnableBt = false;
                if (resultCode != Activity.RESULT_OK) {
                    Log.i(TAG, "BT not enabled");
                }
                break;
        }
    }

    boolean isAdapterReady() {
        return (mBTAdapter != null) && (mBTAdapter.isEnabled());
    }

    void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.title_app_name));
        alertDialogBuilder.setMessage(message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startDeviceListActivity() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void deviceConnectCallback(View view) {
        if (isAdapterReady()) {
            sendMessageToService(MSG_BLUETOOTH_CONNECT, null);
            startDeviceListActivity();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void deviceDisconnectCallback(View view) {
        SetCachedAddress(getApplicationContext(), null);
        if (BluetoothService.isConnected())
            BluetoothService.stopConnection();
        BluetoothService.clearCachedAddress();
    }

    /**
     * MessengerHandler
     */
    private class MessengerHandler extends Handler {

        final BluetoothListener mBTListener;

        public MessengerHandler(BluetoothListener btListener) {
            mBTListener = btListener;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data == null)
                throw new NullPointerException("data is null");

            switch (msg.what) {
                case MSG_DEVICE_STATE:
                    int state = data.getInt("state");
                    mBTListener.onStateChange(state);
                    break;

                case MSG_DEVICE_STATUS:
                    String sig = (String) data.get("sig");
                    String status = (String) data.get("status");
                    String charge = (String) data.get("charge");
                    mBTListener.onStatusUpdate(sig, status, charge);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}