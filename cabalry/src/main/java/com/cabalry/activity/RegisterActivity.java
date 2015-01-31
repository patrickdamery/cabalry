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
public class RegisterActivity extends Activity {

    WebView webRegister;
    WebSettings registerSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set webview.
        webRegister = (WebView) findViewById(R.id.web_register);
        registerSettings = webRegister.getSettings();
        registerSettings.setJavaScriptEnabled(true);
        webRegister.loadUrl(GlobalKeys.REGISTER_URL);
    }
}