package com.example.lenarv03;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.lenarv03.utils.HotspotControl;
import com.example.lenarv03.utils.PermissionSupport;
import com.example.lenarv03.utils.RtspReceiver;
import com.google.android.material.tabs.TabLayout;

import org.videolan.libvlc.MediaPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    //hotpot control
    HotspotControl mHotspotControl = new HotspotControl();
    public static WifiManager.LocalOnlyHotspotReservation mReservation;

    //permission check
    private PermissionSupport permission;

    //test
    private static final String TAG = "JOSH";

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

        //permission
        permissionCheck();

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
        monitorViewSizeChange(rtspReceiveView, 1.5);


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
//                    mRtspReceiver.createPlayer(MainActivity.this, url, rtspReceiveView, height, width);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//                    mRtspReceiver.createPlayer(mainContext, url, rtspReceiveView, height, width);
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


    void monitorViewSizeChange(TextureView textureView, double viewRatio) {

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
        params.topToBottom = R.id.linearLayout2;
        params.topMargin = 20;
        textureView.setLayoutParams(params);
    }

}