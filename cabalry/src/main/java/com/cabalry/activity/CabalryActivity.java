package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.cabalry.utils.Preferences;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Util;

/**
 * Created by Conor Damery on 21/12/14.
 *
 * Activity which launches app and checks whether the user
 * has logged in or is prompted to login screen.
 */
public class CabalryActivity extends Activity {

    // Initialize and launch app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        if(Preferences.getAlarmId() != 0) {

            // Launch alarm.
            Intent alarm = new Intent(getApplicationContext(), AlarmActivity.class);
            startActivity(alarm);
            return;
        }

        Preferences.setAlarmId(0);
        Preferences.setCachedAlarmId(0);

        // Saves current settings.
        SettingsActivity.saveSettings();

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please connect to the internet and login.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                    return;
                }
            }
        }.execute();

        // Check if user is logged in.
        boolean login = Preferences.getBoolean(GlobalKeys.LOGIN);
        if (!login) {

            // Launch login.
            Intent log = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(log);
        } else {

            // Launch home.
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
        }
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
                    Toast.makeText(getApplicationContext(), "Please connect to the internet and login.",
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