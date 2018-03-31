package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Charu Bhargava on 15-Mar-18
 * Helper methods to handle bottom slide up player
 */

public class Player {

    private static Player mInstance;
    private static Context mCtx;

    private Player(Context context) {
        mCtx = context;
    }

    public static synchronized  Player getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Player(context);
        }
        return mInstance;
    }

    public void updateSongInfo(String stn, String song, String artist, boolean isPlaying){
        TextView stnDisplay = (TextView)((Activity)mCtx).findViewById(R.id.stnDisplay);
        stnDisplay.setText(stn);

        TextView songDisplay = (TextView)((Activity)mCtx).findViewById(R.id.songDisplay);
        String songTxt = song + " by " + artist;
        songDisplay.setText(songTxt);

        ImageButton playPauseBtn = ((Activity)mCtx).findViewById(R.id.playPause);
        if(isPlaying){
            //image pause
            playPauseBtn.setImageResource(R.drawable.ic_pause_white_24dp);
        }
        else{
            //image play
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }

    }
}
