package com.cabalry.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import static com.cabalry.util.BluetoothUtil.*;
import static com.cabalry.util.MessageUtil.*;
import static com.cabalry.util.PreferencesUtil.*;

/**
 * BluetoothService
 */
public class BluetoothService extends BindableService {
    private static final String TAG = "BluetoothService";

    private static int mPrevChargeStatus;

    private static NotificationManager mNotificationManager;
    private static DeviceConnector mConnector;
    private static ServiceBluetoothListener mListener;

    private static class ServiceBluetoothListener implements BluetoothListener {

        private final Context mContext;

        public ServiceBluetoothListener(Context context) {
            mContext = context;
        }

        @Override
        public void onStateChange(int state) {
            Bundle data = new Bundle();
            data.putInt("state", state);

            sendMessageToActivity(MSG_DEVICE_STATE, data);
        }

        @Override
        public void onStatusUpdate(String sig, String status, String charge) {
            int chargeVal = Integer.parseInt(charge);
            if (mPrevChargeStatus - chargeVal < 10)
                mPrevChargeStatus = chargeVal;

            if (status.equals("A")) {
                Log.i(TAG, "Alarm");
                alertAlarm(mContext);
            }

            Bundle data = new Bundle();
            data.putString("sig", sig);
            data.putString("status", status);
            data.putInt("charge", mPrevChargeStatus);

            SetDeviceCharge(mContext, mPrevChargeStatus);
            sendMessageToActivity(MSG_DEVICE_STATUS, data);
        }

        @Override
        public void onDeviceName(String deviceName) {
        }

        @Override
        public void onMessageToast(String msg) {
        }
    }

    // Handler of incoming messages from clients.
    class MessengerHandler extends BaseMessengerHandler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static void alertAlarm(Context context) {
        Intent intent = new Intent(context, DeviceControlActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Alert")
                .setContentText("Panic button pressed")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        mNotificationManager.notify(0, n);
    }

    private void alertLowBattery() {

    }

    private void alertDeviceDisconnected() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mListener = new ServiceBluetoothListener(getApplicationContext());

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

    public synchronized static boolean isConnected() {
        return (mConnector != null) && (mConnector.getState() == STATE_CONNECTED);
    }

    public synchronized static void stopConnection() {
        if (mConnector != null) {
            mConnector.stop();
            mConnector = null;
        }
    }

    public synchronized static void setupConnector(BluetoothDevice connectedDevice) {
        Log.i(TAG, "Setting up connector");
        stopConnection();
        try {
            mConnector = new DeviceConnector(connectedDevice, mListener);
            mConnector.connect();

        } catch (IllegalArgumentException e) {
            Log.i(TAG, "setupConnector failed: " + e.getMessage());
        }
    }
}
