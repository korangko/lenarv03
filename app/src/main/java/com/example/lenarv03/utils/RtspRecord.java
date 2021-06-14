package com.example.lenarv03.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class RtspRecord {
    SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd_hhmmss");
    Date date = new Date();
    long recordingId;
    String url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";

    public void recordStart(Context context) {
        String fileSavePath = Environment.getExternalStorageDirectory() + "/MOVIES/" + formatter.format(date) + ".mp4";
        String[] ffmpegCommand = new String[]{"-i", url, "-acodec", "copy", "-vcodec", "copy", "-y", fileSavePath};

        recordingId = FFmpeg.executeAsync(ffmpegCommand, new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with rc=%d.", returnCode));
                }
            }
        });
    }

    public void recordStop(Context context) {
        FFmpeg.cancel(recordingId);
    }

    public String recordTimeCal(long startTime) {
        int minute = (int) (((System.currentTimeMillis() - startTime) / 1000) / 60);
        int second = (((int) (System.currentTimeMillis() - startTime) / 1000) % 60);
        String Time = "00:00:00";
        if (minute == 0 && second < 10) {
            Time = "00:00:0" + second;
        } else if (minute == 0) {
            Time = "00:00:" + second;
        } else if (minute < 10 && second < 10) {
            Time = "00:0" + minute + ":0" + second;
        } else if (minute < 10) {
            Time = "00:0" + minute + ":" + second;
        } else {
            Time = "00:"+minute + ":" + second;
        }
        return Time;
    }
}
