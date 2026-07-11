package com.sync.library.models;

import java.util.Objects;

public class DeviceInfo {
    private final String androidBrand;
    private final String androidModel;
    private final String androidManufacturer;
    private final String androidVersion;
    private final String androidIP;
    private final String androidGaid;
    private final String androidDeviceID;
    private final double installLat;
    private final double installLng;
    private final long installTime;
    private final String simCard;
    private final String simNumber;

    public DeviceInfo(String androidBrand, String androidModel, String androidManufacturer,
                      String androidVersion, String androidIP, String androidGaid,
                      String androidDeviceID, double installLat, double installLng,
                      long installTime, String simCard, String simNumber) {
        this.androidBrand = androidBrand;
        this.androidModel = androidModel;
        this.androidManufacturer = androidManufacturer;
        this.androidVersion = androidVersion;
        this.androidIP = androidIP;
        this.androidGaid = androidGaid;
        this.androidDeviceID = androidDeviceID;
        this.installLat = installLat;
        this.installLng = installLng;
        this.installTime = installTime;
        this.simCard = simCard;
        this.simNumber = simNumber;
    }

    public String getAndroidBrand() { return androidBrand; }
    public String getAndroidModel() { return androidModel; }
    public String getAndroidManufacturer() { return androidManufacturer; }
    public String getAndroidVersion() { return androidVersion; }
    public String getAndroidIP() { return androidIP; }
    public String getAndroidGaid() { return androidGaid; }
    public String getAndroidDeviceID() { return androidDeviceID; }
    public double getInstallLat() { return installLat; }
    public double getInstallLng() { return installLng; }
    public long getInstallTime() { return installTime; }
    public String getSimCard() { return simCard; }
    public String getSimNumber() { return simNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceInfo that = (DeviceInfo) o;
        return Double.compare(installLat, that.installLat) == 0
                && Double.compare(installLng, that.installLng) == 0
                && installTime == that.installTime
                && Objects.equals(androidBrand, that.androidBrand)
                && Objects.equals(androidModel, that.androidModel)
                && Objects.equals(androidManufacturer, that.androidManufacturer)
                && Objects.equals(androidVersion, that.androidVersion)
                && Objects.equals(androidIP, that.androidIP)
                && Objects.equals(androidGaid, that.androidGaid)
                && Objects.equals(androidDeviceID, that.androidDeviceID)
                && Objects.equals(simCard, that.simCard)
                && Objects.equals(simNumber, that.simNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(androidBrand, androidModel, androidManufacturer,
                androidVersion, androidIP, androidGaid, androidDeviceID,
                installLat, installLng, installTime, simCard, simNumber);
    }

    @Override
    public String toString() {
        return "DeviceInfo{androidBrand='" + androidBrand + "', androidModel='" + androidModel
                + "', androidManufacturer='" + androidManufacturer + "', androidVersion='" + androidVersion
                + "', androidIP='" + androidIP + "', androidGaid='" + androidGaid
                + "', androidDeviceID='" + androidDeviceID + "', installLat=" + installLat
                + ", installLng=" + installLng + ", installTime=" + installTime
                + ", simCard='" + simCard + "', simNumber='" + simNumber + "'}";
    }
}
