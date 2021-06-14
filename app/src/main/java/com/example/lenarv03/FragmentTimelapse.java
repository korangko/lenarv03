package com.example.lenarv03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

public class FragmentTimelapse extends Fragment implements View.OnClickListener {

    ImageView timelapseBtn,timeLapseBtn2;
    NumberPicker frPicker;
    boolean timelapseOn;
    Animation rotate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timelapse, container, false);

        timelapseBtn = view.findViewById(R.id.timelapse_btn);
        timelapseBtn.setOnClickListener(this);
        timeLapseBtn2 = view.findViewById(R.id.timelapse_btn2);

        //frame rate number picker
        frPicker = view.findViewById(R.id.framerate_picker);
        frPicker.setMinValue(0);
        frPicker.setMaxValue(3);
        frPicker.setDisplayedValues(new String[]{
                "2 Frame", "3 Frame", "5 Frame", "10 Frame"});
        frPicker.setWrapSelectorWheel(false);
//        frPicker.setSelectionDividerHeight(0);
        frPicker.setDividerPadding(5);


        timelapseOn = false;
        rotate = AnimationUtils.loadAnimation(getActivity().getBaseContext(),R.anim.rotate);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timelapse_btn:
                if(timelapseOn){
                    timelapseOn = false;
                    timeLapseBtn2.clearAnimation();
                    timelapseBtn.setImageResource(R.drawable.ic_reddot);
                }else{
                    timelapseOn = true;
                    timeLapseBtn2.startAnimation(rotate);
                    timelapseBtn.setImageResource(R.drawable.ic_redsquare);
                }
                break;
        }
    }
}