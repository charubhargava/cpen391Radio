package com.example.charubhargava.tutorial1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    private String imageUrl = "";
    private static StreamStatus mInstance;
    private static Context mCtx;
    private static songChangeListener songListener;
    private static imageChangeListener imageListener;

    private StreamStatus(Context context) {
        this.mCtx = context;
        this.isPlaying = false;
        this.currentStreamUrl = "";
        this.currentStation = new Station();
        this.currentSong = new Song();
        this.songListener = null;
        this.imageListener = null;
    }

    public static synchronized  StreamStatus getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamStatus(context);
        }
        return mInstance;
    }


    public Boolean getPlaying() {
        return isPlaying;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public String getImageUrl(){
        return imageUrl;
    }


    public interface songChangeListener{
        public void OnSongChange();
    }

    public interface imageChangeListener{
        public void OnImageChange();
    }

    public static void setSongListener(songChangeListener songListener) {
        StreamStatus.songListener = songListener;
    }

    public static void setImageListener(imageChangeListener imageListener) {
        StreamStatus.imageListener = imageListener;
    }

    private void updateStreamStatusFields(JSONObject jsonObj)  {

        boolean playing;
        Station currStn;
        String currStreamUrl;
        Song currSong;
        try {
            playing = jsonObj.getBoolean(STREAM_STATUS_KEY);
            currStn = new Station(jsonObj.getJSONObject(CURRENT_STATION_KEY));
            currStreamUrl = jsonObj.getString(CURRENT_STREAM_URL_KEY);
            currSong = new Song(jsonObj.getJSONObject(CURRENT_SONG_KEY));
        } catch (JSONException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(mCtx, "Error updating current song", Toast.LENGTH_SHORT).show();
            return;
        }
        this.isPlaying = playing;
        this.currentStation = currStn;
        this.currentStreamUrl = currStreamUrl;
        this.currentSong = currSong;
        this.imageUrl = currSong.getImageUrl();
        if(StreamStatus.songListener != null) songListener.OnSongChange();
        if(StreamStatus.imageListener != null) imageListener.OnImageChange();
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
                        updateStreamStatusFields(response);
                        sharedPref.updateCurrStreamStatus(getInstance(mCtx));

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();

                    String body;
                    //get status code here
                    if(error.networkResponse != null){
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if (error.networkResponse.data != null) {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Body " + body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    } else Log.e(TAG, "Error from server " + error.getMessage());
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", USER_AGENT);
                params.put("userId", userID);
                Log.d(TAG, "user id is: " + userID);
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
                            updateStreamStatusFields(response);
                            sharedPref.updateCurrStreamStatus(getInstance(mCtx));
//                            Player.getInstance(mCtx).updateSongInfo(currentStation.getName(),currentSong.getTitle() , currentSong.getArtist(),isPlaying);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();

                        String body;
                        //get status code here
                        if(error.networkResponse != null){
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            if (error.networkResponse.data != null) {
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Log.e(TAG, "Body " + body);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else Log.e(TAG, "Error from server " + error.getMessage());
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

