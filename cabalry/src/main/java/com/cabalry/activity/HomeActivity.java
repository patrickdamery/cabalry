package com.cabalry.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.cabalry.*;
import com.cabalry.service.AlarmService;
import com.cabalry.service.AlarmTimerService;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.LocationTracerService;
import com.cabalry.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Robert Damery.
 * Contributed by Conor Damery.
 *
 * Activity which acts as application home interface.
 * Contains menu of application screens and two types of alarm activation.
 *
 * Registers GCM id and stores it on the data base.
 */
public class HomeActivity extends Activity {

    // GCM required components.
    private GoogleCloudMessaging gcm;
    private String regid;

    // Attributes.
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuTitles;
    private TypedArray mMenuIcons;

    private ToggleButton bTimer;
    private Button bAlarm;

    private static boolean hasCheckedGps = false;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize preferences.
        Preferences.initialize(getApplicationContext());

        bTimer = (ToggleButton) findViewById(R.id.toggleTimer);
        bAlarm = (Button) findViewById(R.id.bAlarm);

        if(Preferences.getBoolean(GlobalKeys.TIMER_ENABLED)) {
            bTimer.setChecked(true);
        } else {
            bTimer.setChecked(false);
        }

        bTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Preferences.setBoolean(GlobalKeys.TIMER_ENABLED, bTimer.isChecked());

                if(Preferences.getBoolean(GlobalKeys.TIMER_ENABLED)) {
                    startService(new Intent(getApplicationContext(), AlarmTimerService.class));
                } else {
                    AlarmTimerService.stopTimer();
                    stopService(new Intent(HomeActivity.this, AlarmTimerService.class));
                }
            }
        });

        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... voids) {

                        JSONObject result = DB.checkBilling(Preferences.getID(), Preferences.getKey());

                        try {
                            if(result.getBoolean(GlobalKeys.SUCCESS))
                                return true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if(!result) {
                            Toast.makeText(getApplicationContext(), "Cannot start alarm please check your billing.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            startService(new Intent(getApplicationContext(), AlarmService.class));
                        }
                    }
                }.execute();
            }
        });

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {
                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                }
            }
        }.execute();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                JSONObject result = DB.checkBilling(Preferences.getID(), Preferences.getKey());

                try {
                    if(result.getBoolean(GlobalKeys.SUCCESS))
                        return true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result) {
                    Toast.makeText(getApplicationContext(), "There is a problem with your billing.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

        // Saves current settings.
        SettingsActivity.saveSettings();

        final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !hasCheckedGps) {
            alertNoGpsEnabled();
            hasCheckedGps = true;
        }

        // Start tracer service.
        Intent tracer = new Intent(getApplicationContext(), LocationTracerService.class);
        startService(tracer);

        // Register GCM.
        if(!registerGCM()) {
            Toast.makeText(getApplicationContext(), "No valid Google Play Services APK found.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mMenuIcons = getResources().obtainTypedArray(R.array.menu_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, R.id.drawer_item_text, mMenuTitles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

                TextView text = (TextView) rowView.findViewById(R.id.drawer_item_text);
                text.setText(mMenuTitles[position]);

                ImageView icon = (ImageView) rowView.findViewById(R.id.drawer_item_icon);
                icon.setImageDrawable(getResources().getDrawable(mMenuIcons.getResourceId(position, -1)));
                return rowView;
            }
        });
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable home button and change title.
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.app_name);

        // Change action bar icon.
        getActionBar().setIcon(R.drawable.ic_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                           // Host activity.
                mDrawerLayout,                  // DrawerLayout object.
                R.string.drawer_open,       // "open drawer" description for accessibility.
                R.string.drawer_close) {    // "close drawer" description for accessibility

            public void onDrawerClosed(View view) {
                // TODO: Change ActionBar Up Indicator Image.
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // TODO: Change ActionBar Up Indicator Image.
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void alertNoGpsEnabled() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app cannot function properly without GPS, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Calls drawer toggle item select callback.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // Minimize app.
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }

    /**
     * Checks if play services are still valid.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(Preferences.getBoolean(GlobalKeys.TIMER_ENABLED)) {
            bTimer.setChecked(true);
        } else {
            bTimer.setChecked(false);
        }

        // Start tracer service.
        Intent tracer = new Intent(getApplicationContext(), LocationTracerService.class);
        startService(tracer);

        // Check if user still has connection.
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                return !Util.hasActiveInternetConnection(getApplicationContext());
            }

            protected void onPostExecute(Boolean result) {

                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), "Please re-connect to the internet and login again.",
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(login);
                }
            }
        }.execute();

        checkPlayServices();
    }

    /**
     * Action listener for drawer menu items.
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Send callback to backend.
            selectItem(position);
        }
    }

    /**
     * Starts intent of selected menu item.
     */
    private void selectItem(int position) {

        // Call respective item.
        switch(position) {

            case 0:
                // Launch profile activity.
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                break;

            case 1:
                // Launch map activity.
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(map);
                break;

            case 2:
                // Launch recordings activity.
                Intent rec = new Intent(getApplicationContext(), RecordingsActivity.class);
                startActivity(rec);
                break;

            case 3:
                // Launch billing activity.
                Intent billing = new Intent(getApplicationContext(), BillingActivity.class);
                startActivity(billing);
                break;

            case 4:
                // Launch settings activity.
                Intent set = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(set);
                break;

            case 5:
                // Launch help url.
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(GlobalKeys.HELP_URL)));
                break;

            case 6:
                // Logout and launch login.
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                break;
        }
    }

    /**
     * Registers GCM id.
     */
    private boolean registerGCM() {

        // Application context.
        Context context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {

            // Initialize GCM fields.
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            // Handle registration id.
            if(regid.isEmpty()) {
                registerInBackground();
            }
            return true;
        }

        return false;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @return true if user has a compatible APK.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        GlobalKeys.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Logger.log("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = Preferences.getString(GlobalKeys.PROPERTY_REG_ID);

        if (registrationId.isEmpty()) {
            Logger.log("Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = Preferences.getInt(GlobalKeys.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Logger.log("App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * @return Application's version code from the PackageManager.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Should never happen.
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                Context context = getApplicationContext();
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GlobalKeys.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            protected void onPostExecute(String msg) {
                Logger.log(msg);
            }
        }.execute();
    }

    /**
     * Registers GCM on data base.
     */
    private void sendRegistrationIdToBackend() {
        JSONObject result = DB.updateGCM(regid, Preferences.getID(), Preferences.getKey());
        try {
            if(result.getBoolean(GlobalKeys.SUCCESS) != true) {
                Logger.log("Could not register GCM key on data base!");
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * SharedPreferences.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);

        Logger.log("Saving regId on app version " + appVersion);

        Preferences.setString(GlobalKeys.PROPERTY_REG_ID, regId);
        Preferences.setInt(GlobalKeys.PROPERTY_APP_VERSION, appVersion);
    }
}
