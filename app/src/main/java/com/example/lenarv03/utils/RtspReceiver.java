package com.example.lenarv03.utils;

import android.content.Context;
import android.net.Uri;
import android.view.TextureView;
import android.view.View;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.lenarv03.MainActivity.loadingLayout;
import static com.example.lenarv03.MainActivity.loadingPercentage;
import static com.example.lenarv03.MainActivity.mMediaPlayer;
import static com.example.lenarv03.MainActivity.reconnectLayout;
import static com.example.lenarv03.MainActivity.tabLayout;
import static com.example.lenarv03.MainActivity.viewPager;


public class RtspReceiver implements IVLCVout.Callback {


    private LibVLC libvlc;
    private Media m;
    static boolean video_load_complete;
    public static IVLCVout vout;
    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    public void createPlayer(Context mContext, String url, TextureView mTextureView, int mVideoHeight, int mVideoWidth) {
        releasePlayer();
        video_load_complete = false;
        try {
            ArrayList<String> options = new ArrayList<>();
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            options.add("--avcodec-codec=h264");
//            options.add("--avcodec-codec=h265");
            options.add("--file-logging");
            options.add("--logfile=vlc-log.txt");
            options.add("--drop-late-frames");
            /**test**/
//            options.add("--video-filter=rotate");
//            options.add("--rotate-angle=90");
            /** to enable rtsp over rtp **/
//            options.add("--rtsp-tcp");
//            options.add(":network-caching=150");
//            options.add("--drop-late-frames");
//            options.add("--no-audio");
//            options.add("--avcodec-codec=h264");
//            options.add("--live-caching=150");
//            options.add(":clock-jitter=0");
//            options.add(":clock-synchro=0");
            libvlc = new LibVLC(mContext, options);
            mTextureView.setKeepScreenOn(true);
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mTextureView);
            vout.setWindowSize(mVideoWidth, mVideoHeight);
            vout.addCallback(this);
            vout.attachViews();
            m = new Media(libvlc, Uri.parse(url));
            m.setHWDecoderEnabled(true, false);
            m.addOption(":fullscreen");
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Exception e) {
        }
    }

    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<RtspReceiver> mOwner;

        //???????????? ????????? ???????????? ????????? ??????
        private MyPlayerListener(RtspReceiver owner) {
            mOwner = new WeakReference<>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            RtspReceiver player = mOwner.get();
            switch (event.type) {
                case MediaPlayer.Event.Buffering:
//                    loadingPercentage.setText((int)(event.getBuffering())+"%");
//                    if(event.getBuffering() > 99){
//                        video_load_complete = true;
//                        loadingLayout.setVisibility(View.GONE);
//                    }
                    break;

                case MediaPlayer.Event.EndReached:
                    break;
                case MediaPlayer.Event.Paused:
                    break;
                case MediaPlayer.Event.Stopped:
//                    viewPager.setVisibility(View.GONE);
//                    tabLayout.setVisibility(View.GONE);
//                    reconnectLayout.setVisibility(View.VISIBLE);
                    player.releasePlayer();
                    break;
                default:
                    break;
            }
        }
    }

    public void releasePlayer() {
        if (libvlc == null)
            return;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.removeCallback(this);
            vout.detachViews();
        }
        libvlc.release();
        libvlc = null;
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
    }
}
