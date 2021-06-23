package com.example.lenarv03;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        statusCheck();
//        Handler hd = new Handler();
//        hd.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    @Override
    protected void onResume() {
        super.onResume();
        statusCheck();
    }

    private class splashhandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), ConnectActivity1.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }else{
            //if location mode is enabled
            startActivity(new Intent(getApplication(), ConnectActivity1.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashActivity.this.finish();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //if location mode is enabled
//                        startActivity(new Intent(getApplication(), ConnectActivity1.class)); //로딩이 끝난 후, ChoiceFunction 이동
//                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                        SplashActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
