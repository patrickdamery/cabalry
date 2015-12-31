package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;

import static com.cabalry.util.DB.PROFILE_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * Created by conor on 29/01/15.
 *
 * Activity which prompts forgot password screen.
 */
public class ProfileActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(PROFILE_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}