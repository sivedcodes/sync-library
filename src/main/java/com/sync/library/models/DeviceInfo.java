package com.sync.library.models;

import java.util.List;

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
}
