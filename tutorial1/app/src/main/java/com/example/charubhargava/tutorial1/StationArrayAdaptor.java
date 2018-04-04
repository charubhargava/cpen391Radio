package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Charu Bhargava on 22-Mar-18.
 * Reference :https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/
 * http://ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */

public class StationArrayAdaptor extends ArrayAdapter<StationDisplayItem>{

    private Context mCtx;
    private ArrayList<StationDisplayItem> mStations;
    private int resourceId;
    public StationArrayAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<StationDisplayItem> stations) {
        super(context, resource, stations);
        this.resourceId = resource;
        this.mCtx = context;
        this.mStations = stations;
    }

    public View getView(int position, View ConvertView, ViewGroup Parent){

        View row = ConvertView;
        StationHolder holder = null;
        if(row == null){
            LayoutInflater inflater = ((Activity)mCtx).getLayoutInflater();
            row = inflater.inflate(resourceId,Parent, false);

            holder = new StationHolder();
            //set holder texts
            row.setTag(holder);
            holder.titleTxt = (TextView)row.findViewById(R.id.stn_title);
            holder.timeTxt = (TextView)row.findViewById(R.id.stn_duration);
        }
        else{
            holder = (StationHolder)row.getTag();
        }

        final StationDisplayItem curStn = mStations.get(position);
        holder.titleTxt.setText(curStn.toString());
        holder.timeTxt.setText(curStn.getDuration());
        return row;

    }



    static class StationHolder {
        TextView titleTxt;
        TextView timeTxt;
    }


}
