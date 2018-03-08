package com.example.charubhargava.tutorial1;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by Charu Bhargava on 06-Mar-18.
 */

public class User {
    private static final String ID_KEY = "Id";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String IS_PLAYING_KEY = "IsPLaying";
    private static final String CURRENT_PLAYING_KEY = "CurrentPlaying";
    private static final String RECORDINGS_KEY = "Recordings";
    private static final String CREATED_AT_KEY = "CreatedAt";

    private String id;
    private String deviceToken;
    private boolean isPlaying;
    private String currentPlaying;
    private String recordings[];
    private long createdAt;

    public User(JSONObject jsonObject, Context context){
        //create new user object
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getUserId() {
        return id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String[] getRecordings() {
        return recordings;
    }

    public String getCurrentPlaying() {
        return currentPlaying;
    }
}
