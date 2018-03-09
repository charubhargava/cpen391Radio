package com.example.charubhargava.tutorial1;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.wearable.DataMap.TAG;

public class StreamStatus {
    private Boolean isPlaying;
    private Station currentStation = new Station();
    private String currentStreamUrl;
    private Song currentSong = new Song();

    public StreamStatus(){
        this.isPlaying = false;
        this.currentStreamUrl = "";
        this.currentStation = new Station();
        this.currentSong = new Song();
    }

    public StreamStatus(JSONObject jsonObj) throws JSONException {
        Log.d(TAG, "jsonobj StreamStatus: " + jsonObj.toString());
        this.isPlaying = jsonObj.getBoolean("isPlaying");
        this.currentStation = new Station(jsonObj.getJSONObject("currentStation"));
        this.currentStreamUrl = jsonObj.getString("currentStreamUrl");
        this.currentSong = new Song(jsonObj.getJSONObject("currentSong"));
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public String getCurrentStreamUrl() {
        return currentStreamUrl;
    }

    public Song getCurrentSong() {
        return currentSong;
    }
}

