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
import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.google.android.gms.maps.*;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Activity to display map using google maps api
 * References:
 * https://developers.google.com/maps/documentation/android-api/map-with-marker
 * https://developer.android.com/training/volley/request.html
 */

public class  MainActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATIONDB_KEY = "stationDB";
    public static boolean debug = false;

    public static StationDB myStnDB = new StationDB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO this is ghetto init
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(MainActivity.this);
        setToolBar();

        //Init cache
        try{
            Reservoir.init(this, 16384);
        } catch (IOException e) {
            if(debug)
            Toast.makeText(this, "Failure init cache " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failure init cache " + e.getMessage());
        }

        //Map
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO do a get to init text display for the song playing currently
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

        //Get stations and plot
        getAllStations(googleMap);

        LatLng vancouver = new LatLng(49.282, -123.121);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));
        googleMap.setOnInfoWindowClickListener(this);


    }

    void  getAllStations(GoogleMap googleMap) {

        //Check if stationsDB exists in cache
        boolean stationDBExists = false;
        try {
            stationDBExists = Reservoir.contains(STATIONDB_KEY);
            if(stationDBExists){
                try{
                    myStnDB = Reservoir.get(STATIONDB_KEY, StationDB.class);
                    if(debug)
                        Toast.makeText(this, "Got stations from cache " + myStnDB.size(), Toast.LENGTH_SHORT).show();
                    if(myStnDB.size() > 100) {
                        plotAllStations(googleMap);
                        return;
                    }
                } catch (IOException e){
                    if(debug)
                        Toast.makeText(this, "Exception getting stn db from cache " + e.getMessage() , Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Exception getting stn db from cache " + e.getMessage());
                }
            }
        } catch (IOException e){
            if(debug)
                Toast.makeText(this, "Error checking cache " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error checking cache " + e.getMessage());
        }

        //Otherwise get from server
        getStationsFromServer(googleMap);
    }

    void getStationsFromServer(final GoogleMap googleMap) {
        SharedPrefManager sharedpref = SharedPrefManager.getInstance(this);
        String userID = sharedpref.getUserId();
        if(debug)
            Toast.makeText(this, "Getting stations by " + userID, Toast.LENGTH_SHORT).show();
        String url = sharedpref.getInstance(this).getStationsURL();

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray stationsJSON = response.getJSONArray("stations");
                            myStnDB = new StationDB(stationsJSON);
                            plotAllStations(googleMap);

                            Reservoir.putAsync(STATIONDB_KEY, myStnDB, new ReservoirPutCallback() {
                                @Override
                                public void onSuccess() {
                                    if(debug)
                                        Toast.makeText(MainActivity.this, "Success putting stations in cache", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    if(debug)
                                        Toast.makeText(MainActivity.this, "Failure putting stations in cache " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG,"Failure putting stations in cache " + e.getMessage() );
                                }
                            });

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
        googleMap.clear();
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
        if(debug)
            Toast.makeText(this, "Radio Station selected by" + userID, Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected Station ID = " + marker.getTitle());
        Log.d(TAG, "UserID = " + userID);

        StreamStatus.getInstance(MainActivity.this).updateStreamStatus(marker.getTitle(), true);

    }

}

