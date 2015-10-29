package com.cabalry;

import android.os.Bundle;

import static com.cabalry.util.DB.BILLING_URL;
import static com.cabalry.util.Utility.GetUserID;
import static com.cabalry.util.Utility.GetUserKey;

/**
 * Created by Conor Damery on 29/01/15.
 *
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
