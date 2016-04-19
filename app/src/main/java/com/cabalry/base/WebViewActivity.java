package com.cabalry.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import static android.content.DialogInterface.*;

import com.cabalry.R;
import com.cabalry.app.HomeActivity;
import com.cabalry.util.TasksUtil.*;

/**
 * WebViewActivity
 */
public abstract class WebViewActivity extends CabalryActivity.Compat {

    // Web view components.
    private WebView mWebView;
    private WebSettings mSettings;

    ProgressDialog progressBar;

    /**
     * Initializes activity components.
     */
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                WebViewActivity.this.onBackPressed();
            }
        };
        progressBar.setCancelable(false);
        progressBar.setMessage(getResources().getString(R.string.msg_loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Get the Drawable custom_progressbar
        //Drawable customDrawable = getResources().getDrawable(R.drawable.cabalry_progressbar);

        // set the drawable as progress drawable
        //progressBar.setProgressDrawable(customDrawable);
        progressBar.show();

        // Setup web view.
        mWebView = (WebView) findViewById(R.id.web_cabalry);
        mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
                progressBar.dismiss();
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up chrome client to enable prompt
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setMessage(message);

                final EditText editText = new EditText(view.getContext());
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                builder.setView(editText);
                builder.setPositiveButton(getString(R.string.prompt_ok), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm(editText.getText().toString());
                    }
                });

                builder.setNegativeButton(getString(R.string.prompt_cancel), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });

                builder.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        result.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new CheckNetworkTask(getApplicationContext()) {

            @Override
            protected void onPostExecute(Boolean result) {

                if (!result) { // no internet return to home
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        progressBar.dismiss();
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    protected WebView getWebView() {
        return mWebView;
    }

    protected WebSettings getSettings() {
        return mSettings;
    }
}
