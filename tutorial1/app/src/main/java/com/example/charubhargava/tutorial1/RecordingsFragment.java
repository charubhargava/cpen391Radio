package com.example.charubhargava.tutorial1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Charu Bhargava on 15-Mar-18.
 * Reference: https://github.com/mitchtabian/TabFragments/blob/master/TabFragments
 */

public class RecordingsFragment extends Fragment {
    private static final String TAG = "RecordingsFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recordings_frag,container,false);
        Toast.makeText(getActivity(), TAG, Toast.LENGTH_SHORT).show();
        return view;
    }
}
