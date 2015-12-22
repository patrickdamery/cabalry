package com.cabalry.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by conor on 14/10/15.
 */
public class PreferencesUtil {

    public static final int TWO_MINUTES = 12000;

    public static final String PACKAGE_NAME = "CABALRY";
    public static final String PREF_USER_ID = "ID";
    public static final String PREF_USER_KEY = "KEY";
    public static final String PREF_USER_LOGIN = "LOGIN";
    public static final String PREF_USER_IP = "IP";
    public static final String PREF_ALARM_ID = "ALARMID";
    public static final String PREF_LATITUDE = "LAT";
    public static final String PREF_LONGITUDE = "LNG";

    public static final String PREF_BUTTON1_MAC = "BTN1";

    public static final String PREF_FAKE_PASS = "FAKE";
    public static final String PREF_TIMER = "TIMER";
    public static final String PREF_TIMER_ENABLED = "TIMER_ENABLED";
    public static final String PREF_SILENT = "SILENT";
    public static final String PREF_ALERT_COUNT = "ALERT_COUNT";
    public static final String PREF_ALARM_RANGE = "ALERT_RANGE";

    public static int GetUserID(Context context) {
        return GetSharedPrefs(context).getInt(PREF_USER_ID, 0);
    }

    public static String GetUserKey(Context context) {
        return GetSharedPrefs(context).getString(PREF_USER_KEY, "");
    }

    public static int GetAlarmID(Context context) {
        return GetSharedPrefs(context).getInt(PREF_ALARM_ID, 0);
    }

    public static String GetUserIP(Context context) {
        return GetSharedPrefs(context).getString(PREF_USER_IP, "");
    }

    public static String GetMacButton(String button, Context context) {
        return GetSharedPrefs(context).getString(button, "");
    }

    public static void SetMacButton(String button, String mac, Context context) {
        GetSharedPrefs(context).edit().putString(button, mac);
    }

    public static boolean IsUserLogin(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_USER_LOGIN, false);
    }

    public static void LoginUser(Context context, int id, String key) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, id);
        editor.putString(PREF_USER_KEY, key);
        editor.putBoolean(PREF_USER_LOGIN, true);
        editor.commit();
    }

    public static void LogoutUser(Context context) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, 0);
        editor.putString(PREF_USER_KEY, "");
        editor.putBoolean(PREF_USER_LOGIN, false);
        editor.commit();
    }

    public static void StoreLocation(Context context, LatLng location) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putFloat(PREF_LATITUDE, (float) location.latitude);
        editor.putFloat(PREF_LONGITUDE, (float) location.longitude);
        editor.commit();
    }

    public static LatLng GetLocation(Context context) {
        SharedPreferences prefs = GetSharedPrefs(context);
        return new LatLng(prefs.getFloat(PREF_LATITUDE, 0), prefs.getFloat(PREF_LONGITUDE, 0));
    }

    public static SharedPreferences GetSharedPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }
}
