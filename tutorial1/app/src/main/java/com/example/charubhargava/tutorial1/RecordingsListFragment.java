package com.example.charubhargava.tutorial1;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * Created by Charu Bhargava on 15-Mar-18.
 * Reference: https://github.com/mitchtabian/TabFragments/blob/master/TabFragments
 * https://github.com/Clans/FloatingActionButton
 * https://developer.android.com/training/basics/fragments/fragment-ui.html
 */

public class RecordingsListFragment extends Fragment {
    private static final String TAG = "RecordingsListFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recordings_list_frag,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = getView().findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RecordingsFragment parent = (RecordingsFragment) (RecordingsListFragment.this).getParentFragment();
            parent.setViewPager(1);
            }
        });

    }
}
