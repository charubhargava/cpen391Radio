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
import java.util.Map;

/**
 * Created by Charu Bhargava on 16-Mar-18.
 */

public class RecordingsDB {
    private static final String TAG = "RecordingsDB";
    private static final String RECORDINGS_KEY = "Recordings";
    private static final String STATION_ID_KEY = "stationId";
    private static final String TITLE_KEY = "title";
    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String RECORDING_KEY = "recording";

    private static RecordingsDB mInstance;
    private static Context mCtx;


    private static ArrayList<Recording> recordings = new ArrayList<>();

    private RecordingsDB (Context context) {
        this.mCtx = context;

    }

    public static synchronized  RecordingsDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RecordingsDB(context);
        }
        return mInstance;
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
                        if(MainActivity.debug)
                            Toast.makeText(mCtx, TAG + " Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        JSONObject recording = null;
                        try {
                            recording = response.getJSONObject(RECORDING_KEY);
                            Recording currRecording = new Recording(recording);
                            recordings.add(currRecording);
                            Toast.makeText(mCtx, "Recording scheduled!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            if(MainActivity.debug)
                                Toast.makeText(mCtx, "Error creating recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(mCtx, "Error creating recording: ", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mCtx, TAG + " Error from server: " + error.getMessage() , Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.toString());
                        Log.e(TAG, error.getStackTrace().toString());

                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
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

    }
    public void getRecordings(){
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(mCtx);
        String url = sharedPref.getRecordingsURL();
        final String userID = sharedPref.getUserId();


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(MainActivity.debug)
                            Toast.makeText(mCtx, TAG + " Response: " + response.toString(), Toast.LENGTH_LONG).show();
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

    private void addRecordings(JSONObject jsonObject){
        try {
            JSONArray recordings = jsonObject.getJSONArray(RECORDINGS_KEY);
            for ( int i = 0; i < recordings.length(); i++ ) {
                Recording currRecording = new Recording(recordings.getJSONObject(i));
                RecordingsDB.recordings.add(currRecording);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

