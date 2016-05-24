package com.cabalry.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cabalry.base.WebViewActivity;
import com.cabalry.net.CabalryServer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.cabalry.net.CabalryServer.RECORDINGS_URL;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;

/**
 * RecordingsActivity
 * <p/>
 * Activity which displays a list of user's alarm recordings.
 */
public class RecordingsActivity extends WebViewActivity {

    /**
     * Initializes activity components.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Url.
        getWebView().loadUrl(RECORDINGS_URL + "?id=" + GetUserID(this) + "&auth_key=" + GetUserKey(this));
    }
}
