package com.example.charubhargava.tutorial1;
import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;


/**
 * Activity to display map using google maps api
 * References:
 * https://developers.google.com/maps/documentation/android-api/map-with-marker
 * https://developer.android.com/training/volley/request.html
 */

public class  MainActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static StationDB myStnDB = new StationDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO this is ghetto init
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(MainActivity.this);
        setToolBar();

        //TODO is there a reason we dont call this in OnMapReady?

        try {
            getAllStations();
        } catch (JSONException e){
            Toast.makeText(MainActivity.this, "JSON Exception getting stations " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //Map
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Play button
        final ImageButton playPauseBtn = findViewById(R.id.playPause);
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isPlaying = !sharedPref.getIsPlaying();
                StreamStatus.getInstance(MainActivity.this).updateStreamStatus(sharedPref.getCurrStreamID(), isPlaying );
                if(isPlaying){
                    //image pause
                    playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                }
                else{
                    //image play
                    playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                }
            }
        });


    }

    void setToolBar() {
        //App bar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json_names));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

//        try {
//            getAllStations();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        plotAllStations(googleMap);

        LatLng vancouver = new LatLng(49.282, -123.121);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));
        googleMap.setOnInfoWindowClickListener(this);


    }

    void  getAllStations() throws JSONException{
        SharedPrefManager sharedpref = SharedPrefManager.getInstance(this);
        String userID = sharedpref.getUserId();
//        Toast.makeText(this, "Getting stations by " + userID, Toast.LENGTH_SHORT).show();
        String url = sharedpref.getInstance(this).getStationsURL();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray stations = response.getJSONArray("stations");
                            myStnDB = new StationDB(stations);
                        } catch (JSONException e){
                            Toast.makeText(MainActivity.this, "JSONException getting stations  " +
                                    e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error getting stations: " +
                                error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Add to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    void plotAllStations(GoogleMap googleMap) {
        //Add a marker for each station
        HashMap<String, Station> stations = myStnDB.getStations();
       for (String id : stations.keySet()) {
           Station s = stations.get(id);
           LatLng stn = new LatLng(s.getCountryLat(), s.getCountryLong());
           googleMap.addMarker(new MarkerOptions().position(stn).title(id).snippet(s.getGenre()
                   + " " + s.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_white_24dp)));
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        SharedPrefManager sharedpref = SharedPrefManager.getInstance(this);
        String userID = sharedpref.getUserId();

//        Toast.makeText(this, "Radio Station selected by" + userID, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected Station ID = " + marker.getTitle());
        Log.d(TAG, "UserID = " + userID);
//
//       updateStreamStatus(marker.getTitle(), true);

        StreamStatus.getInstance(MainActivity.this).updateStreamStatus(marker.getTitle(), true);

    }



//    public void displayStreamInfo() {
//        SharedPrefManager sharedPref = SharedPrefManager.getInstance(this);
//
//        String stationName = sharedPref.getCurrStn();
//        String songTitle = sharedPref.getCurrSong();
//        String songArtist = sharedPref.getCurrArtist();
//        TextView stnDisplay = findViewById(R.id.stnDisplay);
//        String displayText = stationName + "\n" + songTitle + " - " + songArtist;
//        stnDisplay.setText(displayText);
//    }


//    void plotAllStations(GoogleMap googleMap) {
    //Add a marker for each station
//        for (Station i : myStnDB.getSet()) {
//            LatLng stn = new LatLng(i.getCountryLat(), i.getCountryLong());
//            googleMap.addMarker(new MarkerOptions().position(stn).title(i.getCountryName()).snippet(i.getStreamUrl()));
//        }

    //api to get exact location
//        for (Set<Station> i : myStnDB.getLocationMap().values()) {
//            int dx = 0;
//            for(Station s : i) {
//                 String url = "http://ip-api.com/json/" + s.getStreamUrl();
//                 JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // mTxtDisplay.setText("Response: " + response.toString());
//                        try {
//                            ((TextView) findViewById(R.id.txtDisplay)).setText(response.toString());
//                            myStnDB = new StationDB(response.getJSONArray("stations"));
//
//                            Toast.makeText(getApplicationContext(),"Loading Stations around the world", Toast.LENGTH_LONG).show();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//
//        // Access the RequestQueue through your singleton class.
//        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
//        Toast.makeText(getApplicationContext(),"Plotting Stations around the world", Toast.LENGTH_LONG).show();
//}

//    void updateStreamStatus(final String stationId, boolean isPlaying){
//        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(this);
//        String url = sharedPref.streamURL;
//        final String userID = sharedPref.getUserId();
//
//        if(stationId == null){
//            //do something
//            Toast.makeText(getApplicationContext(), "Station not available", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//            final JSONObject streamStatusJSON = new JSONObject();
//            try {
//                streamStatusJSON.put(STREAM_STATUS_KEY, isPlaying);
//                streamStatusJSON.put(STATION_ID_KEY, stationId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//            (Request.Method.POST, url, streamStatusJSON, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Toast.makeText(getApplicationContext(), "request: " + streamStatusJSON.toString(), Toast.LENGTH_LONG).show();
//
//                    Toast.makeText(getApplicationContext(), "Response: " + response.toString(), Toast.LENGTH_LONG).show();
//                    try {
//                        StreamStatus myStreamStatus = new StreamStatus(response);
//                        myStreamStatus.setStationID(stationId);
//
//                        sharedPref.updateCurrStreamStatus(myStreamStatus);
//
//                        displayStreamInfo();
//                    } catch (JSONException e){
//                        Toast.makeText(getApplicationContext(), "Error in update stream status: " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                    }
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // TODO Auto-generated method stub
//                    Toast.makeText(getApplicationContext(), "Error from server: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "onErrorResponse: " + userID);
//                }
//            }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("User-Agent", USER_AGENT);
//                params.put("userId", userID);
//                return params;
//            }
//        };
//        Log.d(TAG, "Stream Request: " + jsObjRequest.getBody().toString());
//
//        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
//
//    }


}

