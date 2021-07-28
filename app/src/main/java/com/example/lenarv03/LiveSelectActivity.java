package com.example.lenarv03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class LiveSelectActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveselect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.youtube_selectbtn).setOnClickListener(this);
        findViewById(R.id.facebook_selectbtn).setOnClickListener(this);
        findViewById(R.id.twitch_selectbtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.youtube_selectbtn:
                startActivity(new Intent(LiveSelectActivity.this, LiveYoutubeActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSelectActivity.this.finish();
                break;
            case R.id.facebook_selectbtn:
                break;
            case R.id.twitch_selectbtn:
                break;

            case R.id.menu_before_btn:
                startActivity(new Intent(LiveSelectActivity.this, MainMenuActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSelectActivity.this.finish();
                break;
        }
    }

}
