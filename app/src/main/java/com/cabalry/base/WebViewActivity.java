package com.cabalry.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.cabalry.R;
import com.cabalry.app.HomeActivity;
import com.cabalry.util.TasksUtil.CheckNetworkTask;

import static android.content.DialogInterface.OnCancelListener;
import static android.content.DialogInterface.OnClickListener;

/**
 * WebViewActivity
 */
public abstract class WebViewActivity extends CabalryActivity.Compat {

    protected ProgressDialog progressBar;
    // Web view components.
    private WebView mWebView;
    private WebSettings mSettings;

    /**
     * Initializes activity components.
     */
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mSettings = mWebView.getSettings();
        mSettings.setSupportMultipleWindows(true);
        mSettings.setJavaScriptEnabled(true);
        mSettings.setLoadsImagesAutomatically(true);
        mSettings.setUseWideViewPort(true);
        mSettings.setLoadWithOverviewMode(true);


        // Set up client to get input from web view.
        setWebViewClient(new WebViewClient() {
            @Override
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

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up chrome client to enable prompt
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(getApplicationContext());
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        if (url != null) {
                            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        //super.onPageStarted(view, url, favicon);
                    }
                });
                //addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

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

    protected void setWebViewClient(WebViewClient webViewClient) {
        mWebView.setWebViewClient(webViewClient);
    }

    protected void setWebChromeClient(WebChromeClient webChromeClient) {
        mWebView.setWebChromeClient(webChromeClient);
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
    public void onDestroy() {
        super.onDestroy();
        progressBar.dismiss();
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
