package com.cabalry;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.cabalry.CabalryUtility.*;
import static com.cabalry.CabalryPrefs.*;

/**
 * Login screen for Cabalry app.
 *
 * Created by conor on 06/10/15.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    enum StartupActivity { LOGIN, HOME, MAP }
    StartupActivity mStartupActivity = StartupActivity.MAP; // [ Debug Only ]

    // Regex for email validation
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // Keep track of the login task to ensure we can cancel it if requested.
    private final UserLoginTask loginTask = new UserLoginTask() {
        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                UserLogin(LoginActivity.this, getID(), getKey());

                Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(home);
            } else {
                UserLogout(LoginActivity.this);

                mPasswordView.setError(getString(R.string.error_incorrect_login));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() { showProgress(false); }
    };

    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(IsUserLogin(this)) {
            gotoStartup(); // Redirects to mStartupActivity
        }

        // Set up the login form.
        mUserView = (AutoCompleteTextView) findViewById(R.id.user);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return true;
            }
        });

        Button mLogInButton = (Button) findViewById(R.id.login_button);
        mLogInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void gotoStartup() {
        Intent intent = null;

        switch(mStartupActivity) {
            case LOGIN : break;

            case HOME :
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                break;

            case MAP :
                intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                break;

            default :
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                break;
        }

        if(intent != null)
            startActivity(intent);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (loginTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid user.
        if(TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;

        } else if(!isValidUser(user)) {
            if(!isValidEmail(user)) {
                mUserView.setError(getString(R.string.error_invalid_email));
                focusView = mUserView;
                cancel = true;
            }
        }

        // Check for a valid password.
        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;

        } else if(!isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error. Focus on source.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and perform the user login attempt.
            showProgress(true);
            loginTask.setLoginInfo(user, password);
            loginTask.execute();
        }
    }

    private boolean isValidUser(String user) {
        return !user.contains("@") && !user.contains(".");
    }

    private boolean isValidEmail(String user) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(user).find();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // Optimization for progress spinner on Honeycomb MR2.
        // Use if available.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> users = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addUsersToAutoComplete(users);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        };

        int ADDRESS = 0;
    }


    private void addUsersToAutoComplete(List<String> userAddressCollection) {
        // Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, userAddressCollection);

        mUserView.setAdapter(adapter);
    }
}