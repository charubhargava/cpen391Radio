package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charu Bhargava on 22-Mar-18.
 * Reference :https://www.sitepoint.com/custom-data-layouts-with-your-own-android-arrayadapter/
 * http://ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
 */

public class RecordingArrayAdaptor extends ArrayAdapter<Recording>{

    private Context mCtx;
    private ArrayList<Recording> mRecordings;
    private int resourceId;
    public RecordingArrayAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<Recording> recordings) {
        super(context, resource, recordings);
        this.resourceId = resource;
        this.mCtx = context;
        this.mRecordings = recordings;
    }

    public View getView(int position, View ConvertView, ViewGroup Parent){

        View row = ConvertView;
        RecordingHolder holder = null;
        if(row == null){
            LayoutInflater inflater = ((Activity)mCtx).getLayoutInflater();
            row = inflater.inflate(resourceId,Parent, false);

            holder = new RecordingHolder();
            //set holder texts
            row.setTag(holder);
            holder.titleTxt = (TextView)row.findViewById(R.id.rec_title);
            holder.statusTxt = (TextView)row.findViewById(R.id.rec_status);
        }
        else{
            holder = (RecordingHolder)row.getTag();
        }

        Recording curRec = mRecordings.get(position);
        holder.titleTxt.setText(curRec.getTitle());
        holder.statusTxt.setText(curRec.getStatus());

        return row;

    }



    static class RecordingHolder{
        TextView titleTxt;
        TextView statusTxt;
    }
}
