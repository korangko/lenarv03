package com.example.lenarv03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenarv03.utils.CustomOrientationEventListener;
import com.example.lenarv03.utils.PermissionSupport;
import com.example.lenarv03.utils.RtspReceiver;
import com.google.android.material.tabs.TabLayout;

import org.videolan.libvlc.MediaPlayer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView alarmBtn, displayModeBtn, micBtn;
    TextureView rtspReceiveView;
    Animation open, close;
    boolean alarmOn, displayMaximized, micOn;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    //rtsp receive variables
    public static MediaPlayer mMediaPlayer = null;
    RtspReceiver mRtspReceiver = new RtspReceiver();
    String url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    //permission check
    private PermissionSupport permission;
    //test
    private static final String TAG = "JOSH";
    //Orientation Variable
    private CustomOrientationEventListener customOrientationEventListener;
    final int ROTATION_O = 1;
    final int ROTATION_90 = 2;
    final int ROTATION_180 = 3;
    final int ROTATION_270 = 4;
    private int rotAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        alarmBtn = findViewById(R.id.alarm_btn);
        alarmBtn.setOnClickListener(this);
        alarmOn = true;
        displayModeBtn = findViewById(R.id.displaymode_btn);
        displayModeBtn.setOnClickListener(this);
        displayMaximized = false;
        micBtn = findViewById(R.id.mic_btn);
        micBtn.setOnClickListener(this);
        micOn = true;

        rtspReceiveView = findViewById(R.id.rtspReceiveView);
        rtspReceiveView.setSurfaceTextureListener(mSurfaceTextureListener);

        /** tablayout **/
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Capture"));
        tabLayout.addTab(tabLayout.newTab().setText("Timelapse"));
        tabLayout.addTab(tabLayout.newTab().setText("Video"));
        tabLayout.addTab(tabLayout.newTab().setText("Live"));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        /**permission**/
        permissionCheck();
        /**RTSP size setup**/
        monitorViewSizeChange(rtspReceiveView, 1.5, true);
        /**orientation change**/
        customOrientationEventListener = new
                CustomOrientationEventListener(getBaseContext()) {
                    @Override
                    public void onSimpleOrientationChanged(int orientation) {
                        switch (orientation) {
                            case ROTATION_O:
                                rotAngle = 0;
                                break;
                            case ROTATION_90:
                                rotAngle = -90;
                                break;
                            case ROTATION_270:
                                rotAngle = 90;
                                break;
                            case ROTATION_180:
                                rotAngle = 180;
                                break;
                        }

                        rtspReceiveView.animate().rotation(rotAngle).setDuration(500).start();
                        System.out.println("josh rotate angle = " + rotAngle);
                        if (rotAngle == -90 || rotAngle == 90) {
//                            mVideoWidth = 1920;
//                            mVideoHeight = 1080;
                        } else {
//                            mVideoWidth = 1080;
//                            mVideoHeight = 1920;
                        }
                    }
                };


        /** getting wifi ssid pwd that smartphone is connected to**/
//        WifiManager mng = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        String currentSSID = mng.getConnectionInfo().getSSID();
//        String currentBSSID = mng.getConnectionInfo().getBSSID();
//        System.out.println("josh ssid = " + currentSSID);
//        System.out.println("josh bssid = " + currentBSSID);
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            permission = new PermissionSupport(this, this);
            if (!permission.checkPermission()) {
                permission.requestPermission();
            }
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    mRtspReceiver.createPlayer(MainActivity.this, url, rtspReceiveView, height, width);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    mRtspReceiver.createPlayer(MainActivity.this, url, rtspReceiveView, height, width);
                    if(width>height){
                        monitorViewSizeChange(rtspReceiveView, 1.5, false);
                    }else{
                        monitorViewSizeChange(rtspReceiveView, 1.5, true);
                    }
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                }
            };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_btn:
                if (alarmOn) {
                    alarmBtn.setImageResource(R.drawable.ic_bell_off);
                    alarmOn = false;
                } else {
                    alarmBtn.setImageResource(R.drawable.ic_bell);
                    alarmOn = true;
                }
                break;
            case R.id.displaymode_btn:
                if (displayMaximized) {
                    displayModeBtn.setImageResource(R.drawable.ic_maximize_2);
                    displayMaximized = false;
                } else {
                    ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(360, 240);
                    displayModeBtn.setImageResource(R.drawable.ic_minimize_2);
                    displayMaximized = true;
                }
                break;

            case R.id.mic_btn:
                if (micOn) {
                    micBtn.setImageResource(R.drawable.ic_mic_off);
                    micOn = false;
                } else {
                    micBtn.setImageResource(R.drawable.ic_mic);
                    micOn = true;
                }
                break;
        }
    }


    void monitorViewSizeChange(TextureView textureView, double viewRatio, boolean verticalLayout) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = getResources().getDisplayMetrics().density;
        // diplay size value in dp
        float dpHeight = metrics.heightPixels / density;
        float dpWidth = metrics.widthPixels / density;
        // diplay width value * resolutin bias = view
        int viewHeight = (int) ((metrics.widthPixels) / viewRatio); // 1.78 = 1920 / 1080 video resolution ratio  || 1.5 = 240 * 160
        int viewWidth = (int) (metrics.widthPixels);

        //parent layoutparam -> so it is constraint layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(viewWidth, viewHeight);
        if(verticalLayout) {
            params.topToBottom = R.id.linearLayout2;
            params.topMargin = 50;
        }
        textureView.setLayoutParams(params);
    }

}