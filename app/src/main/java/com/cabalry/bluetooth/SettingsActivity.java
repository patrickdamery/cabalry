package com.cabalry.bluetooth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Created by sash0k on 29.11.13.
 * Настройки приложения
 */
public final class SettingsActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.settings_activity);

        //final ActionBar bar = getSupportActionBar();
        //bar.setHomeButtonEnabled(true);
        //bar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        //setPrefenceTitle(getString(R.string.pref_commands_mode));
        //setPrefenceTitle(getString(R.string.pref_commands_ending));
    }
    // ============================================================================


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ============================================================================


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String value) {
        setPrefenceTitle(value);
    }
    // ============================================================================


    /**
     * Установка заголовка списка
     */
    private void setPrefenceTitle(String TAG) {
        final Preference preference = null;//findPreference(TAG);
        if (preference == null) return;
        if (preference instanceof ListPreference) {
            if (((ListPreference) preference).getEntry() == null) return;
            final String title = ((ListPreference) preference).getEntry().toString();
            preference.setTitle(title);
        }
    }
    // ============================================================================
}
