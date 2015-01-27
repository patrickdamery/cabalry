package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.cabalry.*;
import com.cabalry.custom.Preferences;
import com.cabalry.db.GlobalKeys;

/**
 * Created by Robert Damery.
 * Contributed by Conor Damery.
 *
 * Handles app.
 */
public class HomeActivity extends Activity {
    //Attributes
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] mMenuTitles;
    private TypedArray mMenuIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mMenuIcons = getResources().obtainTypedArray(R.array.menu_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, R.id.drawer_item_text, mMenuTitles){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                            .getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                    View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

                    TextView text = (TextView)rowView.findViewById(R.id.drawer_item_text);
                    text.setText(mMenuTitles[position]);

                    ImageView icon = (ImageView)rowView.findViewById(R.id.drawer_item_icon);
                    icon.setImageDrawable(getResources().getDrawable(mMenuIcons.getResourceId(position, -1)));
                    return rowView;
                }
        });
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //enable home button and change title
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.app_name);
        //change action bar icon
        getActionBar().setIcon(R.drawable.ic_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ){
            public void onDrawerClosed(View view) {
                //TODO: Change ActionBar Up Indicator Image
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //TODO: Change ActionBar Up Indicator Image
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //We won't use a menu so there's no need to inflate it
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    /**
     * Action Listener For Drawer Menu Items
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Starts Intent of Selected Menu Item
     */
    private void selectItem(int position) {
        switch(position) {
            case 0:
                //Start Profile Activity
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                break;
            case 1:
                //Start Map Activity
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(map);
                break;
            case 2:
                //Start Recordings Activity
                Intent rec = new Intent(getApplicationContext(), RecordingsActivity.class);
                startActivity(rec);
                break;
            case 3:
                //Start Billing Activity
                Intent billing = new Intent(getApplicationContext(), BillingActivity.class);
                startActivity(billing);
                break;
            case 4:
                //Start Settings Activity
                Intent set = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(set);
                break;
            case 5:
                //Start Help Activity
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(GlobalKeys.HELP_URL)));
                break;
            case 6:
                Preferences.setBoolean(GlobalKeys.LOGIN, false);
                Preferences.setString(GlobalKeys.KEY, "");
                Preferences.setInt(GlobalKeys.ID, 0);
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                break;
        }
    }
}
