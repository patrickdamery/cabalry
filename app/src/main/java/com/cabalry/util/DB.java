package com.cabalry.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 11/12/14.
 * Modified by conor on 13/10/15.
 */
public class DB {

    public static enum UserRequestType { NEARBY, ALARM }

    /**
    * Request response string keys
    */
    public static final String REQ_SUCCESS = "success";
    public static final String REQ_USER_ID = "id";
    public static final String REQ_USER_KEY = "key";
    public static final String REQ_USER_NAME = "name";
    public static final String REQ_USER_CAR = "make";
    public static final String REQ_USER_COLOR = "color";
    public static final String REQ_LATITUDE = "lat";
    public static final String REQ_LONGITUDE = "lon";
    public static final String REQ_LOGIN = "login";
    public static final String REQ_USER_IP = "ip";
    public static final String REQ_ALARM_ID = "alarmId";
    public static final String REQ_LOCATION = "location";
    public static final String REQ_SENT = "sent";
    public static final String REQ_START = "start";
    public static final String REQ_PORT = "port";

    public static final String REQ_FAIL_STATE = "failstate";
    public static final String KEY_FAIL = "keyfail";
    public static final String NEAR_FAIL = "nearfail";
    public static final String CARD_FAIL = "cardfail";
    public static final String PAY_FAIL = "payfail";
    public static final String UNKNOWN_FAIL = "unknownfail";

    public static final String REQ_ALARM_STATE = "state";
    public static final String ACTIVE_ALARM = "active";
    public static final String INACTIVE_ALARM = "inactive";
    public static final String FINISHED_ALARM = "finished";
    public static final String LOST_ALARM = "lost";

    // ?
    public static final String SENDER_ID = "200578369108";
    public static final String PROPERTY_REG_ID = "regId";
    public static final String PROPERTY_APP_VERSION = "0.7.5";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Cabalry url's
     */
    public static final String CABALRY_URL = "cabalry.com";
    public static final String GET_SETTINGS_URL = "https://"+ CABALRY_URL +"/cabalry/getSettings.php";
    public static final String LOGIN_URL = "https://"+ CABALRY_URL +"/cabalry/login.php";
    public static final String CHECK_BILLING_URL = "https://"+ CABALRY_URL +"/cabalry/checkBilling.php";
    public static final String CHECK_PASS_URL = "https://"+ CABALRY_URL +"/cabalry/checkPassword.php";
    public static final String START_ALARM_URL = "https://"+ CABALRY_URL +"/cabalry/startAlarm.php";
    public static final String STOP_ALARM_URL = "https://"+ CABALRY_URL +"/cabalry/stopAlarm.php";
    public static final String IGNORE_ALARM_URL = "https://"+ CABALRY_URL +"/cabalry/ignore.php";
    public static final String ADD_TO_ALARM_URL = "https://"+ CABALRY_URL +"/cabalry/addToAlarm.php";
    public static final String ALARM_LIST_URL = "https://"+ CABALRY_URL +"/cabalry/alarmList.php";
    public static final String ALARM_INFO_URL = "https://"+ CABALRY_URL +"/cabalry/alarmInfo.php";
    public static final String UPDATE_LISTENER_URL = "https://"+ CABALRY_URL +"/cabalry/updateListener.php";
    public static final String LOCATION_URL = "https://"+ CABALRY_URL +"/cabalry/updateLocation.php";
    public static final String GET_LOCATION_URL = "https://"+ CABALRY_URL +"/cabalry/getLocation.php";
    public static final String GCM_URL = "https://"+ CABALRY_URL +"/cabalry/updateGCM.php";
    public static final String NEARBY_URL = "https://"+ CABALRY_URL +"/cabalry/nearby.php";
    public static final String USERINFO_URL = "https://"+ CABALRY_URL +"/cabalry/userInfo.php";
    public static final String LOGOUT_URL = "https://"+ CABALRY_URL +"/cabalry/logout.php";

    public static final String REGISTER_URL = "https://"+ CABALRY_URL +"/register.php";
    public static final String FORGOT_URL = "https://"+ CABALRY_URL +"/forgot.php";
    public static final String BILLING_URL = "https://"+ CABALRY_URL +"/billing.php";
    public static final String PROFILE_URL = "https://"+ CABALRY_URL +"/profile.php";
    public static final String RECORDINGS_URL = "https://"+ CABALRY_URL +"/recordings.php";
    public static final String SETTINGS_URL = "https://"+ CABALRY_URL +"/settings.php";
    public static final String HELP_URL = "https://"+ CABALRY_URL +"#support";
    public static final String VIEWUSER_URL = "https://"+ CABALRY_URL +"/viewUser.php";

