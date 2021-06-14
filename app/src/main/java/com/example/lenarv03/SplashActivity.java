package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초
    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), ConnectActivity1.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}
