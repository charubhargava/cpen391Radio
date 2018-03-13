package com.example.charubhargava.tutorial1;


import org.json.JSONException;
import org.json.JSONObject;

public class Song {

    private static final String ARTIST_KEY = "name";
    private static final String TITLE_KEY = "title";
    private static final String YEAR_KEY = "year";
    private String artist;
    private String title;
    private int year;

    public Song(){

    }

    public Song(JSONObject jsonObj) throws JSONException {
        this.artist = jsonObj.getString(ARTIST_KEY);
        this.title = jsonObj.getString(TITLE_KEY);
        this.year = jsonObj.getInt(YEAR_KEY);

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
