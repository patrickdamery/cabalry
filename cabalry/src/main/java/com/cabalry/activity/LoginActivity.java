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
import com.cabalry.utils.Preferences;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private EditText tUsername, tPassword;
    private Button bLogin;
    private TextView register, forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                login();
            }
        });
    }

    private void logout() {

        // Reset user login data.
        Preferences.setInt(GlobalKeys.ID, 0);
        Preferences.setString(GlobalKeys.KEY, "");
        Preferences.setBoolean(GlobalKeys.LOGIN, false);
        Preferences.setString(GlobalKeys.PROPERTY_REG_ID, "");
    }

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

    // Launch home.
    private void launchHome() {
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }

    // Launch register.
    private void launchRegister() {
        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(register);
    }

    // Launch forgot.
    private void launchForgot() {
        Intent forgot = new Intent(getApplicationContext(), ForgotActivity.class);
        startActivity(forgot);
    }
}