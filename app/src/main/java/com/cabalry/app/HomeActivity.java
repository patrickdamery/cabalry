package com.cabalry.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cabalry.R;
import com.cabalry.alarm.AlarmTimerService;
import com.cabalry.alarm.SilentAlarmService;
import com.cabalry.audio.AudioPlaybackService;
import com.cabalry.audio.AudioStreamService;
import com.cabalry.bluetooth.BluetoothService;
import com.cabalry.net.CabalryServer;
import com.cabalry.location.LocationUpdateService;
import com.cabalry.util.PreferencesUtil;
import com.cabalry.util.TasksUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.cabalry.util.PreferencesUtil.*;
import static com.cabalry.util.PreferencesUtil.SetRegistrationID;
import static com.cabalry.util.TasksUtil.*;
import static com.cabalry.net.CabalryServer.*;

/**
 * HomeActivity
 */
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    // GCM required components.
    private GoogleCloudMessaging gcm;
    private String regid;

    private DrawerLayout mDrawerLayout;
    @SuppressWarnings("deprecation")
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mActivityTitles;
    private TypedArray mActivityIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new TasksUtil.CheckNetworkTask(getApplicationContext()) {

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_no_network),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

        if (!BluetoothService.isRunning()) {
            startService(new Intent(this, BluetoothService.class));
        }

        if (!LocationUpdateService.isRunning()) {
            startService(new Intent(this, LocationUpdateService.class));
        }

        mTitle = mDrawerTitle = getTitle();
        mActivityTitles = getResources().getStringArray(R.array.nav_drawer_array);
        mActivityIcons = getResources().obtainTypedArray(R.array.nav_drawer_ic_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mActivityTitles) {
            @Override
            @SuppressLint("ViewHolder")
            public View getView(int position, View convertView, ViewGroup parent) {
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);

                ImageView item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
                TextView item_text = (TextView) convertView.findViewById(R.id.item_text);

                item_icon.setImageResource(mActivityIcons.getResourceId(position, -1));
                item_text.setText(mActivityTitles[position]);
                return convertView;
            }
        });

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        // noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        // noinspection deprecation
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
            mDrawerLayout.openDrawer(GravityCompat.START);
            PreferencesUtil.SetDrawerLearned(this, true);
        }

        Button bAlarm = (Button) findViewById(R.id.bAlarm);
        bAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlarm();
            }
        });

        final ToggleButton bTimer = (ToggleButton) findViewById(R.id.toggleTimer);
        if (GetTimerEnabled(getApplicationContext())) {
            bTimer.setChecked(true);
        }

        bTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bTimer.isChecked()) {
                    startService(new Intent(getApplicationContext(), AlarmTimerService.class));

                } else {
                    stopService(new Intent(getApplicationContext(), AlarmTimerService.class));
                }
            }
        });

        // Register GCM.
        if (!registerGCM()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_gplay),
                    Toast.LENGTH_LONG).show();
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !GetGPSChecked(this)) {
            alertNoGpsEnabled();
            SetGPSChecked(this, true);
        }
    }

    private void alertNoGpsEnabled() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.prompt_no_gps))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.prompt_yes),
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("unused")
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.prompt_no),
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("unused")
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void launchAlarm() {
        new StartAlarmTask(getApplicationContext()) {
            @Override
            protected void onResult(Boolean result) {
                if (result) {
                    startActivity(new Intent(getApplicationContext(), AlarmMapActivity.class));
                }
            }
        }.execute();
    }

    private void promptPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(getResources().getString(R.string.prompt_password));

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        alert.setView(input);

        alert.setPositiveButton(getResources().getString(R.string.prompt_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String value = input.getText().toString();

                        new CheckPasswordTask(getApplicationContext(), value) {
                            @Override
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    logout();
                                } else {
                                    promptPassword();
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_wrong_password),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }.execute();
                    }
                });

        alert.setNegativeButton(getResources().getString(R.string.prompt_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.openDrawer(GravityCompat.START);
                else
                    mDrawerLayout.closeDrawer(GravityCompat.START);

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
                intent = new Intent("android.intent.action.VIEW", Uri.parse(CabalryServer.HELP_URL));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;

            // Logout and redirect to login activity.
            case 7:
                if (GetAlarmUserID(this) == GetUserID(this)) {
                    promptPassword();
                } else {
                    logout();
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                break;
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    private void logout() {
        if (GetAlarmUserID(this) == GetUserID(this)) {
            new StopAlarmTask(getApplicationContext()).execute();
        }

        new UserLogoutTask(getApplicationContext()) {
            @Override
            protected void onResult(final Boolean success) {
                if (success) {
                    Context context = getApplicationContext();

                    if (!BluetoothService.isRunning())
                        stopService(new Intent(context, BluetoothService.class));

                    if (!LocationUpdateService.isRunning())
                        stopService(new Intent(context, LocationUpdateService.class));

                    //if (!AlarmTimerService.isRunning())
                    stopService(new Intent(context, AlarmTimerService.class));

                    //if (!SilentAlarmService.isRunning())
                    stopService(new Intent(context, SilentAlarmService.class));

                    if (AudioPlaybackService.isRunning()) {
                        AudioPlaybackService.stopAudioPlayback();
                        stopService(new Intent(context, AudioPlaybackService.class));
                    }

                    if (AudioStreamService.isRunning()) {
                        AudioStreamService.stopAudioStream();
                        stopService(new Intent(context, AudioStreamService.class));
                    }

                    SetAlarmID(context, 0);
                    SetAlarmUserID(context, 0);
                    SetGPSChecked(context, false);
                    SetDrawerLearned(context, false);
                    SetRegistrationID(context, "");
                    LogoutUser(context);

                    startActivity(new Intent(context, LoginActivity.class));

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_logout),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, final int position, long id) {
            new CheckNetworkTask(getApplicationContext()) {

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result) {
                        onSelectItem(position);

                    } else if(position == 7) { // logout is the exception
                        onSelectItem(position);

                    } else {
                        // handle no network
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_no_network),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        // noinspection ConstantConditions
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
            if (regid.isEmpty()) {
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
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
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
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = GetRegistrationID(getApplicationContext());

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = GetAppVersion(getApplicationContext());
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
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
                String msg;
                Context context = getApplicationContext();
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
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
                Log.i(TAG, msg);
            }
        }.execute();
    }

    /**
     * Registers GCM on data base.
     */
    private void sendRegistrationIdToBackend() {
        JSONObject result = UpdateGCM(regid, GetUserID(this), GetUserKey(this));
        try {
            if (!result.getBoolean(REQ_SUCCESS)) {
                Log.e(TAG, "Could not register GCM key on data base!");
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
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);

        Log.i(TAG, "Saving regId on app version " + appVersion);

        SetRegistrationID(getApplicationContext(), regId);
        SetAppVersion(getApplicationContext(), appVersion);
    }

}
