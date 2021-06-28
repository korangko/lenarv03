package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.lenarv03.utils.HotspotControl;

public class ConnectActivity1 extends Activity {

    ImageView signalArc, smartPhone, checkSign;
    Animation searchingAnim, fadeinAnim;

    //hotpot control
    HotspotControl mHotspotControl = new HotspotControl();
    public static WifiManager.LocalOnlyHotspotReservation mReservation;
    static public boolean hotspotOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        signalArc = findViewById(R.id.signal_arc);
        smartPhone = findViewById(R.id.smartphone);
        checkSign = findViewById(R.id.checksign);
        searchingAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        fadeinAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        signalArc.startAnimation(searchingAnim);

        Handler mHandler = new Handler();
        mHandler.postDelayed(mMyTask1, 10000);
        mHotspotControl.turnOnHotspot(ConnectActivity1.this);

    }

    private Runnable mMyTask1 = new Runnable() {
        public void run() {
            while (!hotspotOn) {

            }
            Handler mHandler = new Handler();
            mHandler.postDelayed(mMyTask2, 2000);
            signalArc.clearAnimation();
            signalArc.setVisibility(View.GONE);
            smartPhone.setVisibility(View.GONE);
            checkSign.startAnimation(fadeinAnim);
            checkSign.setVisibility(View.VISIBLE);
        }
    };

    private Runnable mMyTask2 = new Runnable() {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            ConnectActivity1.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    };

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


}
