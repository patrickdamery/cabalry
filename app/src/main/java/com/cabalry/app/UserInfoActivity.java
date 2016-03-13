package com.cabalry.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.cabalry.base.WebViewActivity;

import static com.cabalry.net.CabalryServer.*;
import static com.cabalry.util.PreferencesUtil.*;

/**
 * UserInfoActivity
 */
public class UserInfoActivity extends WebViewActivity {
    private static final String TAG = "UserInfoActivity";

    private String mParent;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int id = getIntent().getExtras().getInt("id");
        mParent = getIntent().getExtras().getString("parent");

        // Load Url.
        getWebView().loadUrl(VIEWUSER_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this) + "&userId=" + id);
    }

    @Override
    public void onBackPressed() {
        if (mParent != null) {
            switch (mParent) {
                case "map":
                    startActivity(new Intent(this, UserMapActivity.class));

                    break;
                case "alarm":
                    startActivity(new Intent(this, AlarmMapActivity.class));

                    break;
                default:
                    super.onBackPressed();
                    break;
            }

        } else {
            super.onBackPressed();
        }
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
