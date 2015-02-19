package com.cabalry.db;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 11/12/14.
 */
public class DB {

    /***
     * Function that logs user in to Cabalry
     * @param username of user
     * @param password of user
     * @return JSON object that contains:
     *          success : returns true if successfully logged in
     *          id : returns integer id for user
     *          key : returns authorization key for user
     */
    public static JSONObject login(final String username, final String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.LOGIN_URL, "POST", params);

        return json;
    }

    /***
     * Function that logs user out of Cabalry
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully logged out
     */
    public static JSONObject logout(final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.LOGOUT_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns billing state of user
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if billing is ok, false otherwise
     */
    public static JSONObject checkBilling(final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.CHECKBILLING_URL, "POST", params);

        return json;
    }

    /***
     * Function checks password for user
     * @param id of user
     * @param key of user
     * @param password of user
     * @return JSON object that contains:
     *          success : returns true if password is correct
     */
    public static JSONObject checkPass(final int id, final String key, final String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("password", password));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.CHECKPASS_URL, "POST", params);

        return json;
    }

    /***
     * Function that updates user's location
     * @param latitude of user
     * @param longitude of user
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully updated location
     */
    public static JSONObject updateLocation(final double latitude, final double longitude, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
        params.add(new BasicNameValuePair("longitude", Double.toString(longitude)));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.LOCATION_URL, "POST", params);

        return json;
    }

    /***
     * Function that gets user's location
     * @param userId id of user we want to get location of
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if found location
     *          lat : latitude of user
     *          lon : longitude of user
     */
    public static JSONObject getLocation(final int userId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.GETLOCATION_URL, "POST", params);

        return json;
    }

    /***
     * Function that updates user;s GCM key
     * @param gcm key generated by user
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully updated gcm key
     */
    public static JSONObject updateGCM(final String gcm, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("gcm", gcm));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.GCM_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns nearby Cabalry user's locations
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if users were located
     *          location : Array that contains id, latitude and longitude of each user.
     */
    public static JSONObject nearby(final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.NEARBY_URL, "POST", params);

        return json;
    }

    /***
     * Function that return information for specified user id.
     * @param userId of user we are getting information from
     * @param id of user
     * @param key of user
     * @return JSON Object that contains:
     *          success : returns true if information is found
     *          name : name of user
     *			make : make of car
     *			color : color of car
     */
    public static JSONObject userInfo(final int userId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.USERINFO_URL, "POST", params);

        return json;
    }

    /***
     * Function that creates an alarm
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if alarm was successfully created
     *          alarmId : id of alarm created
     */
    public static JSONObject alarm(final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.ALARM_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns locations of users that where contacted by alarm
     * @param alarmId of alarm we want information from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information is found
     *          state : state of alarm
     *			sent : JSON array of locations of user's that were alerted
     */
    public static JSONObject getAlarmList(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.ALARMLIST_URL, "POST", params);

        return json;
    }

    /***
     * Function that removes user from alarm
     * @param alarmId of alarm we want to be removed from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if removed successfully
     */
    public static JSONObject ignoreAlarm(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.ALARMIGNORE_URL, "POST", params);

        return json;
    }

    /***
     * Function that adds user to alarm
     * @param alarmId of alarm we want to be removed from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if removed? successfully
     */
    public static JSONObject addToAlarm(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.ADDTOALARM_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns user's settings
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information is found
     *          timer : time set by user
     *          fake : fake password of user
     *          silent : returns true if enabled
     *          quantity : quantity we should contact in case of alarm
     *			range : range to be used for nearby function
     */
    public static JSONObject getSettings(final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.GETSETTINGS_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns alarm info
     * @param alarmId of alarm we want information from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information is found
     *          start : time alarm was raised
     *          ip : of server streaming audio
     *          state : of alarm
     *          id : of user who raised alarm
     *			sent : JSON array of locations of user's that were alerted
     */
    public static JSONObject getAlarmInfo(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.ALARMINFO_URL, "POST", params);

        return json;
    }

    /***
     * Function that allows you to update listener information
     * @param alarmId of alarm in question
     * @param id of user
     * @param key of user
     * @param port opened by user
     * @return JSON object that contains:
     *          success : returns true if update is successful
     */
    public static JSONObject updateListenerInfo(final int alarmId, final int id, final String key, final int port) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("port", Integer.toString(port)));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.UPDATELISTENER_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns alarm info
     * @param alarmId of alarm we want to stop
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if alarm was succesfully stopped
     */
    public static JSONObject stopAlarm(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GlobalKeys.STOPALARM_URL, "POST", params);

        return json;
    }
}