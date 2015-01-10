package com.cabalry;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.cabalry.db.DB;

/**
 * Created by Robert Damery.
 * Contributed by Conor Damery.
 *
 * Handles app.
 */
public class HomeActivity extends ActionBarActivity {

    public static void launch(Activity currentActivity) {
        Intent indent = new Intent(currentActivity, HomeActivity.class);
        currentActivity.startActivity(indent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.mMap: MapActivity.launch(this); break;
            case R.id.mSettings: break;
            case R.id.mProfile: break;
            case R.id.mLogout: LoginActivity.launch(this); break;
        }
        return true;
    }
}
