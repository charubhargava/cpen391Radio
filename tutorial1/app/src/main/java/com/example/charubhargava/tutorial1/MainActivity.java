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

import java.util.Set;

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
    public static StationDB myStnDB = new StationDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getAllStations();
            Toast.makeText(getApplicationContext(), "inittttttttttttttttt" + String.valueOf(myStnDB.size()), Toast.LENGTH_LONG).show();

//            Station s = myStnDB.getList().get(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (!SharedPrefManager.getInstance(this).isDeviceRegistered()) {
            //register the new device
            //           registerNewDevice();
        }

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


        //Map
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        LatLng vancouver = new LatLng(49.282, -123.121);
        googleMap.addMarker(new MarkerOptions().position(vancouver).title("Marker in vancouver"));



        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        googleMap.setOnInfoWindowClickListener(this);


    }

//
//    void registerNewDevice(){
//        JSONObject registerDeviceJSON = new JSONObject();
//        String android_id = Secure.getString(this.getContentResolver(),
//                Secure.ANDROID_ID);
//        if(android_id == NULL){
//            //do something
//            Toast.makeText(getApplicationContext(), "Android ID is null", Toast.LENGTH_SHORT).show();
//        }
//
//        try{
//            registerDeviceJSON.put(DEVICE_TOKEN_KEY, android_id);
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
////                (Request.Method.POST, NEW_DEVICE_URL, registerDeviceJSON, new Response.Listener<JSONObject>() {
//                (Request.Method.POST, "https://requestb.in/159m9pg1", registerDeviceJSON, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Toast.makeText(getApplicationContext(),"Response: " + response.toString(), Toast.LENGTH_LONG).show();
//                        User myUser = new User(response, getApplicationContext());
//                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(myUser);
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
//
//
//        // Add to the RequestQueue
//        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
//    }

    void getAllStations() throws JSONException {
        //   final TextView mTxtDisplay;
        //   ImageView mImageView;

        //String url = "http://ec2-54-201-183-2.us-west-2.compute.amazonaws.com:8080/stations";
        String url = "https://api.myjson.com/bins/ozhup";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // mTxtDisplay.setText("Response: " + response.toString());
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
                googleMap.addMarker(new MarkerOptions().position(stn).title(s.getName()).snippet(s.getGenre() + "\n" + s.getStreamUrl()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wifi_white_24dp)));

                dx ++;
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
//        ((TextView) findViewById(R.id.txtDisplay)).setText(myStnDB.getLocationMap().values().toString());

        Log.d(TAG, "Country = " + marker.getTitle());
        ((TextView) findViewById(R.id.txtDisplay)).setText("Country: " + marker.getTitle()+ "\n" + marker.getSnippet());



//        Intent scrollingIntent = new Intent(MainActivity.this, ScrollingActivity.class);
//        startActivity(scrollingIntent);


        }

//    private void addRadioButtons() {
//        LinearLayout llGroup = (LinearLayout) findViewById(R.id.linearLayoutGroup);
//        for(int i=0; i<10; i++){
//            MyRadioButton mrb = new MyRadioButton(this);
//            mrb.setText(String.valueOf(i));
//            llGroup.addView(mrb.getView());
//        }
//    }


}

