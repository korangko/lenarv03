package com.example.lenarv03;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.lenarv03.utils.CustomOrientationEventListener;
import com.example.lenarv03.utils.PermissionSupport;
import com.example.lenarv03.utils.RtspReceiver;
import com.google.android.material.tabs.TabLayout;

import org.videolan.libvlc.MediaPlayer;

import static com.example.lenarv03.utils.RtspReceiver.vout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView settingBtn, layoutMiniBtn;
    TextureView rtspReceiveView;
    boolean displayMaximized;
    static public ViewPager viewPager;
    static public TabLayout tabLayout;
    //mini setting Layout
    FragmentManager manager;
    Fragment miniSettingLayout;
    //rtsp receive variables
    public static MediaPlayer mMediaPlayer = null;
    RtspReceiver mRtspReceiver = new RtspReceiver();
    String url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    //loading layout
    static public LinearLayout loadingLayout;
    static public TextView loadingPercentage;
    //permission check
    private PermissionSupport permission;
    //Orientation Variable
    private CustomOrientationEventListener customOrientationEventListener;
    final int ROTATION_O = 1;
    final int ROTATION_90 = 2;
    final int ROTATION_180 = 3;
    final int ROTATION_270 = 4;
    private int rotAngle;
    //Reconnect Layout
    int viewWidth, viewHeight;
    static public ConstraintLayout reconnectLayout;
    TextView reconnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        displayMaximized = true;
        settingBtn = findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(this);
        loadingLayout = findViewById(R.id.loading_layout);
        loadingPercentage = findViewById(R.id.loading_percentage);
        layoutMiniBtn = findViewById(R.id.layout_mini_btn);
        layoutMiniBtn.setOnClickListener(this);

        /**fragment variable**/
        manager = getSupportFragmentManager();
        miniSettingLayout = manager.findFragmentById(R.id.mini_setting_layout);

        /**reconnect**/
        reconnectLayout = findViewById(R.id.reconnect_layout);
        reconnectBtn = findViewById(R.id.reconnect_btn);
        reconnectBtn.setOnClickListener(this);
        rtspReceiveView = findViewById(R.id.rtspReceiveView);
        rtspReceiveView.setSurfaceTextureListener(mSurfaceTextureListener);

        /** tablayout **/
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Capture"));
        tabLayout.addTab(tabLayout.newTab().setText("Video"));
        tabLayout.addTab(tabLayout.newTab().setText("Timelapse"));
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
                        rtspReceiveView.animate().rotation(rotAngle).setDuration(300).start();
                        if (rotAngle == -90 || rotAngle == 90) {
                            monitorViewSizeChange(rtspReceiveView, 1.5, false);
                        } else {
                            monitorViewSizeChange(rtspReceiveView, 1.5, true);
                        }
                    }
                };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        customOrientationEventListener.enable();
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
                    viewWidth = width;
                    viewHeight = height;
                    mRtspReceiver.createPlayer(MainActivity.this, url, rtspReceiveView, height, width);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reconnect_btn:
                reconnectLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);
                mRtspReceiver.createPlayer(MainActivity.this, url, rtspReceiveView, viewHeight, viewWidth);
                break;
            case R.id.layout_mini_btn:
                if (displayMaximized) {
                    //레이아웃보이는 상태에서 클릭
                    layoutMiniBtn.setImageResource(R.drawable.ic_maximize_2);
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.hide(miniSettingLayout);
                    ft.commit();
                    displayMaximized = false;
                } else {
                    layoutMiniBtn.setImageResource(R.drawable.ic_minimize_2);
                    viewPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.show(miniSettingLayout);
                    ft.commit();
                    displayMaximized = true;
                }
                break;
            case R.id.setting_btn:
                startActivity(new Intent(MainActivity.this, StreamingSettingActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                MainActivity.this.finish();
                break;
        }
    }


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