package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lenarv03.utils.WifiConnect;
import com.github.ybq.android.spinkit.SpinKitView;

import static com.example.lenarv03.utils.WifiConnect.LenarConnected;

public class ConnectActivity extends Activity {

    ImageView checkSign;
    ConstraintLayout connectionFailLayout, connectingLayout, connectionSuccessLayout;
    SpinKitView loadingSignal;
    Animation fadeinAnim;
    TextView reconnectBtn;

    WifiConnect mWifiConnect = new WifiConnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        reconnectBtn = findViewById(R.id.reconnect_btn);
        checkSign = findViewById(R.id.checksign);
        loadingSignal = findViewById(R.id.loading_signal);
        connectionFailLayout = findViewById(R.id.connection_fail_layout);
        connectingLayout = findViewById(R.id.connecting_layout);
        connectionSuccessLayout = findViewById(R.id.connection_success_layout);
        fadeinAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);

        ConnectingLenar nr = new ConnectingLenar() ;
        Thread t = new Thread(nr) ;
        t.start() ;

    }

    public void reconnect_btn_click(View view) {
        connectionFailLayout.setVisibility(View.GONE);
        connectingLayout.setVisibility(View.VISIBLE);
        ConnectingLenar nr = new ConnectingLenar() ;
        Thread t = new Thread(nr) ;
        t.start() ;
    }

    class ConnectingLenar implements Runnable {
        @Override
        public void run() {
            // connection wait till 6s and repeat 3 times
            for (int i = 0; i < 3; i++) {
                long startTime = System.currentTimeMillis();
                mWifiConnect.connectLenar(ConnectActivity.this);
                while((System.currentTimeMillis() - startTime) < 6000 && !LenarConnected) {
                    mWifiConnect.connectionCheck(ConnectActivity.this);
                }
                if(LenarConnected){
                    //if lenar is connected, breakout of for()
                    break;
                }
            }
            if(LenarConnected){
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectingLayout.setVisibility(View.GONE);
                        connectionSuccessLayout.setVisibility(View.VISIBLE);
                    }
                });
                Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(ConnectActivity.this, MainMenuActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                ConnectActivity.this.finish();
                            }
                        }, 3000);
            }else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectingLayout.setVisibility(View.GONE);
                        connectionFailLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
