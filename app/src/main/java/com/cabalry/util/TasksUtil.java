package com.cabalry.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.cabalry.app.AlarmHistoryActivity;
import com.cabalry.app.CabalryAppService;
import com.cabalry.base.HistoryItem;
import com.cabalry.base.MapUser;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import static com.cabalry.net.CabalryServer.CheckBilling;
import static com.cabalry.net.CabalryServer.CheckPassword;
import static com.cabalry.net.CabalryServer.GetAlarmInfo;
import static com.cabalry.net.CabalryServer.GetAlarmNearby;
import static com.cabalry.net.CabalryServer.GetNearby;
import static com.cabalry.net.CabalryServer.GetUserHistory;
import static com.cabalry.net.CabalryServer.GetUserInfo;
import static com.cabalry.net.CabalryServer.GetUserSettings;
import static com.cabalry.net.CabalryServer.IgnoreAlarm;
import static com.cabalry.net.CabalryServer.REQ_ALARM_ID;
import static com.cabalry.net.CabalryServer.REQ_ALARM_IP;
import static com.cabalry.net.CabalryServer.REQ_ALARM_LOCATION;
import static com.cabalry.net.CabalryServer.REQ_ALARM_START;
import static com.cabalry.net.CabalryServer.REQ_ALERT_COUNT;
import static com.cabalry.net.CabalryServer.REQ_FAIL_STATE;
import static com.cabalry.net.CabalryServer.REQ_FAKE_PASS;
import static com.cabalry.net.CabalryServer.REQ_ID;
import static com.cabalry.net.CabalryServer.REQ_LATITUDE;
import static com.cabalry.net.CabalryServer.REQ_LOCATION;
import static com.cabalry.net.CabalryServer.REQ_LONGITUDE;
import static com.cabalry.net.CabalryServer.REQ_RANGE;
import static com.cabalry.net.CabalryServer.REQ_SILENT;
import static com.cabalry.net.CabalryServer.REQ_SUCCESS;
import static com.cabalry.net.CabalryServer.REQ_TIMER;
import static com.cabalry.net.CabalryServer.REQ_USER_CAR;
import static com.cabalry.net.CabalryServer.REQ_USER_COLOR;
import static com.cabalry.net.CabalryServer.REQ_USER_ID;
import static com.cabalry.net.CabalryServer.REQ_USER_KEY;
import static com.cabalry.net.CabalryServer.REQ_USER_NAME;
import static com.cabalry.net.CabalryServer.RequestLogin;
import static com.cabalry.net.CabalryServer.RequestLogout;
import static com.cabalry.net.CabalryServer.StartAlarm;
import static com.cabalry.net.CabalryServer.StopAlarm;
import static com.cabalry.net.CabalryServer.UpdateListenerInfo;
import static com.cabalry.net.CabalryServer.UpdateUserLocation;
import static com.cabalry.net.CabalryServer.UserRequestType;
import static com.cabalry.util.PreferencesUtil.GetAlarmID;
import static com.cabalry.util.PreferencesUtil.GetAlarmUserID;
import static com.cabalry.util.PreferencesUtil.GetUserID;
import static com.cabalry.util.PreferencesUtil.GetUserKey;
import static com.cabalry.util.PreferencesUtil.PREF_ALARM_RANGE;
import static com.cabalry.util.PreferencesUtil.PREF_ALERT_COUNT;
import static com.cabalry.util.PreferencesUtil.PREF_FAKE_PASS;
import static com.cabalry.util.PreferencesUtil.PREF_SILENT;
import static com.cabalry.util.PreferencesUtil.PREF_TIMER;
import static com.cabalry.util.PreferencesUtil.SaveSettings;
import static com.cabalry.util.PreferencesUtil.SetAlarmID;
import static com.cabalry.util.PreferencesUtil.SetAlarmUserID;

/**
 * TasksUtil
 */
public class TasksUtil {

    private static boolean startAlarmRequested = false;

