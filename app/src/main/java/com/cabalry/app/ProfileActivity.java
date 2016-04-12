package com.cabalry.app;

import android.os.Bundle;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.CabalryServer.PROFILE_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * ProfileActivity
 * <p>
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
