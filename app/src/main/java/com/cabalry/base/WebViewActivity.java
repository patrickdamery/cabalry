package com.cabalry.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.DialogInterface.*;

import com.cabalry.R;
import com.cabalry.app.HomeActivity;
import com.cabalry.app.LoginActivity;
import com.cabalry.util.TasksUtil.*;

/**
 * WebViewActivity
 */
public abstract class WebViewActivity extends AppCompatActivity {

    // Web view components.
    private WebView mWebView;
    private WebSettings mSettings;
    private CheckNetworkTask mCheckNetworkTask;

    /**
     * Initializes activity components.
     */
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Check if user still has connection
        if (mCheckNetworkTask == null) {
            mCheckNetworkTask = getCheckNetworkTask();
            mCheckNetworkTask.execute();
        }

        // Setup web view.
        mWebView = (WebView) findViewById(R.id.web_cabalry);
        mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);

        // Set up client to get input from web view.
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Once the page has finished loading dismiss progress dialog.
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
        if (mCheckNetworkTask == null) {
            mCheckNetworkTask = getCheckNetworkTask();
            mCheckNetworkTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        // Return to home
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
        }
        return true;
    }

    private CheckNetworkTask getCheckNetworkTask() {
        return new CheckNetworkTask(this) {
            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
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

    protected WebView getWebView() {
        return mWebView;
    }

    protected WebSettings getSettings() {
        return mSettings;
    }
}
