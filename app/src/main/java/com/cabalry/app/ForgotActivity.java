package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.CabalryServer.FORGOT_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * ForgotActivity
 * <p/>
 * Activity which prompts forgot password screen.
 */
public class ForgotActivity extends WebViewActivity {

    public static boolean active = false;

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
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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
