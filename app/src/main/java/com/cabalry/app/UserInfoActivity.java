package com.cabalry.app;

import android.os.Bundle;

import static com.cabalry.util.DB.USERINFO_URL;
import static com.cabalry.util.PrefsUtil.GetUserID;
import static com.cabalry.util.PrefsUtil.GetUserKey;

/**
 * Created by Conor Damery on 29/01/15.
 */
public class UserInfoActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(USERINFO_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}
