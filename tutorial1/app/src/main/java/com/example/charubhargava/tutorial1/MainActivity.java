package com.example.charubhargava.tutorial1;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
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
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

/**
 * Activity to display map using google maps api
 * References:
 * https://developers.google.com/maps/documentation/android-api/map-with-marker
 * https://developer.android.com/training/volley/request.html
 */

public class  MainActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //    private BottomNavigationView mBottomNav;
    private static final String NEW_DEVICE_URL = "http://my-json-feed";
    private static final String DEVICE_TOKEN_KEY = "DeviceToken";
    private static final String STREAM_STATUS_KEY = "isPlaying";
    private static final String STREAM_ID_KEY = "currentStream";


    public static StationDB myStnDB = new StationDB();
    public StreamStatus myStreamStatus = new StreamStatus();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolBar();
        registerNewDevice();
        try {
            getAllStations();
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        if (!SharedPrefManager.getInstance(this).isDeviceRegistered()) {
//            //register the new device
//            registerNewDevice();
//       }

        //Map
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void setToolBar(){
        //App bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //Nav bar
        BottomNavigationView mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);

        Menu menu = mBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //do things
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        //main map activity
                        break;

                    case R.id.menu_player:
                        Intent playerIntent = new Intent(MainActivity.this, PlayerActivity.class);
                        startActivity(playerIntent);
                        break;

                    case R.id.menu_recordings:
//                        Intent recordingsIntent = new Intent(MainActivity.this,  recordings.class);
//                        startActivity(recordingsIntent);
                        break;

                    case R.id.menu_settings:
//                        Intent settingsIntent = new Intent(MainActivity.this, settings.class);
//                        startActivity(settingsIntent);
                        break;
                }
                return true;
            }
        });
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
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        plotAllStations(googleMap);

        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        LatLng vancouver = new LatLng(49.282, -123.121);
        googleMap.addMarker(new MarkerOptions().position(vancouver).title("Marker in vancouver"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));

        googleMap.setOnInfoWindowClickListener(this);


    }


    void registerNewDevice(){
        JSONObject registerDeviceJSON = new JSONObject();
        String url = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/users";

        String android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        if(android_id == NULL){
            //do something
            Toast.makeText(getApplicationContext(), "Android ID is null", Toast.LENGTH_SHORT).show();
        }

        try{
            registerDeviceJSON.put(DEVICE_TOKEN_KEY, android_id);
        } catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, NEW_DEVICE_URL, registerDeviceJSON, new Response.Listener<JSONObject>() {
                (Request.Method.POST, url, registerDeviceJSON, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Response: " + response.toString(), Toast.LENGTH_LONG).show();
                        User myUser = null;
                        try {
                            myUser = new User(response.getJSONObject("user"), getApplicationContext());
                            ((TextView) findViewById(R.id.txtDisplay)).setText("userID: " + myUser.getUserId() + "\n" + response.getJSONObject("user").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        userId = myUser.getUserId();

                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(myUser);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add to the RequestQueue
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    void getAllStations() throws JSONException {
        //   final TextView mTxtDisplay;
        //   ImageView mImageView;

        //String url = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stations";
        String url = "https://api.myjson.com/bins/ozhup";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ((TextView) findViewById(R.id.txtDisplay)).setText("LOADING RADIO STATIONS");
                             myStnDB = new StationDB(response.getJSONArray("stations"));

                            Toast.makeText(getApplicationContext(),"Loading Stations around the world", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

    }

    void plotAllStations(GoogleMap googleMap) {
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

        for (Set<Station> i : myStnDB.getLocationMap().values()) {
            int dx = 0;
            for(Station s : i) {
                LatLng stn = new LatLng(s.getCountryLat(), s.getCountryLong()+dx);
                googleMap.addMarker(new MarkerOptions().position(stn).title(String.valueOf(s.getDirbleId())).snippet(s.getGenre() + " " + s.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_white_24dp)));

                dx ++;
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Radio Station selected by" + userId,
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected Station ID = " + marker.getTitle());
        Log.d(TAG, "UserID = " + userId);

        ((TextView) findViewById(R.id.txtDisplay)).setText("ID: " + marker.getTitle()+ "\nGenre: " + marker.getSnippet());

        updateStreamStatus(marker.getTitle());

        displayStreamInfo();

        }

    void updateStreamStatus(String stationId){
        //String url = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stream";
        String postUrl = "https://webhook.site/ef5b3a6b-5f05-4ae9-894d-7b4aff63a5da";
        String getUrl = "https://api.myjson.com/bins/1enja9";

//        if (SharedPrefManager.getInstance(this).isDeviceRegistered()) {
//            //register the new device
//            deviceToken = SharedPrefManager.getInstance(this).getDeviceToken();
//        }

        String streamId = stationId;
        if(streamId == NULL){
            //do something
            Toast.makeText(getApplicationContext(), "Station not available", Toast.LENGTH_SHORT).show();
        }
        else {
            JSONObject streamStatusJSON = new JSONObject();
            try {
                streamStatusJSON.put(STREAM_STATUS_KEY, true);
                streamStatusJSON.put(STREAM_ID_KEY, streamId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //POST
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.POST, NEW_DEVICE_URL, registerDeviceJSON, new Response.Listener<JSONObject>() {
                    (Request.Method.POST, postUrl, streamStatusJSON, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                           // try {
                                //myStreamStatus = new StreamStatus(response);
                            //} catch (JSONException e) {
                            //    e.printStackTrace();
                            //}
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onErrorResponse: " + userId);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("User-Agent", "android");
                    params.put("userId", userId);
                    return params;
                }
            };
            Log.d(TAG, "Stream Request: " + jsObjRequest.getBody().toString());

            //VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

            //GET
            JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
//                (Request.Method.POST, NEW_DEVICE_URL, registerDeviceJSON, new Response.Listener<JSONObject>() {
                    (Request.Method.GET, getUrl,null,new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Stream Response: " + response.toString());
                        myStreamStatus = new StreamStatus(response);
                       // Log.d(TAG, "updateStreamStatus: isPlaying = " + myStreamStatus.getPlaying().toString());

                        Toast.makeText(getApplicationContext(),"Stream Status: " + response.toString(), Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    // TODO Auto-generated method stub

                }
            });

            // Add to the RequestQueue
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest2);
        }
    }

    void displayStreamInfo(){
        if(myStreamStatus != null && myStreamStatus.getPlaying()) {
            Log.d(TAG, "mystreamstatus" + myStreamStatus.toString());
            String stationName = myStreamStatus.getCurrentStation().getName();
            String songTitle = myStreamStatus.getCurrentSong().getTitle();
            String songArtist = myStreamStatus.getCurrentSong().getArtist();
            String songYear = String.valueOf(myStreamStatus.getCurrentSong().getYear());
            Log.d(TAG, "Station: " + stationName);
            Log.d(TAG, "Song Title: " + songTitle);
            Log.d(TAG, "Artist: " + songArtist);
            Log.d(TAG, "Year: " + songYear);

            ((TextView) findViewById(R.id.txtDisplay)).setText("Currently Playing...\nStation: " + stationName + "\nSong: " + songTitle + "\nArtist: " + songArtist + "\nYear: " + songYear);
        }
    }

}

