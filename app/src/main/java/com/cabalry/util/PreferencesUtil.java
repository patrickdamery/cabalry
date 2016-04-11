package com.cabalry.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * PreferencesUtil
 */
public class PreferencesUtil {
    private static final String TAG = "PreferencesUtil";

    public static final int TWO_MINUTES = 12000;

    public static final String PACKAGE_NAME = "CABALRY";
    public static final String PREF_USER_ID = "ID";
    public static final String PREF_USER_KEY = "KEY";
    public static final String PREF_USER_LOGIN = "LOGIN";
    public static final String PREF_ALARM_IP = "IP";
    public static final String PREF_ALARM_ID = "ALARMID";
    public static final String PREF_ALARM_USERID = "ALARM_USERID";
    public static final String PREF_LATITUDE = "LAT";
    public static final String PREF_LONGITUDE = "LNG";
    public static final String PREF_DEVICE_CHARGE = "DEV_CHARGE";
    public static final String PREF_CACHED_ADDRESS = "CACHED_ADDRESS";
    public static final String PREF_DRAWER_LEARNED = "NAVDR_LEARNED";
    public static final String PREF_GPS_CHECK = "GPS_CHECK";

    public static final String PREF_REG_ID = "REGID";
    public static final String PREF_APP_VERSION = "APPV";

    public static final String PREF_MAP_LATITUDE = "MLAT";
    public static final String PREF_MAP_LONGITUDE = "MLNG";
    public static final String PREF_MAP_ZOOM = "MZOOM";
    public static final String PREF_MAP_BEARING = "MBEARING";

    public static final String PREF_FAKE_PASS = "FAKE";
    public static final String PREF_TIMER = "TIMER";
    public static final String PREF_TIMER_ENABLED = "TIMER_ENABLED";
    public static final String PREF_SILENT = "SILENT";
    public static final String PREF_ALERT_COUNT = "ALERT_COUNT";
    public static final String PREF_ALARM_RANGE = "ALERT_RANGE";

    public static synchronized int GetDeviceCharge(Context context) {
        return GetSharedPrefs(context).getInt(PREF_DEVICE_CHARGE, 0);
    }

