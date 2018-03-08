package com.example.charubhargava.tutorial1;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StationDB {

    private static Set<Station> stations = new HashSet<>();
    private HashMap<String, Set<Station>> locationMap = new HashMap<>();

    public StationDB(){

    }

    public StationDB(JSONArray stations) throws JSONException {

        for (int i = 0; i < stations.length(); i++)
        {
            Station s = new Station(stations.getJSONObject(i));
            this.stations.add(s);

            if(this.locationMap.containsKey(s.getCountryName())) {
                this.locationMap.get(s.getCountryName()).add(s);
            }
            else {
                Set<Station> newSet = new HashSet<Station>();
                newSet.add(s);
                this.locationMap.put(s.getCountryName(),newSet);
            }
        }
    }

    public Set<Station> getSet(){
        return stations;
    }

    public HashMap<String,Set<Station>> getLocationMap(){
        return locationMap;
    }

    public Set<Station> getSetByCountry(String country){
        Set<Station> ans = new HashSet<Station>();
        for(Station i : stations){
            if(i.getCountryName() == country){
                ans.add(i);
            }
        }
        return ans;
    }

    public List<Station> getListByGenre(String genre){
        List<Station> ans = new LinkedList<Station>();
        for(Station i : stations){
            if(i.getGenre() == genre){
                ans.add(i);
            }
        }
        return ans;
    }


    public int size(){
        return stations.size();
    }



}
