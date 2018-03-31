package com.example.charubhargava.tutorial1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsFragment extends Fragment {
    private static final String TAG = "StatsFragment";
    private static final String RANKINGS_KEY = "rankings";
    private static final String STATION_ID_KEY = "id";
    private static final String STATION_NAME_KEY = "name";
    private static final String DURATION_KEY = "duration";
    private static final String STATION_KEY = "station";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_frag,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchStats();
    }

    private void fetchStats(){

        SharedPrefManager sharedPref = SharedPrefManager.getInstance(getContext());
        String url = sharedPref.getStatsURL();
        final String userID = sharedPref.getUserId();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //populate list
                        populateList(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), TAG + " Error from server: " + error.toString() , Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Server error: " + error.toString());
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
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
    }

    private void populateList(JSONObject response){
        View v = getView();
        if(v == null) return;

        final ArrayList<StationDisplayItem> stations = new ArrayList<>();
        try {
            JSONArray rankings = response.getJSONArray(RANKINGS_KEY);
            //TODO add some kind of placeholder in case no stats
            for ( int i = 0; i < rankings.length(); i++ ) {
                StationDisplayItem stn;
                JSONObject curr;
                try {
                    curr = rankings.getJSONObject(i);
                } catch (JSONException e){
                    Log.e(TAG, e.getMessage());
                    continue;
                }
                JSONObject stnInfo;
                long duration  = 0;
                duration = curr.getLong(DURATION_KEY);
                stnInfo = curr.getJSONObject(STATION_KEY);
                stn = new StationDisplayItem(stnInfo.getString(STATION_NAME_KEY),stnInfo.getString(STATION_ID_KEY),duration);
                stations.add(stn);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }


        final ListView recommendListView = v.findViewById(R.id.recommended_list);
        ArrayAdapter<StationDisplayItem> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.recommended_list_item,stations);
        recommendListView.setAdapter(dataAdapter);
        recommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //play this station
                StationDisplayItem selectedStn = stations.get(i);
                if(selectedStn != null){
                    Toast.makeText(getContext(), "Selected stn name: " + selectedStn.getTitle(), Toast.LENGTH_SHORT).show();
                    StreamStatus.getInstance(getContext()).updateStreamStatus(selectedStn.getId(), true);

                }
            }
        });

    }
}
