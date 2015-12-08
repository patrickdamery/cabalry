package com.cabalry.app;

import android.os.Bundle;

import static com.cabalry.util.DB.SETTINGS_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * Created by conor on 29/01/15.
 *
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
