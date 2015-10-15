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

    public static int GetUserID(Context context) {
        return getSharedPrefs(context).getInt(USER_ID, 0);
    }

    public static String GetUserKey(Context context) {
        return getSharedPrefs(context).getString(USER_KEY, "");
    }

    public static boolean IsUserLogin(Context context) {
        return getSharedPrefs(context).getBoolean(USER_LOGIN, false);
    }

    public static void UserLogin(Context context, int id, String key) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putInt(USER_ID, id);
        editor.putString(USER_KEY, key);
        editor.putBoolean(USER_LOGIN, true);
        editor.commit();
    }

    public static void UserLogout(Context context) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putInt(USER_ID, 0);
        editor.putString(USER_KEY, "");
        editor.putBoolean(USER_LOGIN, false);
        editor.commit();
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }
}
