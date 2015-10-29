package com.cabalry;

import android.os.Bundle;

import static com.cabalry.util.DB.RECORDINGS_URL;
import static com.cabalry.util.Utility.GetUserID;
import static com.cabalry.util.Utility.GetUserKey;

/**
 * Created by Conor Damery on 29/01/15.
 *
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
