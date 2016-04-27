package com.cabalry.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cabalry.R;
import com.cabalry.app.DeviceControlActivity;
import com.cabalry.base.BindableService;
import com.cabalry.base.MovingAverage;

import static com.cabalry.util.MessageUtil.MSG_BLUETOOTH_CONNECT;
import static com.cabalry.util.MessageUtil.MSG_DEVICE_STATE;
import static com.cabalry.util.MessageUtil.MSG_DEVICE_STATUS;
import static com.cabalry.util.PreferencesUtil.GetCachedAddress;
import static com.cabalry.util.PreferencesUtil.GetDeviceCharge;
import static com.cabalry.util.PreferencesUtil.SetDeviceCharge;

/**
 * BluetoothService
 */
public class BluetoothService extends BindableService {
    private static final String TAG = "BluetoothService";

    private static final int LOW_BATTERY_THRESHOLD = 35; // 1 = 1 hour
    private static final int RECONNECT_MAX_TRIES = 2;
    private static final int CHARGE_SAMPLE_SIZE = 3;

    private static NotificationManager mNotificationManager;
    private static DeviceConnector mConnector;
    private static ServiceBluetoothListener mBTListener;
    private static String mCachedDeviceAddress = null;
    private static int mReconnectCount = 0;
    private static boolean mDeviceDisconnected = false;

    private final DeviceListener mDeviceListener = new DeviceListener() {
        @Override
        public void onDeviceButtonPressed(Context context) {
            // Start alarm.
            Intent alarmIntent = new Intent();
            alarmIntent.setAction("com.cabalry.action.ALARM_START");
            sendBroadcast(alarmIntent);
        }

        @Override
        public void onDeviceLowBattery(Context context) {
            Intent intent = new Intent(context, DeviceControlActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

            Notification n = new Notification.Builder(context)
                    .setContentTitle("Low Battery!")
                    .setContentText("Please charge your device")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            mNotificationManager.notify(0, n);
        }

        @Override
        public void onDeviceDisconnected(Context context) {
            if (mReconnectCount < RECONNECT_MAX_TRIES) {
                attemptReconnect(mCachedDeviceAddress);
            }

            Intent intent = new Intent(context, DeviceControlActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

            Notification n = new Notification.Builder(context)
                    .setContentTitle("Device Disconnected!")
                    .setContentText("Please reconnect your device")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            mNotificationManager.notify(0, n);
        }
    };

    private static void attemptReconnect(String address) {
        mReconnectCount++;
        if (address != null) {
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            setupConnector(device);
        }
    }

    public static void clearCachedAddress() {
        mCachedDeviceAddress = null;
    }

    public synchronized static boolean isConnected() {
        return (mConnector != null) && (mConnector.getState() == DeviceConnector.STATE_CONNECTED);
    }

    public synchronized static int getState() {
        return mConnector == null ? DeviceConnector.STATE_NOT_CONNECTED : mConnector.getState();
    }

    public synchronized static void stopConnection() {
        mReconnectCount = 0;

        if (mConnector != null) {
            mConnector.stop();
            mConnector = null;
        }
    }

    public synchronized static void setupConnector(BluetoothDevice connectedDevice) {
        Log.i(TAG, "Setting up connector");
        if (!isConnected() || !mCachedDeviceAddress.equals(connectedDevice.getAddress())) {
            if (isConnected())
                stopConnection();
            try {
                mConnector = new DeviceConnector(connectedDevice, mBTListener);
                mConnector.connect();

                mCachedDeviceAddress = connectedDevice.getAddress();

            } catch (IllegalArgumentException e) {
                Log.i(TAG, "setupConnector failed: " + e.getMessage());
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBTListener = new ServiceBluetoothListener(getApplicationContext());

        String cachedAddress = GetCachedAddress(getApplicationContext());
        if (cachedAddress != null) {
            mDeviceDisconnected = true;
            attemptReconnect(cachedAddress);
        }

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    protected Handler getMessengerHandler() {
        return new MessengerHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConnection();
    }

    private class ServiceBluetoothListener implements BluetoothListener {

        private final Context mContext;
        private double mPrevChargeSample;
        private MovingAverage mMovingAverage;

        public ServiceBluetoothListener(Context context) {
            mContext = context;
            mMovingAverage = new MovingAverage(CHARGE_SAMPLE_SIZE);

            int cachedCharge = GetDeviceCharge(context);
            if (cachedCharge != 0) {
                handleDeviceCharge(cachedCharge);
            }
        }

        @Override
        public void onStateChange(int state) {
            switch (state) {
                case DeviceConnector.STATE_CONNECTED:
                    mDeviceDisconnected = false;
                    mReconnectCount = 0;
                    break;

                case DeviceConnector.STATE_DISCONNECTED:
                    Log.i(TAG, "Device disconnected");
                    mDeviceDisconnected = true;
                    mDeviceListener.onDeviceDisconnected(mContext);
                    break;

                case DeviceConnector.STATE_CONNECTION_FAILED:
                    if (mDeviceDisconnected)
                        mDeviceListener.onDeviceDisconnected(mContext);
                    break;
            }

            Bundle data = new Bundle();
            data.putInt("state", state);

            sendMessageToActivity(MSG_DEVICE_STATE, data);
        }

        @Override
        public void onStatusUpdate(String sig, String status, String charge) {
            mPrevChargeSample = handleDeviceCharge(Integer.parseInt(charge));
            if (mPrevChargeSample <= LOW_BATTERY_THRESHOLD) {
                Log.i(TAG, "Button low battery");
                mDeviceListener.onDeviceLowBattery(mContext);
            }

            if (status.equals("A")) {
                Log.i(TAG, "Button pressed");
                mDeviceListener.onDeviceButtonPressed(mContext);
            }

            Bundle data = new Bundle();
            data.putString("sig", sig);
            data.putString("status", status);
            data.putString("charge", mPrevChargeSample + "");

            SetDeviceCharge(mContext, (int) mPrevChargeSample);
            sendMessageToActivity(MSG_DEVICE_STATUS, data);
        }

        private double handleDeviceCharge(double charge) {
            double chargeAverage = charge;

            if (chargeAverage - mPrevChargeSample < -20) {
                chargeAverage = mPrevChargeSample;
            }

            mMovingAverage.addSample(chargeAverage);
            return mMovingAverage.getCurrentAverage();
        }

        @Override
        public void onDeviceName(String deviceName) {
        }

        @Override
        public void onMessageToast(String msg) {
        }
    }

    // Handler of incoming messages from clients.
    private class MessengerHandler extends BaseMessengerHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BLUETOOTH_CONNECT:
                    mDeviceDisconnected = false;
                    mReconnectCount = 0;
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
