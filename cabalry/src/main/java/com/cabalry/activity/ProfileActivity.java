package com.cabalry.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Preferences;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Util;

/**
 * Created by Conor Damery on 29/01/15.
 *
 * Activity which displays the user's profile info and also modify it.
 */
public class ProfileActivity extends Activity {

    // Web view components.
    WebView webProfile;
    WebSettings profileSettings;
    ProgressDialog pd;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        // Progress Dialog to show while web view is loading.
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.webview_loading));
        pd.show();

        // Setup web view.
        webProfile = (WebView) findViewById(R.id.web_profile);
        profileSettings = webProfile.getSettings();
        profileSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        webProfile.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
                pd.dismiss();
            }
        });

        // Load Url.
        webProfile.loadUrl(GlobalKeys.PROFILE_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
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

    @Override
    public void onBackPressed() {
        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }
}
