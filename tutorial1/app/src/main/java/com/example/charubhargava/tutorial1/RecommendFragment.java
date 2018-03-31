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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendFragment extends Fragment {
    private static final String TAG = "RecommendationsFragment";
    private static final String RECOMMENDATIONS_KEY = "recommendations";
    private static final String STATION_ID_KEY = "id";
    private static final String STATION_NAME_KEY = "name";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_frag,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchRecommendedStations();
    }

    private void fetchRecommendedStations(){

        SharedPrefManager sharedPref = SharedPrefManager.getInstance(getContext());
        String url = sharedPref.getRecommendURL();
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
            JSONArray recommendations = response.getJSONArray(RECOMMENDATIONS_KEY);
            for ( int i = 0; i < recommendations.length(); i++ ) {
                StationDisplayItem stn;
                JSONObject curr;
                try {
                    curr = recommendations.getJSONObject(i);
                } catch (JSONException e){
                    Log.e(TAG, e.getMessage());
                    continue;
                }
                stn = new StationDisplayItem(curr.getString(STATION_NAME_KEY),curr.getString(STATION_ID_KEY));
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
