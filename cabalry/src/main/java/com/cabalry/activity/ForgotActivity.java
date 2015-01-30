package com.cabalry.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.cabalry.R;
import com.cabalry.db.GlobalKeys;

/**
 * Created by conor on 29/01/15.
 */
public class ForgotActivity extends Activity {

    WebView webForgot;
    WebSettings forgotSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        // Set webview.
        webForgot = (WebView) findViewById(R.id.web_forgot);
        forgotSettings = webForgot.getSettings();
        forgotSettings.setJavaScriptEnabled(true);
        webForgot.loadUrl(GlobalKeys.FORGOT_URL);
    }
}
