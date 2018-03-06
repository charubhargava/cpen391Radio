package com.example.charubhargava.tutorial1;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity to display map using google maps api
 * Reference: https://developers.google.com/maps/documentation/android-api/map-with-marker
 */

public class  MainActivity extends AppCompatActivity  implements OnMapReadyCallback {

//    private BottomNavigationView mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));

        LatLng vancouver = new LatLng(49.282,-123.121);
        googleMap.addMarker(new MarkerOptions().position(vancouver).title("Marker in vancouver"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



}

