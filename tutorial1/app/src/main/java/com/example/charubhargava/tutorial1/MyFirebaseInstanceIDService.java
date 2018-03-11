package com.example.charubhargava.tutorial1;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Charu Bhargava on 08-Mar-18.
 * Reference: https://firebase.google.com/docs/cloud-messaging/android/client
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String NEW_DEVICE_URL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/users";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //Send registration token to server
        sendRegistrationToServer(refreshedToken);

        //Save registration token for later use
        SharedPrefManager.getInstance(this).registerDevice(refreshedToken);
    }

    /*
    Sends registration token to the server
     */
    private void sendRegistrationToServer(String token){

        JSONObject registerDeviceJSON = new JSONObject();

        try {
            registerDeviceJSON.put(DEVICE_TOKEN_KEY, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest

                (Request.Method.POST, NEW_DEVICE_URL, registerDeviceJSON, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        User myUser = new User(response, getApplicationContext());
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(myUser);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add to the RequestQueue .
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }
}
