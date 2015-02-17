package com.cabalry.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.cabalry.R;
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
    ProgressDialog pd;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    return;
                }
            }
        }.execute();

        // Progress Dialog to show while web view is loading.
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.webview_loading));
        pd.show();

        // Setup web view.
        webSettings = (WebView) findViewById(R.id.web_settings);
        settingsSettings = webSettings.getSettings();
        settingsSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        webSettings.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
                pd.dismiss();
            }
        });

        // Load Url.
        webSettings.loadUrl(GlobalKeys.SETTINGS_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    return;
                }
            }
        }.execute();
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

    @Override
    public void onBackPressed() {
        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }
}
