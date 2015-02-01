package com.cabalry.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cabalry.R;
import com.cabalry.service.TracerLocationService;
import com.cabalry.utils.Preferences;

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

                // Launch map.
                Intent map = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(map);
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
}
