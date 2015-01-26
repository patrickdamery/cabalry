package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.cabalry.custom.Preferences;
import com.cabalry.db.GlobalKeys;

/**
 * Created by Conor Damery on 21/12/14.
 *
 * Launches app.
 */
public class CabalryActivity extends Activity {

    // Initialize and launch app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        // Check if user is logged in.
        boolean login = Preferences.getBoolean(GlobalKeys.LOGIN);
        if (!login) {
            Intent log = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(log);
        } else {
            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home);
        }
    }
}