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

    //    private static Set<Station> stations = new HashSet<>();
    private static HashMap<String,Station> stations = new HashMap<>();

//    private HashMap<String, Set<Station>> locationMap = new HashMap<>();

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
//            if(this.locationMap.containsKey(s.getCountryName())) {
//                this.locationMap.get(s.getCountryName()).add(s);
//            }
//            else {
//                Set<Station> newSet = new HashSet<Station>();
//                newSet.add(s);
//                this.locationMap.put(s.getCountryName(),newSet);
//            }

        }
    }

    public HashMap<String, Station> getStations(){
        return stations;
    }

//    public HashMap<String,Set<Station>> getLocationMap(){
//        return locationMap;
//    }
//
//    public Set<Station> getSetByCountry(String country){
//        Set<Station> ans = new HashSet<Station>();
//        for(Station i : stations){
//            if(i.getCountryName() == country){
//                ans.add(i);
//            }
//        }
//        return ans;
//    }
//
//    public List<Station> getListByGenre(String genre){
//        List<Station> ans = new LinkedList<Station>();
//        for(Station i : stations){
//            if(i.getGenre() == genre){
//                ans.add(i);
//            }
//        }
//        return ans;
//    }


    public int size(){
        return stations.size();
    }



}
