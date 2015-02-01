package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Preferences;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Util;

/**
 * Created by conor on 29/01/15.
 *
 * Activity which displays the user's profile info and also modify it.
 */
public class SettingsActivity extends Activity {

    // Web view components.
    WebView webSettings;
    WebSettings settingsSettings;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }

        // Setup web view.
        webSettings = (WebView) findViewById(R.id.web_settings);
        settingsSettings = webSettings.getSettings();
        settingsSettings.setJavaScriptEnabled(true);
        webSettings.loadUrl(GlobalKeys.SETTINGS_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSettings();
    }

    public static void saveSettings() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                String fakePass = "1334";
                int timer = 5;
                int alertCount = 15;
                int range = 20;
                boolean silent = false;

                Preferences.setString(GlobalKeys.FAKE_PASS, fakePass);
                Preferences.setInt(GlobalKeys.TIMER, timer);
                Preferences.setInt(GlobalKeys.ALERT_COUNT, alertCount);
                Preferences.setInt(GlobalKeys.RANGE, range);
                Preferences.setBoolean(GlobalKeys.SILENT, silent);

                return null;
            }
        }.execute();
    }
}
