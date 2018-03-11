package com.example.charubhargava.tutorial1;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

/**
 * Created by Charu Bhargava on 08-Mar-18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public void onMessageReceived(RemoteMessage remoteMessage) {
        User user;

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String userid = remoteMessage.getData().get("username");

        changeSong("got something");
    }

    private void changeSong(String message){

    }
}
