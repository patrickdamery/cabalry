package com.cabalry.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * BindableActivity
 */
public class BindableActivity extends AppCompatActivity {
    private static final String TAG = "BindableActivity";

    private Messenger mService = null;
    private boolean mIsBound;
    private Messenger mMessenger;

    private int mRegisterClientMsg, mUnregisterClientMsg;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.d(TAG, "onServiceConnected");
            try {
                Message msg = Message.obtain(null, mRegisterClientMsg);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not connect service!");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected");
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    protected void bindToService(Class serviceClass, Handler messengerHandler, int registerMsg, int unregisterMsg) {
        Log.d(TAG, "Binding ..");
        mMessenger = new Messenger(messengerHandler);
        mRegisterClientMsg = registerMsg;
        mUnregisterClientMsg = unregisterMsg;

        bindService(new Intent(this, serviceClass), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    protected void unbindFromService() {
        Log.d(TAG, "Unbinding ..");
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, mUnregisterClientMsg);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    Log.e(TAG, "Could not unbind service!");
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    protected void sendMessageToService(int msgIndex, String msg) {
        if (msgIndex != mRegisterClientMsg && msgIndex != mUnregisterClientMsg) {
            if (mIsBound) {
                if (mService != null) {
                    try {
                        Message m = Message.obtain(null, msgIndex, msg);
                        m.replyTo = mMessenger;
                        mService.send(m);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Could not send message");
                    }
                }
            }
        }
    }

    protected void sendMessageToService(int msgIndex) {
        if (msgIndex != mRegisterClientMsg && msgIndex != mUnregisterClientMsg) {
            if (mIsBound) {
                if (mService != null) {
                    try {
                        Message m = Message.obtain(null, msgIndex);
                        m.replyTo = mMessenger;
                        mService.send(m);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Could not send message");
                    }
                }
            }
        }
    }
}
