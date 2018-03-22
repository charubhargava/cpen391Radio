package com.example.charubhargava.tutorial1;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Charu Bhargava on 20-Mar-18.
 * Reference: https://developer.android.com/guide/topics/ui/controls/pickers.html
 * https://stackoverflow.com/questions/4674174/convert-integer-dates-times-to-unix-timestamp-in-java
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean isStart = true;

    public void setIsStart(boolean start) {
        isStart = start;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int displayHour = hourOfDay;
        String am_pm;
        if(displayHour >= 12) {
            if(displayHour > 12) displayHour  -= 12;
            am_pm = " PM ";
        }
        else{
            if(displayHour == 0) displayHour = 12;
            am_pm = " AM ";
        }

        String timeDisplay = Integer.toString(displayHour) + ":" + Integer.toString(minute) + am_pm;
        RecordingsCreateFragment parent = (RecordingsCreateFragment) this.getParentFragment();
        parent.setTime(hourOfDay, minute,timeDisplay, isStart);

    }
}