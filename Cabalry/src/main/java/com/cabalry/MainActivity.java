package com.cabalry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cabalry.db.DB;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "Cabalry";

    EditText username, password;
    Button register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (Button) findViewById(R.id.button2);
        login = (Button) findViewById(R.id.button);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);


        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String key = prefs.getString("key", "");
        if(key != "") {
            Intent intent = new Intent(getApplicationContext(), LoggedIn.class);
            startActivity(intent);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegister();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLogIn();
            }
        });
    }

    private void launchLogIn() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String u = username.getText().toString();
                String p = password.getText().toString();
                JSONObject result = new DB().login(u, p);

                try {
                    int success = result.getInt("success");

                    if (success == 1) {
                        //save key and id
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putInt("id", result.getInt("id"));
                        editor.putString("key", result.getString("key"));
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), LoggedIn.class);
                        startActivity(intent);

                    } else {
                        //Return some sort of error message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void launchRegister() {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
}