package com.cabalry.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static com.cabalry.util.BluetoothUtil.*;

/**
 * DeviceConnector
 */
public class DeviceConnector {
    private static final String TAG = "DeviceConnector";

    private int mState;

    private final BluetoothAdapter mBTAdapter;
    private final BluetoothListener mBTListener;
    private final BluetoothDevice mDevice;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    public DeviceConnector(BluetoothDevice device, BluetoothListener btListener) {
        mBTListener = btListener;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mDevice = device;

        mState = STATE_NOT_CONNECTED;
    }

    public synchronized void connect() {
        Log.d(TAG, "Connect to : " + mDevice);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                Log.d(TAG, "cancel mConnectThread");
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(mDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void stop() {
        Log.d(TAG, "Stop");

        if (mConnectThread != null) {
            Log.d(TAG, "cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NOT_CONNECTED);
    }

    private synchronized void setState(int state) {
        mState = state;
        mBTListener.onStateChange(state);
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connected(BluetoothSocket socket) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            Log.d(TAG, "cancel mConnectThread");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            Log.d(TAG, "cancel mConnectedThread");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_CONNECTED);

        // Callback to listener device name method
        String deviceName = (mDevice.getName() == null) ? mDevice.getAddress() : mDevice.getName();
        mBTListener.onDeviceName(deviceName);

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    private void connectionFailed() {
        Log.d(TAG, "connectionFailed");

        // Send a failure message back to the Activity
        mBTListener.onMessageToast("Connection Failed");
        setState(STATE_CONNECTION_FAILED);
    }

    private void connectionLost() {
        // Send a failure message back to the Activity
        mBTListener.onMessageToast("Connection Lost");
        setState(STATE_DISCONNECTED);
    }

    private class ConnectThread extends Thread {
        private static final String TAG = "ConnectThread";

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "create ConnectThread");
            mmDevice = device;
            mmSocket = CreateRfcommSocket(mmDevice);
        }

        public void run() {
            Log.d(TAG, "ConnectThread run");
            mBTAdapter.cancelDiscovery();
            if (mmSocket == null) {
                Log.d(TAG, "unable to connect to device, socket isn't created");
                connectionFailed();
                return;
            }

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (DeviceConnector.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }

        public void cancel() {
            Log.d(TAG, "ConnectThread cancel");

            if (mmSocket == null) {
                Log.d(TAG, "unable to close null socket");
                return;
            }
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private void updateDeviceStatus(String msg) {
        String[] msgs = msg.split("\\s+");

        for (String m : msgs) {
            if (m.length() > 4) {
                String sig = m.substring(0, 3);
                String state = m.substring(3, 4);
                String charge = m.substring(4);

                mBTListener.onStatusUpdate(sig, state, charge);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private static final String TAG = "ConnectedThread";

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");

            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            Log.i(TAG, "ConnectedThread run");
            byte[] buffer = new byte[512];
            int bytes;
            StringBuilder readMessage = new StringBuilder();
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readed = new String(buffer, 0, bytes);
                    readMessage.append(readed);

                    if (readed.contains("\n")) {
                        updateDeviceStatus(readMessage.toString());
                        readMessage.setLength(0);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
