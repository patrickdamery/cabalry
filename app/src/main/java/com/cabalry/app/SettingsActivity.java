package com.cabalry.app;

import android.os.Bundle;
import android.util.Log;

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
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SettingsActivity", "HERE");
        new TasksUtil.SaveSettingsTask(getApplicationContext()).execute();
    }
}
