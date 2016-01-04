package com.cabalry.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.cabalry.R;
import com.cabalry.location.LocationUpdateManager;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.ui.NavigationDrawerFragment;
import com.cabalry.util.DB;

import static com.cabalry.util.PreferencesUtil.*;

public class HomeActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "HomeActivity";

    private static Intent mLocationUpdateIntent;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private boolean mStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mStart = true;

        if(mLocationUpdateIntent == null) {
            // Start location update service.
            mLocationUpdateIntent = new Intent(getApplicationContext(), LocationUpdateService.class);
            startService(mLocationUpdateIntent);
        }
        else {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationUpdateManager.Instance(this).resetProvider(manager);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final String[] navDrawerStrings = new String[] {
                getString(R.string.title_nav_profile),
                getString(R.string.title_nav_map),
                getString(R.string.title_nav_devices),
                getString(R.string.title_nav_recordings),
                getString(R.string.title_nav_billing),
                getString(R.string.title_nav_settings),
                getString(R.string.title_nav_help),
                getString(R.string.title_nav_logout) };

        mNavigationDrawerFragment.setNavDrawerStrings(navDrawerStrings);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        if(!mStart) {
            Log.d(TAG, "onSectionAttached(): "+number);
            Intent intent = null;
            switch (number) {

                // Launch respective activity
                case 1: intent = new Intent(getApplicationContext(), ProfileActivity.class);        break;
                case 2: intent = new Intent(getApplicationContext(), UserMapActivity.class);        break;
                case 3: intent = new Intent(getApplicationContext(), DeviceControlActivity.class);  break;
                case 4: intent = new Intent(getApplicationContext(), RecordingsActivity.class);     break;
                case 5: intent = new Intent(getApplicationContext(), BillingActivity.class);        break;
                case 6: intent = new Intent(getApplicationContext(), SettingsActivity.class);       break;

                // Redirect to help url
                case 7: intent = new Intent("android.intent.action.VIEW", Uri.parse(DB.HELP_URL));  break;

                // Logout and redirect to login activity.
                case 8:
                    LogoutUser(this);
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    break;
            }

            if(intent != null) {
                startActivity(intent);
                finish();
            }
        }
        else mStart = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            // TODO look into this
            //getMenuInflater().inflate(R.menu.main, menu);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "SEC_NUM";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_home, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
