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

    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String LOGIN = "login";
    public static final String SUCCESS = "success";

    public static final String IP = "cabalry.com";
    public static final String LOGIN_URL = "http://"+IP+"/cabalry/login.php";
    public static final String ALARM_URL = "http://"+IP+"/cabalry/alarm.php";
    public static final String ALARMINFO_URL = "http://"+IP+"/cabalry/alarmInfo.php";
    public static final String LOCATION_URL = "http://"+IP+"/cabalry/location.php";
    public static final String GETLOCATION_URL = "http://"+IP+"/cabalry/getLocation.php";
    public static final String GCM_URL = "http://"+IP+"/cabalry/gcm.php";
    public static final String NEARBY_URL = "http://"+IP+"/cabalry/nearby.php";
    public static final String USERINFO_URL = "http://"+IP+"/cabalry/userInfo.php";
    public static final String CARINFO_URL = "http://"+IP+"/cabalry/carInfo.php";
    public static final String REGISTER_URL = "http://"+IP+"/register.php";
    public static final String STOPALARM_URL = "http://"+IP+"/cabalry/stopAlarm.php";

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
        json = new JSONParser().makeHttpRequest(LOGIN_URL, "POST", params);

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
        json = new JSONParser().makeHttpRequest(LOCATION_URL, "POST", params);

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
        params.add(new BasicNameValuePair("latitude", Integer.toString(userId)));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(GETLOCATION_URL, "POST", params);

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
        json = new JSONParser().makeHttpRequest(GCM_URL, "POST", params);

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
        json = new JSONParser().makeHttpRequest(NEARBY_URL, "POST", params);

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
     *          number : phone number of user
     *          profile : profile picture url of user
     */
    public static JSONObject userInfo(final int userId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(USERINFO_URL, "POST", params);

        return json;
    }

    /***
     * Function that returns car information for specified user id
     * @param userId of user we are getting information from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information is found
     *          make : make of car
     *          color : color of car
     *          plate : plate of car
     */
    public static JSONObject carInfo(final int userId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(CARINFO_URL, "POST", params);

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
        json = new JSONParser().makeHttpRequest(ALARM_URL, "POST", params);

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
     *          port : of server streaming audio
     *          id : of user who raised alarm
     */
    public static JSONObject getAlarmInfo(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(ALARMINFO_URL, "POST", params);

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
    public JSONObject stopAlarm(final int alarmId, final int id, final String key) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        // getting JSON Object
        JSONObject json;
        json = new JSONParser().makeHttpRequest(STOPALARM_URL, "POST", params);

        return json;
    }
}