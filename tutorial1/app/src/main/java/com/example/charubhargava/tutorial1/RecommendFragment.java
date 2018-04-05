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
import android.widget.ProgressBar;
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

public class RecommendFragment extends Fragment {
    private static final String TAG = "RecommendationsFragment";
    private static final String RECOMMENDATIONS_KEY = "recommendations";
    private static final String STATION_ID_KEY = "id";
    private static final String STATION_NAME_KEY = "name";
    private static final String GENRE_KEY = "genre";
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
//                        Toast.makeText(getContext(), TAG + " Error from server", Toast.LENGTH_LONG).show();


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
        ProgressBar progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
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
                stn = new StationDisplayItem(curr.getString(STATION_NAME_KEY),curr.getString(STATION_ID_KEY), curr.getString(GENRE_KEY));
                stations.add(stn);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }


        final ListView recommendListView = v.findViewById(R.id.recommended_list);
        StationArrayAdaptor dataAdapter = new StationArrayAdaptor(getContext(), R.layout.recommended_list_item,stations);
        recommendListView.setAdapter(dataAdapter);
        recommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //play this station
                StationDisplayItem selectedStn = stations.get(i);
                if(selectedStn != null){
                    StreamStatus.getInstance(getContext()).updateStreamStatus(selectedStn.getId(), true);

                }
            }
        });

    }
}
