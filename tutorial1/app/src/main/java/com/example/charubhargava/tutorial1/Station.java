package com.example.charubhargava.tutorial1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Station {
    private String id;
    private String name;
    private int dirbleId;
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

    public Station(JSONObject jsonObj) throws JSONException {
        this.id = jsonObj.getString("id");
        this.name = jsonObj.getString("name");
        this.dirbleId = jsonObj.getInt("dirbleId");
        this.country = jsonObj.getJSONObject("country");
        this.countryCode = country.getString("code");
        this.countryName = country.getString("name");
        this.countryLat = country.getDouble("latitude");
        this.countryLong = country.getDouble("longitude");
        this.streamUrl = jsonObj.getString("streamUrl");
        this.genre = jsonObj.getString("genre");
        this.createdAt = jsonObj.getLong("createdAt");
        this.updatedAt = jsonObj.getLong("updatedAt");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDirbleId() { return dirbleId;
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
