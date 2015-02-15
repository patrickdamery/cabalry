package com.cabalry.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    ProgressDialog pd;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    return;
                }
            }
        }.execute();

        // Progress Dialog to show while web view is loading.
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.webview_loading));
        pd.show();

        // Setup web view.
        webRecordings = (WebView) findViewById(R.id.web_recordings);
        recordingsSettings = webRecordings.getSettings();
        recordingsSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        webRecordings.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
                pd.dismiss();
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // If a link is clicked on open in default browser.
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
            }
        });

        // Load Url.
        webRecordings.loadUrl(GlobalKeys.RECORDINGS_URL + "?id=" + Preferences.getID() + "&auth_key=" + Preferences.getKey());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    return;
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }
}
