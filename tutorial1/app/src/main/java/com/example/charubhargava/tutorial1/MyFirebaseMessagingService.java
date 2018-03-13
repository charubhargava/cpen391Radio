package com.example.charubhargava.tutorial1;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Charu Bhargava on 08-Mar-18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(getApplicationContext());

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();
        JSONObject dataJson = new JSONObject(data);
        Log.e(TAG, "MSG: " + dataJson);
        try {
            if(dataJson.getString("currentStation") != null) {
                StreamStatus.getInstance(getApplicationContext()).updateStreamStatus();
                Log.e(TAG, "song: " + sharedPref.getCurrSong());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.getMessage());
        }

    }

}
