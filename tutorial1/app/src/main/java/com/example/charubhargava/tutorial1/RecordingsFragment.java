package com.example.charubhargava.tutorial1;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Charu Bhargava on 15-Mar-18.
 * Reference: https://github.com/mitchtabian/Fragments
 */

public class RecordingsFragment extends Fragment {
    private static final String TAG = "RecordingsListFragment";

    private static final String LIST_TAB_TITLE= "Recordings";
    private static final String CREATE_TAB_TITLE= "Add Recording";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recordings_frag,container,false);

        mSectionsPageAdapter = new SectionsPageAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.recordingsContainer);
        setupViewPager(mViewPager);
        return view;
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new RecordingsListFragment(), LIST_TAB_TITLE);
        adapter.addFragment(new RecordingsCreateFragment(), CREATE_TAB_TITLE);
        viewPager.setAdapter(adapter);

    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
        if(fragmentNumber == 0){
            SectionsPageAdapter adapter = (SectionsPageAdapter) mViewPager.getAdapter();
            RecordingsListFragment f = (RecordingsListFragment) adapter.getItem(mViewPager.getCurrentItem());
            f.updateRecordings();
        }
    }

}
