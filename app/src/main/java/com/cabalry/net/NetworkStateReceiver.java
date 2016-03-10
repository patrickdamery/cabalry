package com.cabalry.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * NetworkStateReceiver
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //String status = NetworkUtil.getConnectivityStatusString(context);

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}
