package com.cabalry.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.cabalry.R;
import com.cabalry.custom.Preferences;
import com.cabalry.db.GlobalKeys;


public class ProfileActivity extends Activity {

    WebView webProfile;
    WebSettings profileSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set webview.
        webProfile = (WebView) findViewById(R.id.web_profile);
        profileSettings = webProfile.getSettings();
        profileSettings.setJavaScriptEnabled(true);
        webProfile.loadUrl(GlobalKeys.PROFILE_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
    }
}
