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

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_image_frag,container,false);
        return view;
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        StreamStatus.setListener(new StreamStatus.imageChangeListener() {
//            @Override
//            public void OnImageChange() {
//                String imageUrl = StreamStatus.getInstance(getContext()).getImageUrl();
//                if(!(imageUrl == null || imageUrl.equals(""))){
//                    //set image glide
//                    Toast.makeText(getContext(), "Image change", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}
