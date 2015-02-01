package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.utils.Util;

/**
 * Created by Conor Damery on 29/01/15.
 *
 *
 */
public class UserInfoActivity extends Activity {

    // Web view components.
    WebView webUserInfo;
    WebSettings UserInfoSettings;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }

        // Setup web view.
        /*webUserInfo = (WebView) findViewById(R.id.web_user_info);
        UserInfoSettings = webUserInfo.getSettings();
        UserInfoSettings.setJavaScriptEnabled(true);
        webUserInfo.loadUrl(GlobalKeys.BILLING_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user still has connection.
        if(!Util.hasActiveInternetConnection(getApplicationContext())) {

            // User has no available internet connection.
            Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                    Toast.LENGTH_LONG).show();

            // return to login.
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            return;
        }
    }
}
