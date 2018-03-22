package com.example.charubhargava.tutorial1;


import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StationDB {

    private static final String TAG = "StationDB class";
    private static HashMap<String,Station> stations = new HashMap<>();

    public StationDB(){

    }

    public StationDB(JSONArray stations) {

        for (int i = 0; i < stations.length(); i++)
        {
            try {
                Station s = new Station(stations.getJSONObject(i));
                String id = s.getId();
                if (!this.stations.containsKey(id)) {
                    this.stations.put(id, s);
                }
            } catch (JSONException e){
                Log.e(TAG, e.getMessage());
            }

        }
    }

    public HashMap<String, Station> getStations(){
        return stations;
    }

    public int size(){
        return stations.size();
    }



}
