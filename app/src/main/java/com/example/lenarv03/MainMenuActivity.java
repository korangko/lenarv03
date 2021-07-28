package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainMenuActivity extends Activity {

    ImageView graphicImage;
    Animation scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        graphicImage =findViewById(R.id.background_circle);
        scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        graphicImage.startAnimation(scale);

    }

    public void video_btn_click(View view) {
        startActivity(new Intent(MainMenuActivity.this, MainActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        MainMenuActivity.this.finish();
    }

    public void live_btn_click(View view) {
        startActivity(new Intent(MainMenuActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        MainMenuActivity.this.finish();
    }
}
