package com.cabalry;

import android.content.Intent;
import android.os.AsyncTask;

import com.cabalry.db.DB;
import com.cabalry.map.CabalryUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Utility class for server request async-tasks,
 * debug methods, etc
 *
 * Created by conor on 14/10/15.
 */
public class CabalryUtility {
    public enum UserRequestType { NEARBY, ALARM }

    /**
     * [ Debug Only ]
     * Simple method that prints the elements of a list of users
     */
    @SuppressWarnings("unused")
    public static void PrintCabalryUserList(final Vector<CabalryUser> userList) {
        for(CabalryUser user : userList) {
            System.out.println("[ id = "+user.getID()+", lat = "+user.getLat()+", lng = "+user.getLng()+" ]");
        }
    }

    /**
     * Represents an asynchronous task that collects locations
     * of nearby cabalry members
     */
    public static class CollectUsersTask extends AsyncTask<Void, Void, Vector<CabalryUser>> {

        private final int mID;
        private final String mKey;
        private final UserRequestType mType;
        private final int mAlarmID;

        private String failState;

        public CollectUsersTask(int id, String key, UserRequestType type, int alarmID) {
            mID = id; mKey = key; mType = type; mAlarmID = alarmID;
        }

        public String getFailState() { return failState; }

        @Override
        protected Vector<CabalryUser> doInBackground(Void... params) {
            Vector<CabalryUser> users = null;

            JSONObject result;
            boolean success;

            try {
                // Get correct result
                switch(mType) {
                    case NEARBY : result = DB.getNearby(mID, mKey); break;
                    case ALARM  : result = DB.getAlarmNearby(mAlarmID, mID, mKey); break;
                    default     : result = DB.getNearby(mID, mKey); break;
                }

                try {
                    // Check if request was successful
                    success = result.getBoolean(DB.SUCCESS);
                    if(success) {
                        users = new Vector<>();

                        // Get locations array
                        JSONArray locations = result.getJSONArray(DB.LOCATION);

                        for(int i = 0; i < locations.length(); i++) {
                            JSONObject location = locations.getJSONObject(i);

                            int id = location.getInt(DB.USER_ID);
                            double lat = location.getDouble(DB.LATITUDE);
                            double lng = location.getDouble(DB.LONGITUDE);

                            // Add location to list
                            users.add(new CabalryUser(id, lat, lng, null));
                        }
                    } else {
                        failState = result.getString(DB.FAIL_STATE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return users;
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

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
                result = DB.requestLogin(mUser, mPassword);
                try {
                    success = result.getBoolean(DB.SUCCESS);

                    if (success) {

                        // Register user.
                        mID = result.getInt(DB.USER_ID);
                        mKey = result.getString(DB.USER_KEY);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return success;
        }
    }
}
