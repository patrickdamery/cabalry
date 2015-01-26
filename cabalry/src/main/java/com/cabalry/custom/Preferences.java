package com.cabalry.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by conor on 22/12/14.
 *
 * Wrapper class for SharedPreferences.
 */
public class Preferences {

    private static Context context;
    private static SharedPreferences preferences;

    private Preferences() { }

    public static void initialize(Context ctxt) {
        context = ctxt;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Getters.
    public static String getString(String key) { return preferences.getString(key, null); }
    public static int getInt(String key) { return preferences.getInt(key, 0); }
    public static boolean getBoolean(String key) { return preferences.getBoolean(key, false); }
    public static float getFloat(String key) { return preferences.getFloat(key, 0f); }
    public static long getLong(String key) { return preferences.getLong(key, 0); }
    public static Set<String> getStringSet(String key) { return preferences.getStringSet(key, null); }

    public static Context getContext() { return context; }
    public static SharedPreferences getPreferences() { return preferences; }

    // Setters.
    public static void setString(String key, String v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putString(key, v);
        e.apply();
    }
    public static void setInt(String key, int v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putInt(key, v);
        e.apply();
    }
    public static void setBoolean(String key, boolean v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(key, v);
        e.apply();
    }
    public static void setFloat(String key, float v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putFloat(key, v);
        e.apply();
    }
    public static void setLong(String key, long v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putLong(key, v);
        e.apply();
    }
    @TargetApi(11)
    public static void setStringSet(String key, Set<String> v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putStringSet(key, v);
        e.apply();
    }
}