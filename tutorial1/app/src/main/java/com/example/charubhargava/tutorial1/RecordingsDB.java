package com.example.charubhargava.tutorial1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Charu Bhargava on 16-Mar-18.
 * Reference https://guides.codepath.com/android/Creating-Custom-Listeners
 */

public class RecordingsDB {
    private static final String TAG = "RecordingsDB";
    private static final String RECORDINGS_KEY = "recordings";
    private static final String STATION_ID_KEY = "stationId";
    private static final String TITLE_KEY = "title";
    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String RECORDING_KEY = "recording";
    private static final String RECORDING_ID_KEY = "recordingId";
    private static final String DELETE_RESPONSE_KEY = "ok";

    private static RecordingsDB mInstance;
    private static Context mCtx;


    private static Set<Recording> recordings = new HashSet<>();

    private static recordingsListener listener;

    private RecordingsDB (Context context) {
        this.mCtx = context;
        this.listener = null;
    }

    public static synchronized  RecordingsDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RecordingsDB(context);
        }
        return mInstance;
    }

    public static void setListener(recordingsListener listener) {
        RecordingsDB.listener = listener;
    }

    public void createNewRecording(String stationId, String title, long startDate, long endDate){
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        String url = sharedPref.getRecordingsURL();
        final String userID = sharedPref.getUserId();

        if(stationId == null){
            Toast.makeText(mCtx, TAG + " Station not available", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject createRecordingJson = new JSONObject();
        try {
            createRecordingJson.put(STATION_ID_KEY, stationId);
            createRecordingJson.put(TITLE_KEY, title);
            createRecordingJson.put(START_DATE_KEY, startDate);
            createRecordingJson.put(END_DATE_KEY, endDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(MainActivity.debug)
            Toast.makeText(mCtx, TAG + " Req object: " + createRecordingJson.toString(), Toast.LENGTH_LONG).show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, createRecordingJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        if(MainActivity.debug)
//                            Toast.makeText(mCtx, TAG + " Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        JSONObject recording = null;
                        try {
                            recording = response.getJSONObject(RECORDING_KEY);
                            Recording currRecording = new Recording(recording);
                            recordings.add(currRecording);
                            if(RecordingsDB.listener != null) listener.OnRecordingsReady();
                            Toast.makeText(mCtx, "Recording scheduled!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            if(MainActivity.debug)
                                Toast.makeText(mCtx, "Error creating recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(mCtx, "Error creating recording", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, TAG + " Error from server", Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.toString());
                        Log.e(TAG, error.getStackTrace().toString());
                        if(error.networkResponse != null) {
                            String body;
                            //get status code here
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
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "android");
                params.put("userId", userID);
                return params;
            }
        };
        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);

    }
    public void fetchRecordings(){
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        String url = sharedPref.getRecordingsURL();
        final String userID = sharedPref.getUserId();


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addRecordings(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, TAG + " Error from server: " + error.toString() , Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: " + userID);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "android");
                params.put("userId", userID);
                return params;
            }
        };
        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);
    }

    public boolean deleteRecording(final String id){
        final boolean[] success = {false};
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        String url = sharedPref.getRecordingsURL();
        url+= ("/" + id);
        final String userID = sharedPref.getUserId();

        final JSONObject deleteRecordingJson = new JSONObject();
        try {
            deleteRecordingJson.put(RECORDING_ID_KEY, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            success[0] = response.getBoolean(DELETE_RESPONSE_KEY);
                            if(success[0]) removeRecording(id);
                        } catch (JSONException e) {
                            Log.e(TAG , e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, TAG + " Error from server: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        Log.e(TAG, "status code " + statusCode);
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                Log.e(TAG,"Body " + body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "android");
                params.put("userId", userID);
                return params;
            }
        };
        VolleySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);
        return success[0];
    }

    //Remove recording from set
    private void removeRecording(String id){
        for(Recording rec : RecordingsDB.recordings){
            if(rec.getId().equals(id)) recordings.remove(rec);
        }
        if(RecordingsDB.listener != null) listener.OnRecordingsReady();
    }

    public String  getRecordingName(String id){
        for(Recording rec : RecordingsDB.recordings){
            if(rec.getId().equals(id)) return rec.getTitle();
        }
        return " ";

    }
    private void addRecordings(JSONObject jsonObject){
        try {
            JSONArray recordings = jsonObject.getJSONArray(RECORDINGS_KEY);
            for ( int i = 0; i < recordings.length(); i++ ) {
                Recording currRecording = new Recording(recordings.getJSONObject(i));
                RecordingsDB.recordings.add(currRecording);
                Log.e(TAG, "Added recording " + currRecording.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        if(RecordingsDB.listener != null) listener.OnRecordingsReady();
    }

    public  Set<Recording> getRecordings() {
        return recordings;
    }

    public interface recordingsListener{
        public void OnRecordingsReady();
    }
}

