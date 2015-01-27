package com.cabalry.activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.cabalry.R;
import com.cabalry.custom.Preferences;
import com.cabalry.db.DB;
import com.cabalry.db.GlobalKeys;


public class BillingActivity extends Activity {

    WebView webBilling;
    WebSettings billingSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        //Set webview
        webBilling = (WebView) findViewById(R.id.web_billing);
        billingSettings = webBilling.getSettings();
        billingSettings.setJavaScriptEnabled(true);
        webBilling.loadUrl(GlobalKeys.BILLING_URL + "?id=" + Preferences.getInt(GlobalKeys.ID) + "&auth_key=" + Preferences.getString(GlobalKeys.KEY));
    }
}
