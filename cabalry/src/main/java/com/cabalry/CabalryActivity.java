package com.cabalry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cabalry.db.DB;

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
        boolean login = Preferences.get(DB.LOGIN, false);
        if (!login) {
            LoginActivity.launch(this);
        } else {
            HomeActivity.launch(this);
        }
    }
}