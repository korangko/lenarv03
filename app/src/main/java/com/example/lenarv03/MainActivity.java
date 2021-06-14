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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

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

    /** 가능성 높은 ssid pwd 가져오기**/
    private WifiManager.LocalOnlyHotspotReservation mReservation;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOnHotspot() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    mReservation = reservation;
//                    WifiConfiguration wifiConfiguration = reservation.getWifiConfiguration();
//                    String ssid = wifiConfiguration.SSID;
//                    String pwd = wifiConfiguration.preSharedKey;

                    String ssid = "pi";
                    String password = "password";

                    WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                            .setSsid(ssid)
                            .setWpa2Passphrase(password)
                            .build();

                    NetworkRequest networkRequest = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .setNetworkSpecifier(wifiNetworkSpecifier)
                            .build();

                    ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback());

//                    System.out.println("josh Wifi Hotspot is on now" + ssid);
//                    System.out.println("josh Wifi Hotspot is on now02" + pwd);
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    System.out.println("josh Wifi Hotspot is onStopped");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    System.out.println("josh Wifi Hotspot is onFailed");
                }
            }, new Handler());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void turnOffHotspot() {
        if (mReservation != null) {
            mReservation.close();
        }
    }



    private void permissionCheck(){
        if(Build.VERSION.SDK_INT >=23){
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
                    turnOffHotspot();
                    micOn = false;
                } else {
                    micBtn.setImageResource(R.drawable.ic_mic);
                    turnOnHotspot();
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
        int viewHeight = (int)((metrics.widthPixels) / viewRatio); // 1.78 = 1920 / 1080 video resolution ratio  || 1.5 = 240 * 160
        int viewWidth = (int)(metrics.widthPixels);

        //parent layoutparam -> so it is constraint layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(viewWidth, viewHeight);
        params.topToBottom = R.id.linearLayout2;
        params.topMargin = 20;
        textureView.setLayoutParams(params);
    }

}