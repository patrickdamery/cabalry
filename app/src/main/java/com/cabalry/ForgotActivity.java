package com.cabalry;

import android.content.Intent;
import android.os.Bundle;

import static com.cabalry.util.DB.FORGOT_URL;
import static com.cabalry.util.Utility.GetUserID;
import static com.cabalry.util.Utility.GetUserKey;

/**
 * Created by conor on 29/01/15.
 *
 * Activity which prompts forgot password screen.
 */
public class ForgotActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(FORGOT_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }

    @Override
    public void onBackPressed() {
        // Return to login
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
