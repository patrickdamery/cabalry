package com.cabalry.app;

import android.os.Bundle;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.DataBase.BILLING_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * BillingActivity
 * <p/>
 * Activity which displays the user's billing info and also modify it.
 */
public class BillingActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(BILLING_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}
