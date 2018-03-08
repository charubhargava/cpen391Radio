package com.example.charubhargava.tutorial1;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class MyRadioButton implements View.OnClickListener{

    private ImageView iv;
    private TextView tv;
    private RadioButton rb;
    private View view;

    public MyRadioButton(Context context) {

        view = View.inflate(context, R.layout.my_radio_button, null);
        rb = (RadioButton) view.findViewById(R.id.radioButton1);
        tv = (TextView) view.findViewById(R.id.textView1);
        iv = (ImageView) view.findViewById(R.id.imageView1);

        view.setOnClickListener(this);
        rb.setOnCheckedChangeListener(null);

    }


    public View getView() {
        return view;
    }

    @Override
    public void onClick(View v) {

        boolean nextState = !rb.isChecked();

        LinearLayout lGroup = (LinearLayout)view.getParent();
        if(lGroup != null){
            int child = lGroup.getChildCount();
            for(int i=0; i<child; i++){
                //uncheck all
                ((RadioButton)lGroup.getChildAt(i).findViewById(R.id.radioButton1)).setChecked(false);
            }
        }

        rb.setChecked(nextState);
    }

    public void setImage(Bitmap b){
        iv.setImageBitmap(b);
    }

    public void setText(String text){
        tv.setText(text);
    }

    public void setChecked(boolean isChecked){
        rb.setChecked(isChecked);
    }


}