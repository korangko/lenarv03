package com.example.lenarv03.livestream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lenarv03.MainActivity;
import com.example.lenarv03.MainMenuActivity;
import com.example.lenarv03.R;
import com.example.lenarv03.utils.RtmpSender;
import com.example.lenarv03.utils.RtspReceiver;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;

import static com.example.lenarv03.MainActivity.APP_NAME;
import static com.example.lenarv03.utils.RtspReceiver.vout;
import static com.example.lenarv03.utils.YouTubeApi.broadCastingUrl;
import static com.example.lenarv03.utils.YouTubeApi.credential;
import static com.example.lenarv03.utils.YouTubeApi.currentEvent;
import static com.example.lenarv03.utils.YouTubeApi.jsonFactory;
import static com.example.lenarv03.utils.YouTubeApi.transport;

public class LiveStreamActivity extends Activity implements View.OnClickListener {

    RtspReceiver mRtspReceiver = new RtspReceiver();
    RtmpSender mRtmpsender = new RtmpSender();
    TextureView rtspReceiveView;
    final int REQUEST_AUTHORIZATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestream);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.live_start_btn).setOnClickListener(this);

//        rtspReceiveView = findViewById(R.id.rtspReceiveView);
//        rtspReceiveView.setSurfaceTextureListener(mSurfaceTextureListener);

//        monitorViewSizeChange(rtspReceiveView, 1.5, true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_before_btn:
                startActivity(new Intent(LiveStreamActivity.this, MainMenuActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveStreamActivity.this.finish();
                break;
            case R.id.live_start_btn:
                StartYoutubeLive syLive = new StartYoutubeLive();
                syLive.start();
                break;

        }

    }

    public class StartYoutubeLive extends Thread {
//        GoogleAccountCredential credential;
//        HttpTransport transport = newCompatibleTransport();
//        JsonFactory jsonFactory = new GsonFactory();
        @Override
        public void run() {
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            mRtmpsender.broadcastStart(LiveStreamActivity.this, broadCastingUrl);
            String broadcId = currentEvent.getId();
            /** start live event **/
            try {
                YouTubeApi.startEvent(youtube, broadcId);
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                Log.e(MainActivity.APP_NAME, "", e);
            }
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            };

    void monitorViewSizeChange(TextureView textureView, double viewRatio, boolean verticalLayout) {
        int viewHeight;
        int viewWidth;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        /**
         float density = getResources().getDisplayMetrics().density;
         // diplay size value in dp
         float dpHeight = metrics.heightPixels / density;
         float dpWidth = metrics.widthPixels / density;
         // diplay width value * resolutin bias = view
         **/
        if (verticalLayout) {
            viewHeight = (int) ((metrics.widthPixels) / viewRatio); // 1.78 = 1920 / 1080 video resolution ratio  || 1.5 = 240 * 160
            viewWidth = (int) (metrics.widthPixels);
        } else {
            viewHeight = (int) (metrics.widthPixels); // 1.78 = 1920 / 1080 video resolution ratio  || 1.5 = 240 * 160
            viewWidth = (int) ((metrics.widthPixels) * viewRatio);
        }
        /**parent layoutparam -> so it is constraint layout**/
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(viewWidth, viewHeight);
        params.topToTop = R.id.imageView5;
        params.bottomToBottom = R.id.imageView5;
        params.startToStart = R.id.imageView5;
        params.endToEnd = R.id.imageView5;
        params.verticalBias = (float) 0.3;
        textureView.setLayoutParams(params);
        if (vout != null) {
            vout.setWindowSize(viewWidth, viewHeight);
        }
    }
}
