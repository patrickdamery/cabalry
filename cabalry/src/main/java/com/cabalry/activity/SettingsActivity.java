package com.cabalry.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.cabalry.R;
import com.cabalry.custom.Preferences;
import com.cabalry.db.GlobalKeys;

public class SettingsActivity extends Activity {

    WebView webSettings;
    WebSettings settingsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set webview.
        webSettings = (WebView) findViewById(R.id.web_settings);
        settingsSettings = webSettings.getSettings();
        settingsSettings.setJavaScriptEnabled(true);
        webSettings.loadUrl(GlobalKeys.SETTINGS_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
    }
}
