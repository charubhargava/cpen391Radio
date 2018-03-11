package com.example.charubhargava.tutorial1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



/**
 * Reference: https://www.simplifiedcoding.net/android-volley-tutorial/#Android-Volley-Tutorial
 */


public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "radioUserSharedPref";

    private static final String ID_KEY = "Id";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String IS_PLAYING_KEY = "IsPLaying";
    private static final String CURRENT_PLAYING_KEY = "CurrentPlaying";
    private static final String RECORDINGS_KEY = "Recordings";
    private static final String CREATED_AT_KEY = "CreatedAt";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ID_KEY, user.getUserId());
        editor.putString(DEVICE_TOKEN_KEY, user.getDeviceToken());
        editor.putBoolean(IS_PLAYING_KEY, user.isPlaying());
        editor.putString(CURRENT_PLAYING_KEY, user.getCurrentPlaying());
        //editor.putStringSet(RECORDINGS_KEY, user.getRecordings()); TODO store array??
        editor.putLong(CREATED_AT_KEY, user.getCreatedAt());

        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isDeviceRegistered() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ID_KEY, null) != null;
    }

    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ID_KEY, null);
    }

    //Register a new device
    public void registerDevice(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_TOKEN_KEY, token);
    }

    //Get user id
    public String getUserId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ID_KEY, null);
    }

}