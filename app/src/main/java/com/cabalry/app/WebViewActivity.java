package com.cabalry.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.DialogInterface.*;

import com.cabalry.R;
import com.cabalry.util.TasksUtil.*;

/**
 * Created by conor on 29/10/15.
 */
public abstract class WebViewActivity extends Activity {

    // Web view components.
    private WebView mWebView;
    private WebSettings mSettings;
    private ProgressDialog progressDialog;
    private CheckNetworkTask mCheckNetworkTask;

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Check if user still has connection
        if(mCheckNetworkTask == null) {
            mCheckNetworkTask = getCheckNetworkTask();
            mCheckNetworkTask.execute();
        }

        // Progress Dialog to show while web view is loading.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.prompt_webview_loading));
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
                builder.setPositiveButton(getString(R.string.action_enter), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm(editText.getText().toString());
                    }
                });

                builder.setNegativeButton(getString(R.string.action_cancel), new OnClickListener() {

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

        // Check if user still has connection
        if(mCheckNetworkTask == null) {
            mCheckNetworkTask = getCheckNetworkTask();
            mCheckNetworkTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private final CheckNetworkTask getCheckNetworkTask() {
        return new CheckNetworkTask(this) {
            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    // User has no available internet connection.
                    Toast.makeText(getApplicationContext(), getString(R.string.error_no_network),
                            Toast.LENGTH_LONG).show();

                    // return to login.
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

                mCheckNetworkTask = null;
            }
        };
    }

    protected WebView getWebView() { return mWebView; }
    protected WebSettings getSettings() { return mSettings; }
    protected ProgressDialog getProgressDialog() { return progressDialog; }
}
