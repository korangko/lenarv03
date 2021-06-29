package com.example.lenarv03.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class HotspotControl {

    public static WifiManager.LocalOnlyHotspotReservation mReservation;
    public static boolean hotspotOn = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void turnOnHotspot(Activity mActivity) {
        WifiManager manager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);


        try {
            Log.d("HotspotSpec", ": " + manager);
            Log.d("HotspotSpec", "5Ghz : " + manager.is5GHzBandSupported());

            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {

                if (manager.isWifiEnabled())
                    manager.setWifiEnabled(false);

                manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

                    @Override
                    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                        super.onStarted(reservation);
                        mReservation = reservation;
                        WifiConfiguration mWifiConfig = mReservation.getWifiConfiguration();

                        hotspotOn = true;

                        Log.d("HotspotSpec", "Wifi Hotspot is on now");
                        Log.d("HotspotSpec", "SSID:" + mWifiConfig.SSID);
                        Log.d("HotspotSpec", "Password:" + mWifiConfig.preSharedKey);

                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                        if (mReservation != null) {
                            mReservation.close();
                            mReservation = null;
                        }
                        Log.d("HotspotSpec", "onStopped: ");
                    }

                    @Override
                    public void onFailed(int reason) {
                        super.onFailed(reason);
                        if (mReservation != null) {
                            mReservation.close();
                            mReservation = null;
                        }
                        Log.d("HotspotSpec", "onFailed: " + reason);
                    }
                }, new Handler());
                return;
            } else
                Log.d("HotspotSpec", "Required permissions not there");

        } catch (Exception e) {
            Log.d("Exception", "" + e);
        }
    }

    public void turnOffHotspot() {
        if (mReservation != null) {
            mReservation.close();
        }
    }
}
