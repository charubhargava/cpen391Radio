package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StreamStatus {
    private static final String TAG = "StreamStatus";

    //POST REQ PARAMETERS
    private static final String STREAM_STATUS_KEY = "isPlaying";
    private static final String STATION_ID_KEY = "currentStream";
    private static final String USER_AGENT = "android";
    //POST RESPONSE PARAMETERS
    private static final String CURRENT_SONG_KEY = "currentSong";
    private static final String CURRENT_STATION_KEY = "currentStation";
    private static final String CURRENT_STREAM_URL_KEY = "currentStreamUrl";

    private Boolean isPlaying;
    private Station currentStation = new Station();
    private String currentStreamUrl;
    private Song currentSong = new Song();
    private static StreamStatus mInstance;
    private static Context mCtx;


    private StreamStatus(Context context) {
        this.mCtx = context;
        this.isPlaying = false;
        this.currentStreamUrl = "";
        this.currentStation = new Station();
        this.currentSong = new Song();
    }

    public static synchronized  StreamStatus getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamStatus(context);
        }
        return mInstance;
    }


    private void updateStreamStatusFields(JSONObject jsonObj) throws JSONException {
        Log.d(TAG, "jsonobj StreamStatus: " + jsonObj.toString());
        this.isPlaying = jsonObj.getBoolean(STREAM_STATUS_KEY);
        this.currentStation = new Station(jsonObj.getJSONObject(CURRENT_STATION_KEY));
        this.currentStreamUrl = jsonObj.getString(CURRENT_STREAM_URL_KEY);
        this.currentSong = new Song(jsonObj.getJSONObject(CURRENT_SONG_KEY));
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

    public void updateStreamStatus(final String streamId, final boolean isPlaying){
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        final String userID = sharedPref.getUserId();
        String url = sharedPref.getStreamURL();

        if(streamId == null){
            Toast.makeText(mCtx, TAG + " Station not available", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject streamStatusJSON = new JSONObject();
        try {
            streamStatusJSON.put(STREAM_STATUS_KEY, isPlaying);
            streamStatusJSON.put(STATION_ID_KEY, streamId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.POST, url, streamStatusJSON, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(MainActivity.debug)
                        Toast.makeText(mCtx, "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                    try {

                        updateStreamStatusFields(response);
                        sharedPref.updateCurrStreamStatus(getInstance(mCtx));
                        Player.getInstance(mCtx).updateSongInfo(currentStation.getName() + "\n" +
                                currentSong.getTitle() + " - " + currentSong.getArtist(),isPlaying);
                    } catch (JSONException e){
                        if(MainActivity.debug)
                            Toast.makeText(mCtx, "Error in update stream status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(MainActivity.debug)
                        Toast.makeText(mCtx, "Error from server: " + TAG + error.getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onErrorResponse: " + userID);

                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", USER_AGENT);
                params.put("userId", userID);
                return params;
            }
        };
        Log.d(TAG, "Stream Request: " + jsObjRequest.getBody().toString());

        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);

    }


    public void updateStreamStatus(){
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        final String userID = sharedPref.getUserId();
        String url = sharedPref.getStreamURL();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(MainActivity.debug)
                            Toast.makeText(mCtx, TAG + "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            updateStreamStatusFields(response);
                            sharedPref.updateCurrStreamStatus(getInstance(mCtx));
                            Player.getInstance(mCtx).updateSongInfo(currentStation.getName() + "\n" +
                                    currentSong.getTitle() + " - " + currentSong.getArtist(),isPlaying);
                        } catch (JSONException e){
                            Toast.makeText(mCtx, "Error in update stream status (get): " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, "Error from server: " + error.toString() , Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: " + userID);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", USER_AGENT);
                params.put("userId", userID);
                return params;
            }
        };
        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);
    }




}

