package com.sync.library.models;

public class WifiInfo {
    private String bssid;
    private String ssid;

    public WifiInfo(String bssid, String ssid) {
        this.bssid = bssid;
        this.ssid = ssid;
    }

    public String getBssid() { return bssid; }
    public String getSsid() { return ssid; }
}
