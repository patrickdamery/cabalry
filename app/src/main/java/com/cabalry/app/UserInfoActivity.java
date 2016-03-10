package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.CabalryServer.USERINFO_URL;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
