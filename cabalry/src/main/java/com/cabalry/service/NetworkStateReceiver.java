package com.cabalry.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by conor on 04/02/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //String status = NetworkUtil.getConnectivityStatusString(context);

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}
