package com.cabalry.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * BindableActivity
 */
public abstract class BindableActivity extends CabalryActivity.Compat {
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
        Log.i(TAG, "Binding ..");
        mMessenger = new Messenger(messengerHandler);
        mRegisterClientMsg = registerMsg;
        mUnregisterClientMsg = unregisterMsg;

        bindService(new Intent(this, serviceClass), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG, "Bounded");
    }

    protected void unbindFromService() {
        Log.i(TAG, "Unbinding ..");
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
            Log.i(TAG, "Unbounded");
        }
    }

    protected void sendMessageToService(int msgIndex, Bundle data) {
        if (msgIndex != mRegisterClientMsg && msgIndex != mUnregisterClientMsg) {
            if (mIsBound) {
                if (mService != null) {
                    try {
                        Message msg = Message.obtain(null, msgIndex);
                        msg.setData(data);
                        msg.replyTo = mMessenger;
                        mService.send(msg);

                    } catch (RemoteException e) {
                        Log.e(TAG, "Could not send message");
                    }
                }
            }
        }
    }
}
