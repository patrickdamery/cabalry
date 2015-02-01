package com.cabalry.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cabalry.R;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by conor on 29/01/15.
 */
public class AlarmNotificationActivity extends Activity {

    // UI components.
    private Button bIgnore;
    private Button bAccept;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        // Initializes the SharePreference instance.
        Preferences.initialize(getApplicationContext());

        // Start tracer service in case it's not running.
        Intent tracer = new Intent(getApplicationContext(), TracerLocationService.class);
        startService(tracer);

        // Setup UI components.
        bIgnore = (Button) findViewById(R.id.bIgnore);
        bIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ignoreAlarm();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AlarmNotificationActivity.this);
                builder.setMessage("Are you sure you want to ignore alarm?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        bAccept = (Button) findViewById(R.id.bAccept);
        bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Launch alarm map.
                Intent alarm = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(alarm);
            }
        });
    }

    @Override
    public void onBackPressed() { }

    private void ignoreAlarm() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JSONObject result = DB.ignoreAlarm(Preferences.getAlarmId(), Preferences.getID(), Preferences.getKey());

                try {
                    if(!result.getBoolean(GlobalKeys.SUCCESS)) {
                        Logger.log("Could not ignore alarm on server!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        Preferences.setCachedAlarmId(Preferences.getAlarmId());
        Preferences.setAlarmId(0);
        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }
}
