package com.example.lenarv03.utils;

import android.content.Context;
import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

public class RtmpSender2 {

    //    FFmpegSession session = FFmpegKit.execute("-i file1.mp4 -c:v mpeg4 file2.mp4");
    FFmpegSession session = FFmpegKit.execute("-rtsp_transport tcp -i rtsp://demo:demo@ipvmdemo.dyndns.org:5541/onvif-media/media.amp" +
            "-tune zerolatency -ar 44100 -vcodec libx264 -pix_fmt yuv420p -threads 6 -c:v copy -f flv -flvflags no_duration_filesize "
            + "rtmp://live-sel.twitch.tv/app/live_586002078_LBVa6QJiQjfXqic4FAQyFMb5eoBRtR");

    public void broadcastStart(Context context) {

        if (ReturnCode.isSuccess(session.getReturnCode())) {

            // SUCCESS

        } else if (ReturnCode.isCancel(session.getReturnCode())) {

            // CANCEL

        } else {

            // FAILURE
            Log.d("TAG", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

        }

    }
}
