package com.cabalry.db;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 11/12/14.
 */
public class DB {
    private final String IP = "alonica.net";
    private final String REGISTER_URL = "http://"+IP+"/cabalry/register.php";
    private final String LOGIN_URL = "http://"+IP+"/cabalry/login.php";
    private final String ALARM_URL = "http://"+IP+"/cabalry/alarm.php";
    private final String LOCATION_URL = "http://"+IP+"/cabalry/location.php";
    private final String GCM_URL = "http://"+IP+"/cabalry/gcm.php";
    private final String CABALRY_URL = "http://"+IP+"/cabalry/cabalry.php";

    // JSON Node names
    private final String TAG_SUCCESS = "success";
    private final String TAG_KEY = "key";
    private final String TAG_ID = "id";

    public void register(final String username, final String password, final String email, final String name, final String number) {
        //TODO: Add means to show progress
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("email", email));
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("number", number));

                // getting JSON Object
                // Note that create product url accepts POST method
                JSONObject json;
                json = new JSONParser().makeHttpRequest(REGISTER_URL, "POST", params);

                // check log cat from response
                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully created user
                        System.out.println("Succesful!");
                        //return true;
                    } else {
                        System.out.println("Fail!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }.execute();

    }


    public JSONObject login(final String username, final String password) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                // getting JSON Object
                JSONObject json;
                json = new JSONParser().makeHttpRequest(LOGIN_URL, "POST", params);

                return json;
    }

    public JSONObject updateLocation(final double latitude, final double longitude, final int id, final String key) {
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

    public JSONObject updateGCM(final String gcm, final int id, final String key) {
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



}