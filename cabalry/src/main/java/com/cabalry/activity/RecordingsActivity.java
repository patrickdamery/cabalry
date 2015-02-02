package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Preferences;
import com.cabalry.utils.Util;

/**
 * Created by Conor Damery on 29/01/15.
 *
 * Activity which displays a list of user's alarm recordings.
 */
public class RecordingsActivity extends Activity {

    // Web view components.
    WebView webRecordings;
    WebSettings recordingsSettings;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

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
        webRecordings = (WebView) findViewById(R.id.web_recordings);
        recordingsSettings = webRecordings.getSettings();
        recordingsSettings.setJavaScriptEnabled(true);
        webRecordings.loadUrl(GlobalKeys.RECORDINGS_URL + "?id=" + Preferences.getID() + "&auth_key=" + Preferences.getKey());

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
