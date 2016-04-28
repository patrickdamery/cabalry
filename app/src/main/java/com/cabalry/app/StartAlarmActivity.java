package com.cabalry.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cabalry.R;
import com.cabalry.base.BindableActivity;

import static com.cabalry.util.MessageUtil.MSG_ALARM_START;
import static com.cabalry.util.MessageUtil.MSG_REGISTER_CLIENT;
import static com.cabalry.util.MessageUtil.MSG_UNREGISTER_CLIENT;

/**
 * StartAlarmActivity
 * <p/>
 * This activity should be used as a dummy activity to
 * activate an alarm an show a progress bar when there
 * are no activities available.
 */
public class StartAlarmActivity extends BindableActivity {
    static final String TAG = "StartAlarmActivity";

    static boolean active = false;

    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_alarm);
        active = true;

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage(getResources().getString(R.string.msg_loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Log.i(TAG, "Starting alarm ..");

        bindToService(CabalryAppService.class, new MessengerHandler(),
                MSG_REGISTER_CLIENT, MSG_UNREGISTER_CLIENT);

        progressBar.show();

        Intent intent = new Intent();
        intent.setAction("com.cabalry.action.ALARM_START");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;

        unbindFromService();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * MessengerHandler
     */
    private class MessengerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data == null)
                throw new NullPointerException("Error data is null!");

            switch (msg.what) {
                case MSG_ALARM_START:
                    Log.i(TAG, "MSG_ALARM_START");
                    if (active) {
                        progressBar.dismiss();
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
