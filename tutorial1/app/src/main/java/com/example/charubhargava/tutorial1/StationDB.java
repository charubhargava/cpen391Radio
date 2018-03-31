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
            Station s;
            try {
                s = new Station(stations.getJSONObject(i));
            } catch (JSONException e){
                Log.d(TAG, e.getMessage());
                continue;
            }
            String id = s.getId();
            if (!StationDB.stations.containsKey(id)) {
                StationDB.stations.put(id, s);
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
