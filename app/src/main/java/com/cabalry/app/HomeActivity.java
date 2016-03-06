package com.cabalry.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cabalry.R;
import com.cabalry.bluetooth.BluetoothService;
import com.cabalry.db.DataBase;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.util.PreferencesUtil;

import static com.cabalry.util.PreferencesUtil.*;

/**
 * HomeActivity
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mActivityTitles;
    private int[] mActivityIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!BluetoothService.isRunning()) {
            startService(new Intent(this, BluetoothService.class));
        }

        if (!LocationUpdateService.isRunning()) {
            startService(new Intent(this, LocationUpdateService.class));
        }

        mTitle = mDrawerTitle = getTitle();
        mActivityTitles = getResources().getStringArray(R.array.nav_drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mActivityIcons = new int[8];
        mActivityIcons[0] = R.drawable.ic_profile;
        mActivityIcons[1] = R.drawable.ic_map;
        mActivityIcons[2] = R.drawable.ic_drawer;
        mActivityIcons[3] = R.drawable.ic_recordings;
        mActivityIcons[4] = R.drawable.ic_billing;
        mActivityIcons[5] = R.drawable.ic_settings;
        mActivityIcons[6] = R.drawable.ic_help;
        mActivityIcons[7] = R.drawable.ic_logout;

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mActivityTitles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);

                ImageView item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
                TextView item_text = (TextView) convertView.findViewById(R.id.item_text);

                item_icon.setImageResource(mActivityIcons[position]);
                item_text.setText(mActivityTitles[position]);
                return convertView;
            }
        });

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Open drawer for the first use of app
        if (!PreferencesUtil.IsDrawerLearned(this)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            PreferencesUtil.SetDrawerLearned(this, true);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                else
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void onSelectItem(int number) {
        Log.d(TAG, "onSelectItem(): " + number);
        Intent intent = null;
        switch (number) {

            // Launch respective activity
            case 0:
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                break;
            case 1:
                intent = new Intent(getApplicationContext(), UserMapActivity.class);
                break;
            case 2:
                intent = new Intent(getApplicationContext(), DeviceControlActivity.class);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), RecordingsActivity.class);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), BillingActivity.class);
                break;
            case 5:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                break;

            // Redirect to help url
            case 6:
                intent = new Intent("android.intent.action.VIEW", Uri.parse(DataBase.HELP_URL));
                break;

            // Logout and redirect to login activity.
            case 7:
                LogoutUser(this);
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            onSelectItem(position);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
