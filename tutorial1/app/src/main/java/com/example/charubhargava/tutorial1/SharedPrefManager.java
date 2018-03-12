package com.example.charubhargava.tutorial1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


/**
 * Reference: https://www.simplifiedcoding.net/android-volley-tutorial/#Android-Volley-Tutorial
 */


public class SharedPrefManager {

    public static final String createUserURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/users";
    public static final String stationsURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stations";
    public static final String streamURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stream";

    private static final String SHARED_PREF_NAME = "radioUserSharedPref";

    private static final String ID_KEY = "Id";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String IS_PLAYING_KEY = "IsPLaying";
    private static final String CURRENT_PLAYING_KEY = "CurrentPlaying";
    private static final String RECORDINGS_KEY = "Recordings";
    private static final String CREATED_AT_KEY = "CreatedAt";
    private static final String CURR_STREAM_URL_KEY = "currentStreamUrl";
    private static final String CURR_SONG_KEY = "currentSong";
    private static final String CURR_STATION_KEY = "currentStation";
    private static final String STATION_ID_KEY = "id";
    private static final String STATION_NAME_KEY = "name";
    private static final String COUNTRY_KEY = "country";
    private static final String COUNTRY_NAME_KEY = "name";
    private static final String GENRE_KEY = "genre";
    private static final String ARTIST_KEY = "artist";
    private static final String TITLE_KEY = "title";




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

    //USER METHODS

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Toast.makeText(mCtx,"User Id in shared pref from user object " + user.getUserId(), Toast.LENGTH_LONG).show();
        editor.putString(ID_KEY, user.getUserId());
        editor.putString(DEVICE_TOKEN_KEY, user.getDeviceToken());
        editor.putBoolean(IS_PLAYING_KEY, user.isPlaying());
        editor.putString(CURRENT_PLAYING_KEY, user.getCurrentPlaying());
        //editor.putStringSet(RECORDINGS_KEY, user.getRecordings()); TODO store array??
        editor.putLong(CREATED_AT_KEY, user.getCreatedAt());

        editor.apply();

    }

    //Get user id
    public String getUserId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ID_KEY, null);
    }

    public void updateCurrStreamStatus(StreamStatus stream){


        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Station stn = stream.getCurrentStation();
        editor.putString(CURR_STATION_KEY, stn.getId());
        editor.putString(STATION_NAME_KEY, stn.getName());
        editor.putString(COUNTRY_KEY, stn.getCountryName());
        editor.putString(GENRE_KEY, stn.getGenre());
        editor.putString(CURR_STREAM_URL_KEY, stn.getStreamUrl());
        Song song = stream.getCurrentSong();
        editor.putString(CURR_SONG_KEY, song.getTitle());
        editor.putString(ARTIST_KEY,song.getArtist());
        boolean isPlaying = stream.getPlaying();
        editor.putBoolean(IS_PLAYING_KEY, isPlaying);
        editor.apply();


    }

    //Get current station
    public String getCurrStn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURR_STATION_KEY, null);
    }

    //Get current song
    public String getCurrSong(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURR_SONG_KEY, null);
    }

    //Get current artist
    public String getCurrArtist(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ARTIST_KEY, null);
    }



}