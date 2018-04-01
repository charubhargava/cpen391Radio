package com.example.charubhargava.tutorial1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by Charu Bhargava on 15-Mar-18.
 * Reference: https://github.com/mitchtabian/TabFragments/blob/master/TabFragments
 */

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    private String currImageUrl = "http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg";
    private String defaultUrl = "http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_image_frag,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView img = (ImageView)view.findViewById(R.id.song_image);
        StreamStatus.setImageListener(new StreamStatus.imageChangeListener() {
            @Override
            public void OnImageChange() {
                String imageUrl = StreamStatus.getInstance(getContext()).getImageUrl();
                if(!(imageUrl == null || imageUrl.equals(""))){
                    //set image glide
                    Toast.makeText(getContext(), "Image url exists", Toast.LENGTH_SHORT).show();
                    Glide.with(getContext())
                            .load(imageUrl)
                            .into(img);
                    currImageUrl = imageUrl;
                }
                else{
                    Glide.with(getContext())
                            .load(defaultUrl)
                            .into(img);
                    currImageUrl = defaultUrl;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if(v == null) return;
        ImageView img = (ImageView)getView().findViewById(R.id.song_image);
        Glide.with(getContext())
                .load(currImageUrl)
                .into(img);
    }
}
