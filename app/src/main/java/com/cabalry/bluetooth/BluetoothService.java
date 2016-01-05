package com.cabalry.bluetooth;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import static com.cabalry.util.BluetoothUtils.*;

/**
 * BluetoothService
 */
public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_STATE_CHANGE = 3;
    public static final int MSG_STATUS_UPDATE = 4;

    private static DeviceConnector mConnector;
    private static final BluetoothListener mBTListener = new BluetoothListener() {
        @Override
        public void onStateChange(DeviceState state) {
            Bundle data = new Bundle();
            data.putSerializable("state", state);
            sendMessageToActivity(MSG_STATE_CHANGE, data);
        }

        @Override
        public void onStatusUpdate(String sig, String status, String charge) {
            Bundle data = new Bundle();
            data.putString("sig", sig);
            data.putString("status", status);
            data.putString("charge", charge);
            sendMessageToActivity(MSG_STATUS_UPDATE, data);
        }

        @Override
        public void onDeviceName(String deviceName) {
        }

        @Override
        public void onMessageToast(String msg) {
        }
    };

    private NotificationManager mNotificationManager;
    private static boolean isRunning = false;

    private static ArrayList<Messenger> mClients = new ArrayList<>(); // Keeps track of all current registered clients.
    final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private static void sendMessageToActivity(int msgIndex, Bundle data) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Message msg = Message.obtain(null, msgIndex);
                msg.setData(data);
                mClients.get(i).send(msg);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        showNotification();
        isRunning = true;
    }

    private void showNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //CharSequence text = getText(R.string.service_started);
        // Set the icon, scrolling text and timestamp
        //Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
        // The PendingIntent to launch our activity if the user selects this notification
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DeviceControlActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        //notification.setLatestEventInfo(this, getText(R.string.service_label), text, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        //nm.notify(R.string.service_started, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If we get killed, after returning from here, stop
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");

        stopConnection();

        //nm.cancel(R.string.service_started); // Cancel the persistent notification.
        isRunning = false;
    }

    public static boolean isRunning() {
        return isRunning;
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
