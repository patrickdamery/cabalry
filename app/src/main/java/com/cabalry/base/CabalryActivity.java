package com.cabalry.base;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.cabalry.app.CabalryApp;

/**
 * CabalryActivity
 */
public abstract class CabalryActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        CabalryApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CabalryApp.activityPaused();
    }

    public abstract static class Compat extends AppCompatActivity {

        @Override
        protected void onResume() {
            super.onResume();
            CabalryApp.activityResumed();
        }

        @Override
        protected void onPause() {
            super.onPause();
            CabalryApp.activityPaused();
        }
    }
}
