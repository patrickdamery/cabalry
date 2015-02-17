package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cabalry.R;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Util;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by conor on 11/01/15.
 *
 * Activity that handles user login.
 */
public class LoginActivity extends Activity {

    // UI components.
    private EditText tUsername, tPassword;
    private Button bLogin;
    private TextView register, forgot;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        // Reset data.
        logout();

        // Get layout buttons.
        register = (TextView) findViewById(R.id.register);
        forgot = (TextView) findViewById(R.id.forgot);
        bLogin = (Button) findViewById(R.id.login);
        tUsername = (EditText) findViewById(R.id.user_textfield);
        tPassword = (EditText) findViewById(R.id.pass_textfield);

        // Action listeners.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegister();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgot();
            }
        });
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if user still has connection.
                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... voids) {

                        return !Util.hasActiveInternetConnection(getApplicationContext());
                    }

                    protected void onPostExecute(Boolean result) {
                        if(result) {
                            // User has no available internet connection.
                            Toast.makeText(getApplicationContext(), "Please connect to the internet and try again.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            login();
                        }
                    }
                }.execute();
            }
        });
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
     * Resets preferences data.
     */
    private void logout() {

        if(Preferences.getID() != 0 && !Preferences.getKey().equals("")) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    JSONObject result = DB.logout(Preferences.getID(), Preferences.getKey());

                    try {
                        if (!result.getBoolean(GlobalKeys.SUCCESS))
                            Logger.log("There was a problem when logging user out of server!");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        }

        // Reset user login data.
        Preferences.setInt(GlobalKeys.ID, 0);
        Preferences.setString(GlobalKeys.KEY, "");
        Preferences.setBoolean(GlobalKeys.LOGIN, false);
        Preferences.setString(GlobalKeys.PROPERTY_REG_ID, "");
    }

    /**
     * Tries to log user in according to username and password.
     */
    private void login() {

        // Get text field values.
        final String u = tUsername.getText().toString();
        final String p = tPassword.getText().toString();

        // Make sure user supplied username and password.
        if (u.equalsIgnoreCase("") || p.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please provide both username and password.",
                    Toast.LENGTH_LONG).show();
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    JSONObject result = null;

                    try {
                        result = DB.login(u, p);
                        try {
                            boolean success = result.getBoolean(GlobalKeys.SUCCESS);

                            if (success) {

                                // Register user.
                                int id = result.getInt(GlobalKeys.ID);
                                String key = result.getString(GlobalKeys.KEY);

                                Preferences.setInt(GlobalKeys.ID, id);
                                Preferences.setString(GlobalKeys.KEY, key);
                                Preferences.setBoolean(GlobalKeys.LOGIN, true);

                                // Go to home.
                                launchHome();
                            } else {
                                Preferences.setBoolean(GlobalKeys.LOGIN, false);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void voids) {

                    // If LOGIN is set to false that means user
                    // provided wrong username/password combination.
                    if(!Preferences.getBoolean(GlobalKeys.LOGIN)) {
                        Toast.makeText(getApplicationContext(), "Wrong username/password combination.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    /**
     * Launch home activity.
     */
    private void launchHome() {
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }

    /**
     * Launch register activity.
     */
    private void launchRegister() {
        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(register);
    }

    /**
     * Launch forgot activity.
     */
    private void launchForgot() {
        Intent forgot = new Intent(getApplicationContext(), ForgotActivity.class);
        startActivity(forgot);
    }
}