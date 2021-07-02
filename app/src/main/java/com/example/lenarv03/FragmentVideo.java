package com.example.lenarv03;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lenarv03.utils.RtspRecord;

import org.w3c.dom.Text;

public class FragmentVideo extends Fragment implements View.OnClickListener {

    ImageView recordBtn;
    TextView recordTimeText;
    boolean recordOn;
    Animation click_btn;
    RtspRecord mRtspRecord = new RtspRecord();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recordBtn = view.findViewById(R.id.record_btn);
        recordTimeText = view.findViewById(R.id.record_time_text);
        recordBtn.setOnClickListener(this);
        recordOn = false;

        click_btn = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.btn_click);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_btn:
                recordBtn.startAnimation(click_btn);
                if(recordOn){
                    recordOn = false;
//                    mRtspRecord.recordStop(getActivity().getBaseContext());
                    recordBtn.setImageResource(R.drawable.ic_reddot);
                }else{
                    recordOn = true;
//                    mRtspRecord.recordStart(getActivity().getBaseContext());
                    recordBtn.setImageResource(R.drawable.ic_redsquare);
                    Timer timer = new Timer();
                    timer.start();
                }
                break;
        }
    }

    public class Timer extends Thread {
        final Handler handler = new Handler();
        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();
            while (recordOn) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {  // 화면에 그려줄 작업
                        recordTimeText.setText(mRtspRecord.recordTimeCal(startTime));
                    }
                });
            }
            handler.post(new Runnable() {
                @Override
                public void run() {  // 화면에 그려줄 작업
                    recordTimeText.setText("00:00:00");
                }
            });
        }
    }
}
