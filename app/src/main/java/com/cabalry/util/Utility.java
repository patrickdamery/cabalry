package com.cabalry.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;

import com.cabalry.map.MapUser;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import static com.cabalry.util.DB.*;

/**
 * Utility class for server request async-tasks,
 * debug methods, etc
 *
 * Created by conor on 14/10/15.
 */
public class Utility {

    public enum UserRequestType { NEARBY, ALARM }

    public static final int TWO_MINUTES = 12000;

    public static final String PACKAGE_NAME = "CABALRY";
    public static final String PREF_USER_ID = "ID";
    public static final String PREF_USER_KEY = "KEY";
    public static final String PREF_USER_LOGIN = "LOGIN";
    public static final String PREF_LATITUDE = "LAT";
    public static final String PREF_LONGITUDE = "LNG";

    public static final String PREF_FAKE_PASS = "FAKE";
    public static final String PREF_TIMER = "TIMER";
    public static final String PREF_TIMER_ENABLED = "TIMER_ENABLED";
    public static final String PREF_SILENT = "SILENT";
    public static final String PREF_ALERT_COUNT = "ALERT_COUNT";
    public static final String PREF_ALARM_RANGE = "ALERT_RANGE";

    public static int GetUserID(Context context) {
        return getSharedPrefs(context).getInt(PREF_USER_ID, 0);
    }

    public static String GetUserKey(Context context) {
        return getSharedPrefs(context).getString(PREF_USER_KEY, "");
    }

    public static boolean IsUserLogin(Context context) {
        return getSharedPrefs(context).getBoolean(PREF_USER_LOGIN, false);
    }

    public static void LoginUser(Context context, int id, String key) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, id);
        editor.putString(PREF_USER_KEY, key);
        editor.putBoolean(PREF_USER_LOGIN, true);
        editor.commit();
    }

    public static void LogoutUser(Context context) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putInt(PREF_USER_ID, 0);
        editor.putString(PREF_USER_KEY, "");
        editor.putBoolean(PREF_USER_LOGIN, false);
        editor.commit();
    }

    public static void StoreLocation(Context context, LatLng location) {
        SharedPreferences.Editor editor = getSharedPrefs(context).edit();
        editor.putFloat(PREF_LATITUDE, (float)location.latitude);
        editor.putFloat(PREF_LONGITUDE, (float)location.longitude);
        editor.commit();
    }

    public static LatLng GetLocation(Context context) {
        SharedPreferences prefs = getSharedPrefs(context);
        return new LatLng(prefs.getFloat(PREF_LATITUDE, 0), prefs.getFloat(PREF_LONGITUDE, 0));
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static double GetDistance(LatLng a, LatLng b) {
        if(a == null || b == null) return 0;

        int R = 6371000; // metres
        double lat1 = Math.toRadians(a.latitude);
        double lat2 = Math.toRadians(b.latitude);
        double dLng = Math.toRadians(b.longitude - a.longitude);

        return Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(dLng)) * R;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix.
     * @param location  The new Location that you want to evaluate.
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one.
     */
    public static boolean IsBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location.
            return true;
        }

        // Check whether the new location fix is newer or older.
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate.
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider.
        boolean isFromSameProvider = IsSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy.
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    public static boolean IsSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Simple method that prints the elements of a list of users
     */
    @SuppressWarnings("unused")
    public static void PrintCabalryUserList(String msg, final Vector<MapUser> userList) {
        String str = "{\n";
        for(MapUser user : userList) {
            str += "\t[ id = "+user.getID()+", lat = "+user.getLat()+", lng = "+user.getLng()+" ]\n";
        }
        System.out.println(msg+" : "+str+"}");
    }

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
}
