package com.cabalry.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.cabalry.map.MapUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.cabalry.util.DB.*;

/**
 * Created by conor on 24/11/15.
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
            mID = id; mKey = key;
            mType = UserRequestType.NEARBY;
        }

        public void setCollectInfo(int id, String key, int alarmID) {
            mID = id; mKey = key; mAlarmID = alarmID;
            mType = UserRequestType.ALARM;
        }

        @SuppressWarnings("unused")
        public void setCollectInfo(int id, String key, UserRequestType type) {
            mID = id; mKey = key; mType = type;
        }

        public String getFailState() { return failState; }

        @Override
        protected Vector<MapUser> doInBackground(Void... params) {
            Vector<MapUser> users = null;

            JSONObject result;
            boolean success;

            try {
                // Get correct result
                switch(mType) {
                    case NEARBY : result = GetNearby(mID, mKey); break;
                    case ALARM  : result = GetAlarmNearby(mAlarmID, mID, mKey); break;
                    default     : result = GetNearby(mID, mKey); break;
                }

                try {
                    // Check if request was successful
                    success = result.getBoolean(REQ_SUCCESS);
                    if(success) {
                        users = new Vector<>();

                        // Get locations array
                        JSONArray locations = result.getJSONArray(REQ_LOCATION);

                        for(int i = 0; i < locations.length(); i++) {
                            JSONObject location = locations.getJSONObject(i);

                            int id = location.getInt(REQ_USER_ID);
                            String name = location.getString(REQ_USER_NAME);
                            String car = location.getString(REQ_USER_CAR);
                            String color = location.getString(REQ_USER_COLOR);
                            double lat = location.getDouble(REQ_LATITUDE);
                            double lng = location.getDouble(REQ_LONGITUDE);

                            // Add location to list
                            users.add(new MapUser(id, name, car, color, lat, lng, null));
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
            mUserID = userID; mID = id; mKey = key;
        }

        public String getFailState() { return failState; }

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
                    if(success) {
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

        public int getID() { return mID; }
        public String getKey() { return mKey; }

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
     * Represents an asynchronous
     */
    public static class UpdateListenerInfoTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UpdateListenerInfoTask";

        private int mID;
        private String mKey;
        private int mAlarmID;
        private int mPort;

        public void setListenerInfo(int id, String key, int alarmID, int port) {
            mID = id; mKey = key;
            mAlarmID = alarmID;
            mPort = port;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject result;
            boolean success = false;

            try {
                result = UpdateListenerInfo(mAlarmID, mID, mKey, mPort);
                try {
                    success = result.getBoolean(REQ_SUCCESS);

                    if(!success) {
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
        protected void onPostExecute(final Void result) {}

        @Override
        protected void onCancelled() {}
    }

    /**
     * Represents an asynchronous
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
        protected void onCancelled() {}
    }
}
