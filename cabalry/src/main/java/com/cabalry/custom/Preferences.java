package com.cabalry.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.cabalry.db.GlobalKeys;

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
    public static Context getContext() { return context; }
    public static SharedPreferences getPreferences() { return preferences; }

    public static String getString(String key) { return preferences.getString(key, ""); }
    public static int getInt(String key) { return preferences.getInt(key, 0); }
    public static boolean getBoolean(String key) { return preferences.getBoolean(key, false); }
    public static float getFloat(String key) { return preferences.getFloat(key, 0f); }
    public static long getLong(String key) { return preferences.getLong(key, 0); }
    public static Set<String> getStringSet(String key) { return preferences.getStringSet(key, null); }

    public static String getString(String key, String d) { return preferences.getString(key, d); }
    public static int getInt(String key, int d) { return preferences.getInt(key, d); }
    public static boolean getBoolean(String key, boolean d) { return preferences.getBoolean(key, d); }
    public static float getFloat(String key, float d) { return preferences.getFloat(key, d); }
    public static long getLong(String key, long d) { return preferences.getLong(key, d); }
    public static Set<String> getStringSet(String key, Set<String> d) { return preferences.getStringSet(key, d); }

    public static int getID() { return Preferences.getInt(GlobalKeys.ID); }
    public static String getKey() { return Preferences.getString(GlobalKeys.KEY); }

    public static void setID(int id) { Preferences.setInt(GlobalKeys.ID, id); }
    public static void setKey(String key) { Preferences.setString(GlobalKeys.KEY, key); }

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