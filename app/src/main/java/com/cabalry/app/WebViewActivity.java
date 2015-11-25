package com.cabalry.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cabalry.R;

/**
 * Created by conor on 29/10/15.
 */
public abstract class WebViewActivity extends Activity {

    // Web view components.
    private WebView mWebView;
    private WebSettings mSettings;
    private ProgressDialog progressDialog;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabalry_webview);

        // Progress Dialog to show while web view is loading.
        progressDialog = new ProgressDialog(this);
        // TODO progressDialog.setMessage(getResources().getString(R.string.webview_loading));
        progressDialog.show();

        // Setup web view.
        mWebView = (WebView) findViewById(R.id.web_cabalry);
        mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // If a link is clicked load it inside mWebView.
                // This is so that the resend email link works correctly.
                mWebView.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: Check if user still has connection.
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    protected WebView getWebView() { return mWebView; }
    protected WebSettings getSettings() { return mSettings; }
    protected ProgressDialog getProgressDialog() { return progressDialog; }
}
