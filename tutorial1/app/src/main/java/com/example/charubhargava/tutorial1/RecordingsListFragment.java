package com.example.charubhargava.tutorial1;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


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
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get recordings and display them
        //update stream status for recording
        RecordingsDB mRecordingsDB = RecordingsDB.getInstance(getContext());
        mRecordingsDB.fetchRecordings();

        final ListView recordingsListView = view.findViewById(R.id.recordings_list);

        mRecordingsDB.setListener(new RecordingsDB.recordingsListener() {
            @Override
            public void OnRecordingsReady() {
                if (getContext() != null) {
                    final ArrayList<Recording> recordings = new ArrayList<>();
                    recordings.addAll(RecordingsDB.getInstance(getContext()).getRecordings());
                    ArrayAdapter<Recording> dataAdapter = new ArrayAdapter<Recording>(getContext(), android.R.layout.simple_list_item_1, recordings);
                    recordingsListView.setAdapter(dataAdapter);

                    recordingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //play this recording
                            Recording toPlay = recordings.get(i);
                            if(toPlay.isFinished())
                                StreamStatus.getInstance(getContext()).updateStreamStatus(toPlay.getId(), true);
                            else
                                Toast.makeText(getContext(), "Recording not finished yet!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RecordingsFragment parent = (RecordingsFragment) (RecordingsListFragment.this).getParentFragment();
            parent.setViewPager(1);
            }
        });

    }
}
