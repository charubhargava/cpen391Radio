package com.example.charubhargava.tutorial1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Station {
    private static final String TAG = "Station class";
    private String id;
    private String name;
    private JSONObject country;
    private String countryCode;
    private String countryName;
    private double countryLat;
    private double countryLong;

    private String streamUrl;
    private String genre;
    private long createdAt;
    private long updatedAt;

    public Station(){

    }

    public Station(JSONObject jsonObj) {
        try {
            this.id = jsonObj.getString("id");
            this.name = jsonObj.getString("name");
            this.country = jsonObj.getJSONObject("country");
            this.countryCode = country.getString("code");
            this.countryName = country.getString("name");
            this.countryLat = country.getDouble("latitude");
            this.countryLong = country.getDouble("longitude");
            this.streamUrl = jsonObj.getString("streamUrl");
            this.genre = jsonObj.getString("genre");
            this.createdAt = jsonObj.getLong("createdAt");
            this.updatedAt = jsonObj.getLong("updatedAt");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JSONObject getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public double getCountryLat() {
        return countryLat;
    }

    public double getCountryLong() {
        return countryLong;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getGenre() {
        return genre;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
