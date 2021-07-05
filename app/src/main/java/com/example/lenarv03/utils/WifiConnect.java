package com.example.lenarv03.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.Context.WIFI_SERVICE;

public class WifiConnect {

    public static Boolean LenarConnected = false;
    String LenarSSID = "T_GiGA_5G_Wave2_6613";


    public void connectLenar(Activity mActivity) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        WifiConfiguration wifiConfig = new WifiConfiguration(); // 와이파이 연결하기
        wifiConfig.SSID = String.format("\"%s\"", LenarSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", "5gk10zg245");
        WifiManager wifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        // turn on wifi mode if it's not on
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        Log.d("wificonnection", ": " + "connection started");
    }

    public void connectionCheck(Activity mActivity) {
        /** getting wifi ssid pwd that smartphone is connected to**/
//        WifiManager mng = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        String currentSSID = mng.getConnectionInfo().getSSID();
//        String currentBSSID = mng.getConnectionInfo().getBSSID();
//        System.out.println("josh ssid = " + currentSSID);
//        System.out.println("josh bssid = " + currentBSSID);

        WifiManager wifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String ssid = wifiInfo.getSSID();
            if (ssid.equals(String.format("\"%s\"", LenarSSID))) {
                LenarConnected = true;
                Log.d("wificonnection", ": " + "connection success");
            }
        }
    }

}