    /**
     * Represents an asynchronous task that collects locations
     * of nearby cabalry members
     */
    public static abstract class CollectUsersTask extends AsyncTask<Void, Void, Vector<MapUser>> {
        private static final String TAG = "CollectUsersTask";

        private Context mContext;
        private UserRequestType mType;
        private String failState;

        public CollectUsersTask(Context context) {
            mContext = context;
        }

        public void setType(UserRequestType type) {
            mType = type;
        }

        public String getFailState() {
            return failState;
        }

        @Override
        protected Vector<MapUser> doInBackground(Void... params) {
            Vector<MapUser> users = null;

            boolean success = false;

            try {
                JSONObject result;
                JSONArray locations = null;

                if (mType == UserRequestType.ALARM) {
                    result = GetAlarmNearby(GetAlarmID(mContext), GetUserID(mContext), GetUserKey(mContext));
                    try {
                        // Check if request was successful
                        success = result.getBoolean(REQ_SUCCESS);
                        if (success) {
                            // Check if request was successful
                            locations = result.getJSONArray(REQ_ALARM_LOCATION);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    result = GetNearby(GetUserID(mContext), GetUserKey(mContext));
                    try {
                        // Check if request was successful
                        success = result.getBoolean(REQ_SUCCESS);
                        if (success) {
                            // Check if request was successful
                            locations = result.getJSONArray(REQ_LOCATION);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (success) {
                    users = new Vector<>();

                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject location = locations.getJSONObject(i);

                        int id = location.getInt(REQ_USER_ID);
                        String name = location.getString(REQ_USER_NAME);
                        String car = location.getString(REQ_USER_CAR);
                        String color = location.getString(REQ_USER_COLOR);
                        double lat = location.getDouble(REQ_LATITUDE);
                        double lng = location.getDouble(REQ_LONGITUDE);

                        MapUser.UserType type = MapUser.UserType.NEARBY;
                        if (mType == UserRequestType.ALARM) {
                            if (GetAlarmUserID(mContext) == id)
                                type = MapUser.UserType.ALERT;
                            else
                                type = MapUser.UserType.ALERTED;
                        }

                        // Add location to list
                        users.add(new MapUser(id, name, car, color, lat, lng, type));

                        Log.i(TAG, "added user, id: " + id + ", name: " + name);
                    }
                } else {
                    failState = result.getString(REQ_FAIL_STATE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return users;
        }

        @Override
        protected abstract void onPostExecute(Vector<MapUser> users);
    }

    /**
     * Represents an asynchronous task that collects the users info
     */
    public static abstract class CollectUserInfoTask extends AsyncTask<Void, Void, MapUser> {

        private int mUserID;
        private int mID;
        private String mKey;

        private String failState;

        public void set(int userID, int id, String key) {
            mUserID = userID;
            mID = id;
            mKey = key;
        }

        public String getFailState() {
            return failState;
        }

        @Override
        protected MapUser doInBackground(Void... params) {
            MapUser user = null;

            JSONObject result;
            boolean success;

            try {
                result = GetUserInfo(mUserID, mID, mKey);

                try {
                    // Check if request was successful
                    success = result.getBoolean(REQ_SUCCESS);
                    if (success) {
                        String name = result.getString(REQ_USER_NAME);
                        String car = result.getString(REQ_USER_CAR);
                        String color = result.getString(REQ_USER_COLOR);
                        double lat = result.getDouble(REQ_LATITUDE);
                        double lng = result.getDouble(REQ_LONGITUDE);

                        user = new MapUser(mID, name, car, color, lat, lng, MapUser.UserType.USER);

                    } else {
                        failState = result.getString(REQ_FAIL_STATE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected abstract void onPostExecute(MapUser user);
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public static abstract class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "UserLoginTask";

        private String mUser;
        private String mPassword;

        private int mID;
        private String mKey;

        public void setLoginInfo(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        public int getID() {
            return mID;
        }

        public String getKey() {
            return mKey;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject result;
            boolean success = false;

            try {
                result = RequestLogin(mUser, mPassword);
                try {
                    success = result.getBoolean(REQ_SUCCESS);
                    Log.i(TAG, "Success: " + success);

                    if (success) {

                        // Register user.
                        mID = result.getInt(REQ_USER_ID);
                        mKey = result.getString(REQ_USER_KEY);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return success;
        }

        @Override
        protected abstract void onPostExecute(final Boolean success);
    }

    /**
     * Represents an asynchronous logout task.
     */
    public static abstract class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "UserLogoutTask";

        private static int count = 0;
        private UserLogoutTask instance;

        private Context mContext;

        public UserLogoutTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
            instance = this;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject result;
            boolean success = false;

            try {
                result = RequestLogout(GetUserID(mContext), GetUserKey(mContext));
                try {
                    success = result.getBoolean(REQ_SUCCESS);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                count++;
                if (count >= 3) {
                    Log.e(TAG, "Unable to logout on server!");
                    count = 0;
                    onResult(result);

                } else {
                    new UserLogoutTask(mContext) {
                        @Override
                        protected void onResult(Boolean result) {
                            instance.onResult(result);
                        }
                    }.execute();
                }

            } else {
                onResult(result);
            }
        }

        protected abstract void onResult(Boolean result);
    }

    /**
     * Represents an asynchronous task that starts an alarm.
     */
    public static abstract class StartAlarmTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "StartAlarmTask";

        private static int count = 0;
        private StartAlarmTask instance;

        private Context mContext;

        public StartAlarmTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
            instance = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (startAlarmRequested) {
                this.cancel(true);
            }

            startAlarmRequested = true;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground");

            JSONObject result = StartAlarm(GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    int alarmID = result.getInt(REQ_ALARM_ID);

                    SetAlarmID(mContext, alarmID);
                    SetAlarmUserID(mContext, GetUserID(mContext));

                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SetAlarmID(mContext, 0);
            Log.e(TAG, "Could not start alarm!");

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute result: " + result);

            if (!result) {
                count++;
                if (count >= 3) {
                    startAlarmRequested = false;

                    Log.e(TAG, "Unable to start alarm on server!");
                    count = 0;
                    onResult(result);

                } else { // try again
                    Log.i(TAG, "onPostExecute could not start alarm, trying again ..");
                    new StartAlarmTask(mContext) {
                        @Override
                        protected void onResult(Boolean result) {
                            instance.onResult(result);
                        }
                    }.execute();
                }

            } else {
                startAlarmRequested = false;

                Log.i(TAG, "Alarm started");
                onResult(result);
            }
        }

        protected abstract void onResult(Boolean result);
    }

    /**
     * Represents an asynchronous task that ignores an alarm.
     */
    public static class IgnoreAlarmTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "StopAlarmTask";

        private Context mContext;
        private int count;

        public IgnoreAlarmTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
            count = 0;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject result = IgnoreAlarm(GetAlarmID(mContext), GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    Log.i(TAG, "Alarm ignored successfully");

                    SetAlarmID(mContext, 0);
                    SetAlarmUserID(mContext, 0);

                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                count++;
                if (count > 3)
                    Log.e(TAG, "Unable to ignore alarm on server!");
                else
                    new IgnoreAlarmTask(mContext).execute();
            }
        }
    }

    /**
     * Represents an asynchronous task that stops an alarm.
     */
    public static class StopAlarmTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "StopAlarmTask";

        private Context mContext;
        private int count;

        public StopAlarmTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
            count = 0;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject result = StopAlarm(GetAlarmID(mContext), GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    Log.i(TAG, "Alarm stopped successfully");

                    SetAlarmID(mContext, 0);
                    SetAlarmUserID(mContext, 0);

                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                count++;
                if (count > 3)
                    Log.e(TAG, "Unable to stop alarm on server!");
                else
                    new StopAlarmTask(mContext).execute();
            }
        }
    }

    public static abstract class GetAlarmHistory extends AsyncTask<Void, Void, ArrayList<HistoryItem>> {
        private static final String TAG = "GetAlarmHistory";

        private Context mContext;

        public GetAlarmHistory(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
        }

        @Override
        protected ArrayList<HistoryItem> doInBackground(Void... voids) {
            ArrayList<HistoryItem> historyItems = new ArrayList<>();
            JSONObject result = GetUserHistory(GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    Log.i(TAG, "success");

                    JSONArray items = result.getJSONArray("history");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = (JSONObject) items.get(i);

                        int alarmId = obj.getInt("alarmId");
                        int alarmUserId = obj.getInt("alarmUserId");
                        String userName = obj.getString("userName");
                        String timestamp = obj.getString("timestamp");
                        String state = obj.getString("state");

                        Log.i(TAG, "alarmId: " + alarmId);
                        Log.i(TAG, "alarmUserId: " + alarmUserId);
                        Log.i(TAG, "userName: " + userName);
                        Log.i(TAG, "timestamp: " + timestamp);
                        Log.i(TAG, "state: " + state);

                        historyItems.add(new HistoryItem(userName, alarmUserId, alarmId, timestamp, state));
                    }
                } else {
                    Log.i(TAG, "unsuccess");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return historyItems;
        }

        @Override
        protected abstract void onPostExecute(ArrayList<HistoryItem> result);
    }

    /**
     * Represents an asynchronous task that returns alarm info
     */
    public static abstract class GetAlarmInfoTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "GetAlarmInfoTask";

        private static int count = 0;
        private GetAlarmInfoTask instance;

        private Context mContext;
        private int mID;
        private String mIP;
        private String mState;
        private String mStart;

        public GetAlarmInfoTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
        }

        public int getID() {
            return mID;
        }

        public String getIP() {
            return mIP;
        }

        public String getState() {
            return mState;
        }

        public String getStart() {
            return mStart;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject result = GetAlarmInfo(GetAlarmID(mContext), GetUserID(mContext), GetUserKey(mContext));

            try {
                Log.i(TAG, "REQ_SUCCESS: " + result.getBoolean(REQ_SUCCESS));

                if (result.getBoolean(REQ_SUCCESS)) {
                    mStart = result.getString(REQ_ALARM_START);
                    mIP = result.getString(REQ_ALARM_IP);
                    mState = result.getString(REQ_ALARM_IP);
                    mID = result.getInt(REQ_ID);

                    Log.i(TAG, "start: " + mStart);
                    Log.i(TAG, "ip: " + mIP);
                    Log.i(TAG, "state: " + mState);
                    Log.i(TAG, "id: " + mID);

                    if (mIP == null) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute result: " + result);

            if (!result) {
                count++;
                if (count >= 3) {
                    Log.e(TAG, "Unable to get alarm info!");
                    count = 0;
                    onResult(result);

                } else { // try again
                    Log.i(TAG, "onPostExecute could not start alarm, trying again ..");
                    new StartAlarmTask(mContext) {
                        @Override
                        protected void onResult(Boolean result) {
                            instance.onResult(result);
                        }
                    }.execute();
                }

            } else {
                onResult(result);
            }
        }

        protected abstract void onResult(Boolean result);
    }

    /**
     * Represents an asynchronous task to check billing state.
     */
    public static abstract class CheckBillingTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "CheckBillingTask";

        private Context mContext;

        public CheckBillingTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject result = CheckBilling(GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS))
                    return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected abstract void onPostExecute(Boolean result);
    }

    /**
     * Represents an asynchronous task which updates the user location on the data base.
     */
    public static class UpdateLocationTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UpdateLocationTask";

        Context mContext;
        LatLng mLocation;

        public UpdateLocationTask(Context context, LatLng location) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
            mLocation = location;
        }

        @Override
        public Void doInBackground(Void... voids) {
            if (mLocation != null) {
                JSONObject result = UpdateUserLocation(mLocation.latitude, mLocation.longitude,
                        GetUserID(mContext), GetUserKey(mContext));

                Log.i(TAG, "doInBackground");

                try {
                    if (result != null) {
                        if (!result.getBoolean(REQ_SUCCESS)) {
                            Log.e(TAG, "Can't update, there's a problem connecting to server!");
                        }
                    } else {
                        Log.e(TAG, "Can't update, there's a problem connecting to server!");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Can't update, location is null!");
            }

            return null;
        }
    }

    /**
     * Represents an asynchronous
     */
    public static class UpdateListenerInfoTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UpdateListenerInfoTask";

        private int mID;
        private String mKey;
        private int mAlarmID;
        private int mPort;

        public void setListenerInfo(int id, String key, int alarmID, int port) {
            mID = id;
            mKey = key;
            mAlarmID = alarmID;
            mPort = port;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject result;
            boolean success;

            try {
                result = UpdateListenerInfo(mAlarmID, mID, mKey, mPort);
                try {
                    success = result.getBoolean(REQ_SUCCESS);

                    if (!success) {
                        Log.e(TAG, "Could not update listener info!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Represents an asynchronous task that validates the user password.
     */
    public static abstract class CheckPasswordTask extends AsyncTask<Void, Void, Boolean> {

        Context mContext;
        String mPassword;

        public CheckPasswordTask(Context context, String password) {
            mContext = context;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject result = CheckPassword(GetUserID(mContext), GetUserKey(mContext), mPassword);

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected abstract void onPostExecute(Boolean result);
    }

    /**
     * Represents an asynchronous task that checks if internet connection is available.
     */
    public static abstract class CheckNetworkTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "CheckNetworkTask";

        private Context mContext;

        public CheckNetworkTask(Context context) {
            mContext = context;
        }

        private boolean hasActiveInternetConnection(Context context) {
            if (isNetworkAvailable(context)) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.e(TAG, "Error checking internet connection");
                }
            } else {
                Log.w(TAG, "No network available!");
            }
            return false;
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return hasActiveInternetConnection(mContext);
        }

        @Override
        protected abstract void onPostExecute(final Boolean result);

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Represents an asynchronous task that saves user settings to preferences.
     */
    public static class SaveSettingsTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "SaveSettingsTask";

        Context mContext;

        public SaveSettingsTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONObject result = GetUserSettings(GetUserID(mContext), GetUserKey(mContext));
            Log.i(TAG, "HERE");

            try {
                if (result.getBoolean(REQ_SUCCESS)) {

                    Bundle settings = new Bundle();
                    settings.putString(PREF_FAKE_PASS, result.getString(REQ_FAKE_PASS));
                    settings.putInt(PREF_TIMER, result.getInt(REQ_TIMER));
                    settings.putInt(PREF_ALERT_COUNT, result.getInt(REQ_ALERT_COUNT));
                    settings.putInt(PREF_ALARM_RANGE, result.getInt(REQ_RANGE));
                    settings.putBoolean(PREF_SILENT, result.getBoolean(REQ_SILENT));

                    Log.i(TAG, "Saving settings: " + settings.toString());

                    SaveSettings(mContext, settings);
                } else {
                    Log.e(TAG, "Error while getting settings!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Represents an asynchronous task that returns a user's info.
     */
    public static abstract class GetUserInfoTask extends AsyncTask<Void, Void, Bundle> {
        private static final String TAG = "GetUserInfoTask";

        Context mContext;
        int mUserID;

        public GetUserInfoTask(Context context, int userID) {
            mContext = context;
            mUserID = userID;
        }

        @Override
        protected Bundle doInBackground(Void... voids) {

            JSONObject result = GetUserInfo(GetUserID(mContext), mUserID, GetUserKey(mContext));
            Bundle info = null;

            try {
                if (result.getBoolean(REQ_SUCCESS)) {
                    info = new Bundle();

                    info.putString(REQ_USER_NAME, result.getString(REQ_USER_NAME));
                } else {
                    Log.e(TAG, "Error while getting settings!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return info;
        }

        @Override
        protected abstract void onPostExecute(Bundle result);
    }
}