    /**
     * Requests a login to the server and returns the state result
     *
     * @param username of user
     * @param password of user
     * @return JSON object that contains:
     *          success : returns true if successfully logged in
     *          id : returns integer id for user
     *          key : returns authorization key for user
     */
    public static JSONObject RequestLogin(final String username, final String password) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        return sendRequest(new RequestObject(params, LOGIN_URL));
    }

    /**
     * Requests a logout to the server and returns the state result
     *
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully logged out
     */
    public static JSONObject RequestLogout(final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, LOGOUT_URL));
    }

    /**
     * Function that returns billing state of user
     *
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if billing inputStream ok, false otherwise
     */
    public static JSONObject CheckBilling(final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, CHECK_BILLING_URL));
    }

    /**
     * Function checks password for user
     *
     * @param id of user
     * @param key of user
     * @param password of user
     * @return JSON object that contains:
     *          success : returns true if password inputStream correct
     */
    public static JSONObject CheckPassword(final int id, final String key, final String password) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("password", password));

        return sendRequest(new RequestObject(params, CHECK_PASS_URL));
    }

    /**
     * Function that updates user's location
     *
     * @param latitude of user
     * @param longitude of user
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully updated location
     */
    public static JSONObject UpdateUserLocation(final double latitude, final double longitude, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
        params.add(new BasicNameValuePair("longitude", Double.toString(longitude)));

        return sendRequest(new RequestObject(params, LOCATION_URL));
    }

    /**
     * Function that gets user's location
     *
     * @param userId id of user we want to get location of
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if found location
     *          lat : latitude of user
     *          lon : longitude of user
     */
    public static JSONObject GetUserLocation(final int userId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));

        return sendRequest(new RequestObject(params, GET_LOCATION_URL));
    }

    /**
     * Function that updates user's GCM key
     *
     * @param gcm key generated by user
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if successfully updated gcm key
     */
    public static JSONObject UpdateUserGCM(final String gcm, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("gcm", gcm));

        return sendRequest(new RequestObject(params, GCM_URL));
    }

    /**
     * Function that returns nearby Cabalry user's locations
     *
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if users were located
     *          location : Array that contains id, name, car, latitude and longitude of each user.
     */
    public static JSONObject GetNearby(final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, NEARBY_URL));
    }

    /**
     * Function that return information for specified user id
     *
     * @param userId of user we are getting information from
     * @param id of user
     * @param key of user
     * @return JSON Object that contains:
     *          success : returns true if information inputStream found
     *          name : name of user
     *			make : make of car
     *			color : color of car
     */
    public static JSONObject GetUserInfo(final int userId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("userId", Integer.toString(userId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, USERINFO_URL));
    }

    /**
     * Function that starts an alarm
     *
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if alarm was successfully created
     *          alarmId : id of alarm created
     */
    public static JSONObject StartAlarm(final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, START_ALARM_URL));
    }

    /**
     * Function that returns locations of users that where contacted by alarm
     *
     * @param alarmId of alarm we want information from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information inputStream found
     *          state : state of alarm
     *			sent : JSON array of locations of user's that were alerted
     */
    public static JSONObject GetAlarmNearby(final int alarmId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, ALARM_LIST_URL));
    }

    /**
     * Function that removes user from alarm
     *
     * @param alarmId of alarm we want to be removed from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if removed successfully
     */
    public static JSONObject IgnoreAlarm(final int alarmId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, IGNORE_ALARM_URL));
    }

    /***
     * Function that adds user to alarm
     * @param alarmId of alarm we want to be removed from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if removed? successfully
     */
    public static JSONObject AddToAlarm(final int alarmId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, ADD_TO_ALARM_URL));
    }

    /***
     * Function that returns user's settings
     *
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information inputStream found
     *          timer : time set by user
     *          fake : fake password of user
     *          silent : returns true if enabled
     *          quantity : quantity we should contact in case of alarm
     *			range : range to be used for nearby function
     */
    public static JSONObject GetUserSettings(final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, GET_SETTINGS_URL));
    }

    /***
     * Function that returns alarm info
     *
     * @param alarmId of alarm we want information from
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if information inputStream found
     *          start : time alarm was raised
     *          ip : of server streaming audio
     *          state : of alarm
     *          id : of user who raised alarm
     *			sent : JSON array of locations of user's that were alerted
     */
    public static JSONObject GetAlarmInfo(final int alarmId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, ALARM_INFO_URL));
    }

    /***
     * Function that allows you to update listener information
     *
     * @param alarmId of alarm in question
     * @param id of user
     * @param key of user
     * @param port opened by user
     * @return JSON object that contains:
     *          success : returns true if update inputStream successful
     */
    public static JSONObject UpdateListenerInfo(final int alarmId, final int id, final String key, final int port) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));
        params.add(new BasicNameValuePair("port", Integer.toString(port)));

        return sendRequest(new RequestObject(params, UPDATE_LISTENER_URL));
    }

    /***
     * Function that stops an active alarm
     *
     * @param alarmId of alarm we want to stop
     * @param id of user
     * @param key of user
     * @return JSON object that contains:
     *          success : returns true if alarm was succesfully stopped
     */
    public static JSONObject StopAlarm(final int alarmId, final int id, final String key) {

        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("alarmId", Integer.toString(alarmId)));
        params.add(new BasicNameValuePair("id", Integer.toString(id)));
        params.add(new BasicNameValuePair("key", key));

        return sendRequest(new RequestObject(params, STOP_ALARM_URL));
    }

    private static JSONObject sendRequest(final RequestObject request) {

        // return JSON object
        return new JSONParser().makeHttpRequest(request.url, request.method, request.params);
    }

    /**
     * Helper class for handling requests
     */
    private static class RequestObject {

        private final List<NameValuePair> params;
        private final String url;
        private final String method;

        public RequestObject(List<NameValuePair> params, final String url) {
            this.params = params; this.url = url;
            method = "POST";
        }

        @SuppressWarnings("unused")
        public RequestObject(List<NameValuePair> params, final String url, final String method) {
            this.params = params; this.url = url; this.method = method;
        }
    }
}