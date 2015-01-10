package com.cabalry;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cabalry.db.DB;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity {

    private EditText tUsername, tPassword;
    private Button bRegister, bLogin;

    public static void launch(Activity currentActivity) {
        Intent indent = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(indent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Reset user login data.
        Preferences.set(DB.ID, 0);
        Preferences.set(DB.KEY, "");
        Preferences.set(DB.LOGIN, false);

        // Get layout buttons.
        bRegister = (Button) findViewById(R.id.register);
        bLogin = (Button) findViewById(R.id.login);
        tUsername = (EditText) findViewById(R.id.user_textfield);
        tPassword = (EditText) findViewById(R.id.pass_textfield);

        // Button listeners.
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegister();
            }
        });
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLogIn();
            }
        });
    }

    // Query data base.
    // If successful register and launch home.
    private void launchLogIn() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                // Get text field values.
                String u = tUsername.getText().toString();
                String p = tPassword.getText().toString();

                JSONObject result = null;

                try {
                    result = DB.login(u, p);
                    try {
                        boolean success = result.getBoolean(DB.SUCCESS);

                        if (success) {

                            // Register user.
                            int id = result.getInt(DB.ID);
                            String key = result.getString(DB.KEY);

                            Preferences.set(DB.ID, id);
                            Preferences.set(DB.KEY, key);
                            Preferences.set(DB.LOGIN, true);

                            // Go to home.
                            HomeActivity.launch(LoginActivity.this);
                        } else {

                            // Failed to login.
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /*
            @Override
            protected void onPostExecute(Void voids) {
                Toast.makeText(getApplicationContext(), "",
                        Toast.LENGTH_LONG).show();
            }
            */
        }.execute();
    }

    // Redirect user to @REGISTER_URL
    private void launchRegister() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(DB.REGISTER_URL)));
    }
}