package com.example.lenarv03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lenarv03.R;

public class FragmentLenarInfo extends Fragment implements View.OnClickListener{

    LinearLayout soundBtn, micBtn;
    ImageView soundBtnImg, micBtnImg;
    TextView soundBtnText, micBtnText;
    boolean soundOn, micOn;

    public FragmentLenarInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lenar_info, container, false);

//        soundBtn = view.findViewById(R.id.sound_btn);
//        soundBtn.setOnClickListener(this);
//        soundBtnImg = view.findViewById(R.id.sound_btn_image);
//        soundBtnText = view.findViewById(R.id.sound_btn_text);
//        micBtn =view.findViewById(R.id.mic_btn);
//        micBtn.setOnClickListener(this);
//        micBtnImg = view.findViewById(R.id.mic_btn_image);
//        micBtnText = view.findViewById(R.id.mic_btn_text);
//
//        soundOn = true;
//        micOn = true;
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.sound_btn:
//                if (soundOn) {
//                    soundBtnImg.setImageResource(R.drawable.ic_volume_mute);
//                    soundBtnText.setText("Sound : Off");
//                    soundOn = false;
//                } else {
//                    soundBtnImg.setImageResource(R.drawable.ic_volume_up);
//                    soundBtnText.setText("Sound : On");
//                    soundOn = true;
//                }
//                break;
//            case R.id.mic_btn:
//                if (micOn) {
//                    micBtnImg.setImageResource(R.drawable.ic_mic_off);
//                    micBtnText.setText("Mic : Off");
//                    micOn = false;
//                } else {
//                    micBtnImg.setImageResource(R.drawable.ic_mic);
//                    micBtnText.setText("Mic : On");
//                    micOn = true;
//                }
//                break;

        }

    }
}
