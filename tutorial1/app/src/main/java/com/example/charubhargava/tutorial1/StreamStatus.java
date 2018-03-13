package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.wearable.DataMap.TAG;

public class StreamStatus {
    private Boolean isPlaying;
    private Station currentStation = new Station();
    private String currentStreamUrl;
    private Song currentSong = new Song();
    private static StreamStatus mInstance;
    private static Context mCtx;

    //POST REQ PARAMETERS
    private static final String STREAM_STATUS_KEY = "isPlaying";
    private static final String STATION_ID_KEY = "currentStream";
    private static final String USER_AGENT = "android";
    //POST RESPONSE PARAMETERS
    private static final String CURRENT_SONG_KEY = "currentSong";
    private static final String CURRENT_STATION_KEY = "currentStation";
    private static final String CURRENT_STREAM_URL_KEY = "currentStreamUrl";

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

    public void updateStreamStatus(final String streamId, boolean isPlaying){
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        final String userID = sharedPref.getUserId();
        String url = sharedPref.streamURL;

        if(streamId == null){
            //do something
            Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(mCtx, "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                    try {

                        updateStreamStatusFields(response);
                        sharedPref.updateCurrStreamStatus(getInstance(mCtx));
                        TextView stnDisplay = (TextView)((Activity)mCtx).findViewById(R.id.stnDisplay);
                        stnDisplay.setText(currentStation.getName() + "\n" + currentSong.getTitle() + " - " + currentSong.getArtist());
//                        Toast.makeText(mCtx, "updated" , Toast.LENGTH_LONG).show();
                        ImageButton playPauseBtn = ((Activity)mCtx).findViewById(R.id.playPause);
                        boolean isPlaying = sharedPref.getIsPlaying();  //todo this is ghetto
                        if(isPlaying){
                            //image pause
                            playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                        }
                        else{
                            //image play
                            playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        }

                    } catch (JSONException e){
                        Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(mCtx, "Error in update stream status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Toast.makeText(mCtx, "Station not available", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(mCtx, "Error from server: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
        String url = sharedPref.streamURL;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(mCtx, "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        try {
                            updateStreamStatusFields(response);
                            sharedPref.updateCurrStreamStatus(getInstance(mCtx));
                            TextView stnDisplay = ((Activity)mCtx).findViewById(R.id.stnDisplay);
                            stnDisplay.setText(currentStation.getName() + "\n" + currentSong.getTitle() + " - " + currentSong.getArtist());
                            ImageButton playPauseBtn = ((Activity)mCtx).findViewById(R.id.playPause);
                            if(isPlaying){
                                //image pause
                                playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                            }
                            else{
                                //image play
                                playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                            }
                        } catch (JSONException e){
                            Toast.makeText(mCtx, "Error in update stream status (get): " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, "Error from server: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

