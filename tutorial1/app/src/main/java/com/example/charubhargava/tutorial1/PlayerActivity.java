package com.example.charubhargava.tutorial1;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity to display currently playing music
 */
public class PlayerActivity extends AppCompatActivity
       {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        //App bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //Nav bar
        BottomNavigationView mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(mBottomNav);

        Menu menu = mBottomNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //do things
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        //main map activity
                        Intent mainIntent = new Intent(PlayerActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        break;

                    case R.id.menu_player:
//                        Intent playerIntent = new Intent(MainActivity.this, PlayerActivity.class);
//                        startActivity(playerIntent);
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

}
