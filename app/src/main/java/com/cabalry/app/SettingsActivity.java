package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;

import com.cabalry.base.WebViewActivity;
import com.cabalry.util.TasksUtil;

import static com.cabalry.net.CabalryServer.SETTINGS_URL;
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

    @Override
    public void onBackPressed() {
        // save settings
        new TasksUtil.SaveSettingsTask(getApplicationContext()).execute();

        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new TasksUtil.SaveSettingsTask(getApplicationContext()).execute();
    }
}
