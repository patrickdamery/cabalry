package com.cabalry.utils;

import android.util.Log;

/**
 * Created by conor on 27/01/15.
 */
public class Logger {

    // Tag used on log messages.
    static final String TAG_LOG = "CABALRY_LOG";
    static final String TAG_ERROR = "CABALRY_ERROR";

    public static void log(String msg) {
        Log.i(TAG_LOG, msg);
    }
}
