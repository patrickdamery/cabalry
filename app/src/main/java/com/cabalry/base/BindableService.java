package com.cabalry.base;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;

import static com.cabalry.util.MessageUtil.*;

/**
 * BindableService
 */
public abstract class BindableService extends Service {
    private static final String TAG = "BluetoothService";

    private static boolean isRunning = false;

    // Keeps track of all current registered clients
    private static ArrayList<Messenger> mClients = new ArrayList<>();

    // Target we publish for clients to send messages to IncomingHandler.
    private Messenger mMessenger;

    public class BaseMessengerHandler extends Handler {
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

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mMessenger == null) {
            Handler messengerHandler = getMessengerHandler();
            if (messengerHandler == null)
                throw new NullPointerException("messengerHandler is null!");
            else
                mMessenger = new Messenger(messengerHandler);
        }
        return mMessenger.getBinder();
    }

    protected Handler getMessengerHandler() {
        return new BaseMessengerHandler();
    }

    protected static void sendMessageToActivity(int msgIndex, Bundle data) {
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

    public static boolean isRunning() {
        return isRunning;
    }
}