package com.cabalry.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.cabalry.base.MapUser;
import com.cabalry.net.DataBase;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.cabalry.net.DataBase.*;
import static com.cabalry.util.PreferencesUtil.*;

/**
 * TasksUtil
 */
public class TasksUtil {

    /**
     * Represents an asynchronous task that collects locations
     * of nearby cabalry members
     */
    public static abstract class CollectUsersTask extends AsyncTask<Void, Void, Vector<MapUser>> {

        private int mID;
        private String mKey;
        private UserRequestType mType;
        private int mAlarmID;

        private String failState;

        public void setCollectInfo(int id, String key) {
            setCollectInfo(id, key, UserRequestType.NEARBY);
        }

        public void setCollectInfo(int id, String key, int alarmID) {
            setCollectInfo(id, key, UserRequestType.ALARM);
            mAlarmID = alarmID;
        }

        public void setCollectInfo(int id, String key, UserRequestType type) {
            mID = id;
            mKey = key;
            mType = type;
        }

        public String getFailState() {
            return failState;
        }

        @Override
        protected Vector<MapUser> doInBackground(Void... params) {
            Vector<MapUser> users = null;

            JSONObject result;
            boolean success;

            try {
                // Get correct result
                switch (mType) {
                    case NEARBY:
                        result = GetNearby(mID, mKey);
                        break;
                    case ALARM:
                        result = GetAlarmNearby(mAlarmID, mID, mKey);
                        break;
                    default:
                        result = GetNearby(mID, mKey);
                        break;
                }

                try {
                    // Check if request was successful
                    success = result.getBoolean(REQ_SUCCESS);
                    if (success) {
                        users = new Vector<>();

                        // Get locations array
                        JSONArray locations = result.getJSONArray(REQ_LOCATION);

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
                                type = MapUser.UserType.ALERTED;
                            }

                            // Add location to list
                            users.add(new MapUser(id, name, car, color, lat, lng, type));
                        }
                    } else {
                        failState = result.getString(REQ_FAIL_STATE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

        @Override
        protected abstract void onCancelled();
    }

    /**
     * Represents an asynchronous task that activates an alarm.
     */
    public static abstract class ActivateAlarmTask extends AsyncTask<Void, Void, Integer> {
        private static final String TAG = "ActivateAlarmTask";

        private Context mContext;

        public ActivateAlarmTask(Context context) {
            if (context == null)
                throw new NullPointerException("context can't be null!");

            mContext = context;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            JSONObject result = DataBase.StartAlarm(GetUserID(mContext), GetUserKey(mContext));

            try {
                if (result.getBoolean(REQ_SUCCESS)) {

                    int alarmID = result.getInt(REQ_ALARM_ID);
                    SetAlarmID(mContext, alarmID);
                    return alarmID;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SetAlarmID(mContext, 0);
            Log.e(TAG, "Could not start alarm!");

            return 0;
        }

        @Override
        protected abstract void onPostExecute(Integer result);
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
        protected abstract void onPostExecute(Boolean result);/* {
            if(!result) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_billing),
                        Toast.LENGTH_LONG).show();
            } else {
                mContext.startService(new Intent(mContext, StartAlarmService.class));
            }
        }*/
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

                try {
                    result.getBoolean(REQ_SUCCESS);
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
        private static final String TAG = "CheckPasswordTask";

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
            return !hasActiveInternetConnection(mContext);
        }

        @Override
        protected abstract void onPostExecute(final Boolean result);

        @Override
        protected void onCancelled() {
        }
    }
}
