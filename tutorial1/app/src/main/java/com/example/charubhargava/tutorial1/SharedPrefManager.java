package com.example.charubhargava.tutorial1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Reference: https://www.simplifiedcoding.net/android-volley-tutorial/#Android-Volley-Tutorial
 */


public class SharedPrefManager {

    private static final String createUserURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/users";
    private static final String stationsURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stations";
    private static final String streamURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stream";
    private static final String recordingsURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/recordings";
    private static final String recommendURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/recommendations";
    private static final String statsURL = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stats";

    private static final String SHARED_PREF_NAME = "radioUserSharedPref";

    private static final String ID_KEY = "Id";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String IS_PLAYING_KEY = "IsPLaying";
    private static final String CURRENT_PLAYING_KEY = "CurrentPlaying";
    private static final String CREATED_AT_KEY = "CreatedAt";
    private static final String CURR_STREAM_URL_KEY = "currentStreamUrl";
    private static final String CURR_SONG_KEY = "currentSong";
    private static final String STREAM_ID_KEY = "id";
    private static final String STATION_NAME_KEY = "name";
    private static final String COUNTRY_KEY = "country";
    private static final String COUNTRY_NAME_KEY = "name";
    private static final String GENRE_KEY = "genre";
    private static final String ARTIST_KEY = "name";

    private static final String NEWREC_START_DAY_KEY = "startDay";
    private static final String NEWREC_END_DAY_KEY = "endDay";
    private static final String NEWREC_START_MONTH_KEY = "startMonth";
    private static final String NEWREC_END_MONTH_KEY = "endMonth";
    private static final String NEWREC_START_YEAR_KEY = "startYear";
    private static final String NEWREC_END_YEAR_KEY = "endYear";
    private static final String NEWREC_START_HOUR_KEY = "startHour";
    private static final String NEWREC_END_HOUR_KEY = "endHour";
    private static final String NEWREC_START_MINUTE_KEY = "startMinute";
    private static final String NEWREC_END_MINUTE_KEY = "endMinute";

    private static final String NEWREC_START_DATE_KEY = "startDate";
    private static final String NEWREC_END_DATE_KEY = "endDate";


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
        editor.putLong(CREATED_AT_KEY, user.getCreatedAt());

        editor.apply();

    }
    public void updateCurrStreamStatus(StreamStatus stream){


        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Station stn = stream.getCurrentStation();
        editor.putString(STREAM_ID_KEY, stn.getId());
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

    public void updateNewRecStartTime(int hour, int minute){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NEWREC_START_HOUR_KEY, hour);
        editor.putInt(NEWREC_START_MINUTE_KEY, minute);
        editor.apply();

    }

    public void updateNewRecEndTime(int hour, int minute){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NEWREC_END_HOUR_KEY, hour);
        editor.putInt(NEWREC_END_MINUTE_KEY, minute);
        editor.apply();

    }

    public void updateNewRecStartDate(int day, int month, int year){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NEWREC_START_DAY_KEY, day);
        editor.putInt(NEWREC_START_MONTH_KEY,  month);
        editor.putInt(NEWREC_START_YEAR_KEY,  year);
        editor.apply();

    }

    public void updateNewRecEndDate(int day, int month, int year){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NEWREC_END_DAY_KEY, day);
        editor.putInt(NEWREC_END_MONTH_KEY,  month);
        editor.putInt(NEWREC_END_YEAR_KEY,  year);
        editor.apply();

    }

    //Get user id
    public String getUserId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ID_KEY, null);
    }

    //Get current station
    public String getCurrStn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(STATION_NAME_KEY, null);
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

    //Get current stn id
    public String getCurrStreamID(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(STREAM_ID_KEY, null);
    }
    //Is song playing
    public boolean getIsPlaying(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_PLAYING_KEY, false);
    }

    public String getStationsURL() {
        return stationsURL;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public String getCreateUserURL() {
        return createUserURL;
    }

    public String getRecordingsURL() {
        return recordingsURL;
    }
    public String getStatsURL() {
        return statsURL;
    }

    public String getRecommendURL() {

        return recommendURL;
    }

}