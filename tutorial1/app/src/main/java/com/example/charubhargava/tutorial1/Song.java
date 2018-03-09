package com.example.charubhargava.tutorial1;


import org.json.JSONException;
import org.json.JSONObject;

public class Song {

    private String artist;
    private String title;
    private int year;

    public Song(){

    }

    public Song(JSONObject jsonObj) throws JSONException {

        this.artist = jsonObj.getString("artist");
        this.title = jsonObj.getString("title");
        this.year = jsonObj.getInt("year");

    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }
}
