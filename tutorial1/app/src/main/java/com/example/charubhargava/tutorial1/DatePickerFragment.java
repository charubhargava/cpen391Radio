package com.example.charubhargava.tutorial1;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Charu Bhargava on 20-Mar-18.
 * https://developer.android.com/guide/topics/ui/controls/pickers.html
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private boolean isStart = true;

    public void setIsStart(boolean start) {
        isStart = start;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        String dateDisplay = Integer.toString(day) + "-" + Integer.toString(month+1) + "-" + Integer.toString(year);
        RecordingsCreateFragment parent = (RecordingsCreateFragment) this.getParentFragment();
        parent.setDate(year, month, day, dateDisplay, isStart);

    }

}