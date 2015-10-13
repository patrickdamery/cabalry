package com.cabalry;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by conor on 03/10/15.
 */
public class CabalryPrefs {

    public static final String PACKAGE_NAME = "CABALRY";
    public static final String USER_ID = "ID";
    public static final String USER_KEY = "KEY";
    public static final String USER_LOGIN = "LOGIN";

    public static final String FAKE_PASS = "FAKE";
    public static final String TIMER = "TIMER";
    public static final String TIMER_ENABLED = "TIMER_ENABLED";
    public static final String SILENT = "SILENT";
    public static final String ALERT_COUNT = "ALERT_COUNT";
    public static final String ALARM_RANGE = "ALERT_RANGE";

    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mEditor;

    public static void begin(Context context) {
        mPrefs = context.getSharedPreferences(PACKAGE_NAME, context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public static boolean isUserLogin() {
        return mPrefs.getBoolean(USER_LOGIN, false);
    }

    public static void userLogin(int id, String key) {
        mEditor.putInt(USER_ID, id);
        mEditor.putString(USER_KEY, key);
        mEditor.putBoolean(USER_LOGIN, true);
        mEditor.commit();
    }

    public static void userLogout() {
        mEditor.putInt(USER_ID, 0);
        mEditor.putString(USER_KEY, "");
        mEditor.putBoolean(USER_LOGIN, false);
        mEditor.commit();
    }
}
