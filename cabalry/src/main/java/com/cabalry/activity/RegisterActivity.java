package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Util;

/**
 * Created by conor on 29/01/15.
 *
 * Activity which displays the user's profile info and also modify it.
 */
public class RegisterActivity extends Activity {

    // Web view components.
    WebView webRegister;
    WebSettings registerSettings;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        webRegister = (WebView) findViewById(R.id.web_register);
        registerSettings = webRegister.getSettings();
        registerSettings.setJavaScriptEnabled(true);
        webRegister.loadUrl(GlobalKeys.REGISTER_URL);
    }
}