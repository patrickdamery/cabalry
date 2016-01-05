package com.cabalry.app;

import android.os.Bundle;

import static com.cabalry.db.DataBase.USERINFO_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * UserInfoActivity
 */
public class UserInfoActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int id = getIntent().getExtras().getInt("id");

        // Load Url.
        getWebView().loadUrl(USERINFO_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this) + "&userId=" + id);
    }
}
