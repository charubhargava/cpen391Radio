package com.example.charubhargava.tutorial1;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Charu Bhargava on 20-Mar-18.
 * https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class RecordingsCreateFragment extends Fragment  {

    private static final String TAG = "Create Recording";
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int startDay;
    private int startMonth;
    private int startYear;
    private int endDay;
    private int endMonth;
    private int endYear;

    private EditText startDate;
    private EditText endDate;
    private EditText startTime;
    private EditText endTime;

    private List<Station> mStations;
    private String selectedStationID = " ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.recordings_create_frag, container, false);
        return v;
    }


    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

//        Button back = v.findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RecordingsFragment parent = (RecordingsFragment) RecordingsCreateFragment.this.getParentFragment();
//                parent.setViewPager(0);
//            }
//        });

        Spinner stationsSpinner = v.findViewById(R.id.spinnerStations);

        final RecordingsFragment parentFrag = (RecordingsFragment) getParentFragment();
        MainActivity parentAct = (MainActivity) parentFrag.getActivity();
        mStations = parentAct.getStations();

        ArrayAdapter<Station> dataAdapter = new ArrayAdapter<Station>(parentAct, android.R.layout.simple_spinner_item, mStations );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationsSpinner.setAdapter(dataAdapter);

        stationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView)adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.lightGrey));
                selectedStationID = mStations.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ((TextView)adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.lightGrey));
            }
        });

        startDate = v.findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setIsStart(true);
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        endDate = v.findViewById(R.id.end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setIsStart(false);
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        startTime = v.findViewById(R.id.start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setIsStart(true);
                newFragment.show(getChildFragmentManager(), "timePicker");
            }

        });


        endTime = v.findViewById(R.id.end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setIsStart(false);
                newFragment.show(getChildFragmentManager(), "timePicker");
            }

        });

        final EditText titleText = v.findViewById(R.id.rec_title);

        Button btnDone = v.findViewById(R.id.add_recording);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add some sort of checking for empty fields.. in xml maybe?
                long unixStartTime = getUnixTime(startYear, startMonth, startDay, startHour, startMinute);
                long unixEndTime = getUnixTime(endYear, endMonth, endDay, endHour, endMinute);
                String title = titleText.getText().toString();

                RecordingsDB.getInstance(getContext()).createNewRecording(selectedStationID, title, unixStartTime, unixEndTime);

                // Return to list recordings fragment
                parentFrag.setViewPager(0);
            }
        });
    }

    public void setTime(int hour, int minute, String timeDisplay, boolean isStart){
        if(isStart){
            this.startHour = hour;
            this.startMinute = minute;
            this.startTime.setText(timeDisplay);
        }
        else {
            this.endHour = hour;
            this.endMinute = minute;
            this.endTime.setText(timeDisplay);
        }
    }

    public void setDate(int year, int month, int day , String dateDisplay , boolean isStart){
        if(isStart){
            this.startDay = day;
            this.startMonth = month;
            this.startYear = year;
            this.startDate.setText(dateDisplay);
        }
        else {
            this.endDay = day;
            this.endMonth = month;
            this.endYear = year;
            this.endDate.setText(dateDisplay);
        }
    }

    long getUnixTime(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long time = c.getTimeInMillis() / 1000L;
        Log.d(TAG, Long.toString(time));
        return  time;
    }

}

