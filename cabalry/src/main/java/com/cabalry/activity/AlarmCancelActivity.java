package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.cabalry.R;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;
import com.cabalry.service.AudioPlaybackService;
import com.cabalry.service.AudioStreamService;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by conor on 29/01/15.
 *
 * Activity that handles alarm de-activation.
 * Prompts user to password screen where he can type a real or fake password
 * which will either de-activate or pretend de-activation.
 */
public class AlarmCancelActivity extends Activity {

    // UI components.
    private Button bCancel;
    private Button bSubmit;
    private EditText tPassword;

    // Amount of times user has failed password.
    private int failedCount = 0;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_cancel);

        tPassword = (EditText) findViewById(R.id.tPassword);

        bCancel = (Button) findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // return to alarm.
                Intent alarm = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(alarm);
            }
        });

        bSubmit = (Button) findViewById(R.id.bSubmit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        realAlarmStop();

                        /*if(checkRealPassword()) {

                            // Really stop alarm.
                            realAlarmStop();

                        } else if(checkFakePassword()) {

                            // Fake stop alarm.
                            fakeAlarmStop();

                        } else {
                            failedCount++;
                            if(failedCount > 2) {
                                fakeAlarmStop();
                            }
                        }*/

                        return null;
                    }
                }.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private boolean checkRealPassword() {

        JSONObject result = DB.checkPass(Preferences.getID(), Preferences.getKey(), tPassword.getText().toString());

        try {
            if(result.getBoolean(GlobalKeys.SUCCESS)) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkFakePassword() {

        if(tPassword.getText().toString().equals(Preferences.getString(GlobalKeys.FAKE_PASS))) {
            return true;
        }
        return false;
    }

    private void realAlarmStop() {

        JSONObject result = DB.stopAlarm(Preferences.getAlarmId(), Preferences.getID(), Preferences.getKey());
        Preferences.setCachedAlarmId(0);
        Preferences.setAlarmId(0);

        AudioPlaybackService.stopAudioPlayback();
        AudioStreamService.stopAudioStream();

        stopService(new Intent(AlarmCancelActivity.this, AudioStreamService.class));
        stopService(new Intent(AlarmCancelActivity.this, AudioPlaybackService.class));

        try {
            if(!result.getBoolean(GlobalKeys.SUCCESS)) {
                Logger.log("Unable to stop alarm on server!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }

    private void fakeAlarmStop() {

        // return to home.
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
    }
}