    public static synchronized void SetDeviceCharge(Context context, int charge) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_DEVICE_CHARGE, charge);
        editor.commit();
    }

    public static synchronized int GetTimerTime(Context context) {
        return GetSharedPrefs(context).getInt(PREF_TIMER, 0);
    }

    public static synchronized boolean GetTimerEnabled(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_TIMER_ENABLED, false);
    }

    public static synchronized void SetTimerEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putBoolean(PREF_TIMER_ENABLED, enabled);
        editor.commit();
    }

    public static synchronized String GetCachedAddress(Context context) {
        return GetSharedPrefs(context).getString(PREF_CACHED_ADDRESS, null);
    }

    public static synchronized void SetCachedAddress(Context context, String address) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putString(PREF_CACHED_ADDRESS, address);
        editor.commit();
    }

    public static synchronized String GetRegistrationID(Context context) {
        return GetSharedPrefs(context).getString(PREF_REG_ID, "");
    }

    public static synchronized void SetRegistrationID(Context context, String regid) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putString(PREF_REG_ID, regid);
        editor.commit();
    }

    public static synchronized String GetFakePassword(Context context) {
        return GetSharedPrefs(context).getString(PREF_FAKE_PASS, "");
    }

    public static synchronized int GetAppVersion(Context context) {
        return GetSharedPrefs(context).getInt(PREF_APP_VERSION, 0);
    }

    public static synchronized void SetAppVersion(Context context, int appVersion) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_APP_VERSION, appVersion);
        editor.commit();
    }

    public static synchronized void SetDrawerLearned(Context context, boolean learned) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putBoolean(PREF_DRAWER_LEARNED, learned);
        editor.commit();
    }

    public static synchronized boolean GetGPSChecked(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_GPS_CHECK, false);
    }

    public static synchronized void SetGPSChecked(Context context, boolean checked) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putBoolean(PREF_GPS_CHECK, checked);
        editor.commit();
    }

    public static synchronized int GetUserID(Context context) {
        return GetSharedPrefs(context).getInt(PREF_USER_ID, 0);
    }

    public static synchronized String GetUserKey(Context context) {
        return GetSharedPrefs(context).getString(PREF_USER_KEY, "");
    }

    public static synchronized int GetAlarmID(Context context) {
        return GetSharedPrefs(context).getInt(PREF_ALARM_ID, 0);
    }

    public static synchronized void SetAlarmID(Context context, int alarmid) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_ALARM_ID, alarmid);
        editor.commit();
    }

    public static synchronized int GetAlarmUserID(Context context) {
        return GetSharedPrefs(context).getInt(PREF_ALARM_USERID, 0);
    }

    public static synchronized void SetAlarmUserID(Context context, int userid) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_ALARM_USERID, userid);
        editor.commit();
    }

    public static synchronized String GetAlarmIP(Context context) {
        return GetSharedPrefs(context).getString(PREF_ALARM_IP, "");
    }

    public static synchronized void SetAlarmIP(Context context, String ip) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putString(PREF_ALARM_IP, ip);
        editor.commit();
    }

    public static synchronized boolean IsUserLogin(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_USER_LOGIN, false);
    }

    public static synchronized boolean IsDrawerLearned(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_DRAWER_LEARNED, false);
    }

    public static synchronized boolean IsSilent(Context context) {
        return GetSharedPrefs(context).getBoolean(PREF_SILENT, false);
    }

    public static synchronized void SaveSettings(Context context, Bundle settings) {
        Log.i(TAG, "Saving settings");
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putString(PREF_FAKE_PASS, settings.getString(PREF_FAKE_PASS));
        editor.putInt(PREF_TIMER, settings.getInt(PREF_TIMER));
        editor.putInt(PREF_ALERT_COUNT, settings.getInt(PREF_ALERT_COUNT));
        editor.putInt(PREF_ALARM_RANGE, settings.getInt(PREF_ALARM_RANGE));
        editor.putBoolean(PREF_SILENT, settings.getBoolean(PREF_SILENT));
        editor.commit();
    }

    public static synchronized void SaveMapState(Context context, CameraPosition cameraPosition) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putFloat(PREF_MAP_LATITUDE, (float) cameraPosition.target.latitude);
        editor.putFloat(PREF_MAP_LONGITUDE, (float) cameraPosition.target.longitude);
        editor.putFloat(PREF_MAP_ZOOM, cameraPosition.zoom);
        editor.putFloat(PREF_MAP_ZOOM, cameraPosition.bearing);
        editor.commit();
    }

    public static synchronized CameraPosition GetMapState(Context context) {
        SharedPreferences preferences = GetSharedPrefs(context);

        LatLng target = new LatLng(
                preferences.getFloat(PREF_MAP_LATITUDE, preferences.getFloat(PREF_LATITUDE, 0)),
                preferences.getFloat(PREF_MAP_LONGITUDE, preferences.getFloat(PREF_LONGITUDE, 0)));
        float zoom = preferences.getFloat(PREF_MAP_ZOOM, 17);
        float bearing = preferences.getFloat(PREF_MAP_BEARING, 0);

        return CameraPosition.builder()
                .target(target)
                .zoom(zoom)
                .bearing(bearing)
                .build();
    }

    public static synchronized void LoginUser(Context context, int id, String key) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, id);
        editor.putString(PREF_USER_KEY, key);
        editor.putBoolean(PREF_USER_LOGIN, true);
        editor.commit();
    }

    public static synchronized void LogoutUser(Context context) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, 0);
        editor.putString(PREF_USER_KEY, "");
        editor.putBoolean(PREF_USER_LOGIN, false);
        editor.commit();
    }

    public static synchronized void StoreLocation(Context context, LatLng location) {
        SharedPreferences.Editor editor = GetSharedPrefs(context).edit();
        editor.putFloat(PREF_LATITUDE, (float) location.latitude);
        editor.putFloat(PREF_LONGITUDE, (float) location.longitude);
        editor.commit();
    }

    public static synchronized LatLng GetLocation(Context context) {
        SharedPreferences prefs = GetSharedPrefs(context);
        return new LatLng(prefs.getFloat(PREF_LATITUDE, 0), prefs.getFloat(PREF_LONGITUDE, 0));
    }

    public static synchronized SharedPreferences GetSharedPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }
}
