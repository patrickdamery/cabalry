package com.cabalry;

import android.content.Intent;
import android.os.Bundle;

import static com.cabalry.util.DB.REGISTER_URL;
import static com.cabalry.util.Utility.GetUserID;
import static com.cabalry.util.Utility.GetUserKey;

/**
 * Created by conor on 29/01/15.
 *
 * Activity which displays the user's profile info and also modify it.
 */
public class RegisterActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(REGISTER_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }

    @Override
    public void onBackPressed() {
        // Return to login
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}