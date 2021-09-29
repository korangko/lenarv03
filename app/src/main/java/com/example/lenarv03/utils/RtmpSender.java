package com.example.lenarv03.utils;

import android.content.Context;
import android.util.Log;

import com.arthenica.ffmpegkit.ExecuteCallback;
import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.Session;
import com.arthenica.ffmpegkit.SessionState;
import com.arthenica.ffmpegkit.Statistics;
import com.arthenica.ffmpegkit.StatisticsCallback;

public class RtmpSender {

    long sessionId;

    /** sync broadcasting**/
    public void broadcastStart(Context context, String streamUrl) {

        /**working code**/
        FFmpegSession session = FFmpegKit.execute("-rtsp_transport tcp -i rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov -tune zerolatency -ar 44100 -vcodec libx264 -pix_fmt yuv420p -threads 6 -c:a aac -f flv " + streamUrl);
        System.out.println("josh printing test" + streamUrl);

        if (ReturnCode.isSuccess(session.getReturnCode())) {

            // SUCCESS

        } else if (ReturnCode.isCancel(session.getReturnCode())) {

            // CANCEL

        } else {

            // FAILURE
            Log.d("TAG", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

        }
    }

    /** async broadcasting**/
    public void broadcastStart2(Context context, String streamUrl) {

        FFmpegKit.executeAsync("-rtsp_transport tcp -i rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov -tune zerolatency -ar 44100 -vcodec libx264 -pix_fmt yuv420p -threads 6 -c:a aac -f flv " + streamUrl, new ExecuteCallback() {

            @Override
            public void apply(Session session) {
                SessionState state = session.getState();
                ReturnCode returnCode = session.getReturnCode();
                sessionId = session.getSessionId();
                System.out.println("josh test !!!!");

                // CALLED WHEN SESSION IS EXECUTED

                Log.d("TAG", String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.getFailStackTrace()));
            }
        }, new LogCallback() {

            @Override
            public void apply(com.arthenica.ffmpegkit.Log log) {
                // CALLED WHEN SESSION PRINTS LOGS

            }
        }, new StatisticsCallback() {

            @Override
            public void apply(Statistics statistics) {
                // CALLED WHEN SESSION GENERATES STATISTICS

            }
        });
    }

    public void broadcastStop() {
        FFmpegKit.cancel();
    }
}
