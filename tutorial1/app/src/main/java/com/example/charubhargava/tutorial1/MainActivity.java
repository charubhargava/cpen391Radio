package com.example.charubhargava.tutorial1;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity to display map using google maps api
 * References:
 * https://developers.google.com/maps/documentation/android-api/map-with-marker
 * https://developer.android.com/training/volley/request.html
 * https://github.com/mitchtabian/TabFragments
 */

public class  MainActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATION_DB_KEY = "stationDB";
    private static final String IMAGE_TAB_TITLE = "Now Playing";
    private static final String RECOMMENDED_TAB_TITLE = "Explore";
    private static final String HISTORY_TAB_TITLE = "Stats";
    private static final String RECORDINGS_TAB_TITLE = "Record";
    private static final int CACHE_SIZE = 16384;


    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    public static StationDB myStnDB = new StationDB();
    public static boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DO NOT REMOVE THIS
        SharedPrefManager sharedPref = SharedPrefManager.getInstance(MainActivity.this);

        setupToolBar();
        setupPlayer();
        setupCache();
        setupRecordings();

        //Map init
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Tabs init
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.tabContainer);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    //TODO Test this
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ImageFragment(), IMAGE_TAB_TITLE);
        adapter.addFragment(new RecommendFragment(), RECOMMENDED_TAB_TITLE);
        adapter.addFragment(new StatsFragment(), HISTORY_TAB_TITLE);
        adapter.addFragment(new RecordingsFragment(), RECORDINGS_TAB_TITLE);
        viewPager.setAdapter(adapter);

    }

    void setupRecordings() {
        RecordingsDB.getInstance(MainActivity.this).fetchRecordings();
    }
    void setupCache(){
        //Init cache
        try{
            Reservoir.init(this, CACHE_SIZE);
        } catch (IOException e) {
            if(debug)
                Toast.makeText(this, "Failure init cache " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failure init cache " + e.getMessage());
        }
    }

    void setupPlayer(){
        //Set up play/pause button on click listener
        final SharedPrefManager sharedPref = SharedPrefManager.getInstance(MainActivity.this);
        final ImageButton playPauseBtn = findViewById(R.id.playPause);
        final TextView stnDisplay = (TextView) findViewById(R.id.stnDisplay);
        final TextView songDisplay = (TextView) findViewById(R.id.songDisplay);
        final StreamStatus mStreamStatus = StreamStatus.getInstance(MainActivity.this);
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isPlaying = !mStreamStatus.getPlaying();
                mStreamStatus.updateStreamStatus(sharedPref.getCurrStreamID(), isPlaying );
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

        //Init stream status and player singletons, update player with the current stream status
        mStreamStatus.updateStreamStatus();
        StreamStatus.setListener(new StreamStatus.songChangeListener() {
            @Override
            public void OnSongChange() {
                stnDisplay.setText(mStreamStatus.getCurrentStation().getName());
                String songDisplayText = mStreamStatus.getCurrentSong().getTitle() + " - " + mStreamStatus.getCurrentSong().getArtist();
                songDisplay.setText(songDisplayText);
                //TODO make text scroll if too big
                boolean isPlaying = mStreamStatus.getPlaying();
                if(isPlaying) playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
                else playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        });
    }

    void setupToolBar  () {
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
            stationDBExists = Reservoir.contains(STATION_DB_KEY);
            if(stationDBExists){
                try{
                    myStnDB = Reservoir.get(STATION_DB_KEY, StationDB.class);
                    if(myStnDB.size() > 100) {
                        if(debug)
                            Toast.makeText(this, "Got stations from cache " + myStnDB.size(), Toast.LENGTH_SHORT).show();
                        plotAllStations(googleMap);
                    }
                    else {
                        if(debug)
                            Toast.makeText(this, "Getting stations from server ", Toast.LENGTH_SHORT).show();

                        //Otherwise get from server
                        getStationsFromServer(googleMap);
                    }
                } catch (IOException e){
                    if(debug)
                        Toast.makeText(this, "Exception getting stn db from cache " + e.getMessage() , Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Exception getting stn db from cache " + e.getMessage());
                }
            }
            else {
                if(debug)
                    Toast.makeText(this, "Getting stations from server ", Toast.LENGTH_SHORT).show();

                //Otherwise get from server
                getStationsFromServer(googleMap);
            }
        } catch (IOException e){
            if(debug)
                Toast.makeText(this, "Error checking cache " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error checking cache " + e.getMessage());
        }


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

                            Reservoir.putAsync(STATION_DB_KEY, myStnDB, new ReservoirPutCallback() {
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

    public ArrayList<Station> getStations(){
        return new ArrayList<Station>(myStnDB.getStations().values());
    }

}

