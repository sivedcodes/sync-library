package com.sync.library.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WifiInfo wifiInfo = (WifiInfo) o;
        return frequency == wifiInfo.frequency && level == wifiInfo.level
                && Objects.equals(bssid, wifiInfo.bssid)
                && Objects.equals(ssid, wifiInfo.ssid)
                && Objects.equals(capabilities, wifiInfo.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bssid, ssid, capabilities, frequency, level);
    }

    @Override
    public String toString() {
        return "WifiInfo{bssid='" + bssid + "', ssid='" + ssid
                + "', capabilities='" + capabilities + "', frequency=" + frequency
                + ", level=" + level + "}";
    }
}
