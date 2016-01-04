package com.cabalry.app;

import android.os.Bundle;

import static com.cabalry.util.DB.RECORDINGS_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * RecordingsActivity
 * <p/>
 * Activity which displays a list of user's alarm recordings.
 */
public class RecordingsActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(RECORDINGS_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}
