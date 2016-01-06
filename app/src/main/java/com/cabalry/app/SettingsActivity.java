package com.cabalry.app;

import android.os.Bundle;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.db.DataBase.SETTINGS_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * SettingsActivity
 * <p/>
 * Activity which displays the user's profile info and also modify it.
 */
public class SettingsActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(SETTINGS_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}
