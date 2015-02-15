package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Preferences;
import com.cabalry.utils.Util;

/**
 * Created by Conor Damery on 29/01/15.
 *
 *
 */
public class UserInfoActivity extends Activity {

    // Web view components.
    WebView webUserInfo;
    WebSettings userInfoSettings;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

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

        final int id = getIntent().getExtras().getInt("id");

        // Setup web view.
        webUserInfo = (WebView) findViewById(R.id.web_user_info);
        userInfoSettings = webUserInfo.getSettings();
        userInfoSettings.setJavaScriptEnabled(true);
        webUserInfo.loadUrl(GlobalKeys.VIEWUSER_URL + "?id=" + Preferences.getID() + "&auth_key=" + Preferences.getKey() + "&userId=" + id);

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
}
