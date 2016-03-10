package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.CabalryServer.REGISTER_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * RegisterActivity
 * <p/>
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return true;
    }
}