package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.lenarv03.utils.HotspotControl;

public class ConnectActivity1 extends Activity {

    ImageView loadingCircle;
    Animation rotate;

    //hotpot control
    HotspotControl mHotspotControl = new HotspotControl();
    public static WifiManager.LocalOnlyHotspotReservation mReservation;
    static public boolean hotspotOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadingCircle = findViewById(R.id.loading_circle);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

//        Handler hd = new Handler(Looper.getMainLooper());
//        hd.postDelayed(new handler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초

        handler nr = new handler();
        Thread t = new Thread(nr);
        t.start();

        mHotspotControl.turnOnHotspot(ConnectActivity1.this);
        loadingCircle.startAnimation(rotate);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            String name = data.getStringExtra("name");
            Toast.makeText(getApplicationContext(), "Message from NewActivity: " + name, Toast.LENGTH_SHORT).show();
        }
    }

    private class handler implements Runnable {
        public void run() {
            while (!hotspotOn) {

            }
            loadingCircle.clearAnimation();
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            ConnectActivity1.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


}
