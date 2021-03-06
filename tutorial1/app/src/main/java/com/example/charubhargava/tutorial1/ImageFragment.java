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
    private String currImageUrl = "";

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
                    Glide.with(getContext())
                            .load(imageUrl)
                            .into(img);
                    currImageUrl = imageUrl;
                }
                else{
                     img.setImageResource(R.drawable.music_default);
                     currImageUrl = "";
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
        if(!(currImageUrl == null || currImageUrl.equals(""))){
        Glide.with(getContext())
                .load(currImageUrl)
                .into(img);
        }
        else{
            img.setImageResource(R.drawable.music_default);
        }

    }
}
