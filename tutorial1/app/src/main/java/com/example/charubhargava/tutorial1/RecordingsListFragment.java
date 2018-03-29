package com.example.charubhargava.tutorial1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;


/**
 * Created by Charu Bhargava on 15-Mar-18.
 * Reference: https://github.com/mitchtabian/TabFragments/blob/master/TabFragments
 * https://github.com/Clans/FloatingActionButton
 * https://developer.android.com/training/basics/fragments/fragment-ui.html
 */

public class RecordingsListFragment extends Fragment {
    private static final String TAG = "RecordingsListFragment";
    private RecordingsDB mRecordingsDB;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recordings_list_frag,container,false);
        Toast.makeText(getContext(), "On create view", Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(getContext(), "View Created", Toast.LENGTH_SHORT).show();
        //Get recordings and display them
        //update stream status for recording
        mRecordingsDB = RecordingsDB.getInstance(getContext());
        mRecordingsDB.fetchRecordings();

        final ListView recordingsListView = view.findViewById(R.id.recordings_list);
        mRecordingsDB.setListener(new RecordingsDB.recordingsListener() {
            @Override
            public void OnRecordingsReady() {
                if (getContext() != null) {
                    final ArrayList<Recording> recordings = new ArrayList<>();
                    recordings.addAll(RecordingsDB.getInstance(getContext()).getRecordings());
                    RecordingArrayAdaptor dataAdapter = new RecordingArrayAdaptor(getContext(), R.layout.recording_list_item, recordings);
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

        FloatingActionButton fab_add = view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            RecordingsFragment parent = (RecordingsFragment) (RecordingsListFragment.this).getParentFragment();
            parent.setViewPager(1);
            }
        });

        FloatingActionButton fab_delete = view.findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton deleteButton = view.findViewById(R.id.deleteRecording);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //delete recordings
                        Toast.makeText(getContext(), "delete recordings", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void updateRecordings() {
        mRecordingsDB.fetchRecordings();
    }

}
