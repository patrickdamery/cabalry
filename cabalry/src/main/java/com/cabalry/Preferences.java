package com.cabalry;

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
    public static String get(String key, String d) { return preferences.getString(key, d); }
    public static int get(String key, int d) { return preferences.getInt(key, d); }
    public static boolean get(String key, boolean d) { return preferences.getBoolean(key, d); }
    public static Context getContext() { return context; }
    public static SharedPreferences getPreferences() { return preferences; }

    // Setters.
    public static void set(String key, String v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putString(key, v);
        e.apply();
    }
    public static void set(String key, int v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putInt(key, v);
        e.apply();
    }
    public static void set(String key, boolean v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putBoolean(key, v);
        e.apply();
    }
    public static void set(String key, float v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putFloat(key, v);
        e.apply();
    }
    public static void set(String key, long v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putLong(key, v);
        e.apply();
    }
    @TargetApi(11)
    public static void set(String key, Set<String> v) {
        SharedPreferences.Editor e = preferences.edit();
        e.putStringSet(key, v);
        e.apply();
    }
}