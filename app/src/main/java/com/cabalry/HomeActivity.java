package com.cabalry;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.cabalry.ui.NavigationDrawerFragment;

import static com.cabalry.util.Utility.*;

public class HomeActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        System.out.println("CABALRY - onCreate 1");

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        final String[] navDrawerStrings = new String[] {
                getString(R.string.title_profile),
                getString(R.string.title_map),
                getString(R.string.title_recordings),
                getString(R.string.title_billing),
                getString(R.string.title_settings),
                getString(R.string.title_help),
                getString(R.string.title_logout) };

        mNavigationDrawerFragment.setNavDrawerStrings(navDrawerStrings);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        System.out.println("CABALRY - onCreate 2");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    boolean start = true;
    public void onSectionAttached(int number) {
        if(start) {
            start = false;
        }
        else {
            System.out.println("CABALRY - onSectionAttached, "+number);
            Intent intent = null;
            switch (number) {
                case 1: intent = new Intent(getApplicationContext(), ProfileActivity.class); break;
                case 2: intent = new Intent(getApplicationContext(), UserMapActivity.class); break;
                case 3: intent = new Intent(getApplicationContext(), RecordingsActivity.class); break;
                case 4: intent = new Intent(getApplicationContext(), BillingActivity.class); break;
                case 5: intent = new Intent(getApplicationContext(), SettingsActivity.class); break;
                case 6: break; // TODO HelpActivity or something

                // Logout and redirect to login activity.
                case 7:
                    LogoutUser(this);
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    break;
            }

            if(intent != null)
                startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
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
