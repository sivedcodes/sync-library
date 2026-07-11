package com.sync.library.models;

public class WifiInfo {
    private String bssid;
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;

    public WifiInfo(String bssid, String ssid) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.capabilities = "";
        this.frequency = 0;
        this.level = 0;
    }

    public WifiInfo(String bssid, String ssid, String capabilities, int frequency, int level) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.capabilities = capabilities;
        this.frequency = frequency;
        this.level = level;
    }

    public String getBssid() { return bssid; }
    public String getSsid() { return ssid; }
    public String getCapabilities() { return capabilities; }
    public int getFrequency() { return frequency; }
    public int getLevel() { return level; }
}
