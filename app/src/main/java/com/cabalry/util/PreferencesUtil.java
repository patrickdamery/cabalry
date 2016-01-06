package com.cabalry.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import static com.cabalry.util.BluetoothUtil.*;

/**
 * PreferencesUtil
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
    public static final String PREF_DEVICE_CHARGE = "DEV_CHARGE";
    public static final String PREF_CACHED_ADDRESS = "CACHED_ADDRESS";

    public static final String PREF_FAKE_PASS = "FAKE";
    public static final String PREF_TIMER = "TIMER";
    public static final String PREF_TIMER_ENABLED = "TIMER_ENABLED";
    public static final String PREF_SILENT = "SILENT";
    public static final String PREF_ALERT_COUNT = "ALERT_COUNT";
    public static final String PREF_ALARM_RANGE = "ALERT_RANGE";

    public static int GetDeviceCharge(Context context) {
        return GetSharedPrefs(context).getInt(PREF_DEVICE_CHARGE, 0);
    }

    public static void SetDeviceCharge(Context context, int charge) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_DEVICE_CHARGE, charge);
        editor.commit();
    }

    public static String GetCachedAddress(Context context) {
        return GetSharedPrefs(context).getString(PREF_CACHED_ADDRESS, null);
    }

    public static void SetCachedAddress(Context context, String address) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putString(PREF_CACHED_ADDRESS, address);
        editor.commit();
    }

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
