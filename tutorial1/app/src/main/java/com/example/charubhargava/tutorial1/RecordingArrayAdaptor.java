package com.example.charubhargava.tutorial1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
    public RecordingArrayAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<Recording> recordings) {
        super(context, resource, recordings);
        this.mCtx = context;
        this.mRecordings = recordings;
    }

//    public View getView(int position, View ConvertView, ViewGroup Parent){
//        Recording curRec = mRecordings.get(position);
//        LayoutInflater inflater = ((Activity)mCtx).getLayoutInflater();
//    }
}
