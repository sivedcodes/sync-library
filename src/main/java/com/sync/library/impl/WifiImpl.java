package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.core.content.ContextCompat;

import com.sync.library.models.WifiInfo;

import java.util.ArrayList;
import java.util.List;

public class WifiImpl {

    public static List<WifiInfo> getWifi(Context context) {
        List<WifiInfo> list = new ArrayList<>();
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return list;

        android.net.wifi.WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            String bssid = connectionInfo.getBSSID() != null ? connectionInfo.getBSSID() : "";
            String ssid = connectionInfo.getSSID() != null ? connectionInfo.getSSID() : "";
            list.add(new WifiInfo(bssid, ssid));
        } else {
            list.add(new WifiInfo("", ""));
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults != null) {
                for (ScanResult result : scanResults) {
                    String ssid = result.SSID != null ? result.SSID : "";
                    String bssid = result.BSSID != null ? result.BSSID : "";
                    String capabilities = result.capabilities != null ? result.capabilities : "";
                    list.add(new WifiInfo(bssid, ssid, capabilities, result.frequency, result.level));
                }
            }
        }
        return list;
    }
}
