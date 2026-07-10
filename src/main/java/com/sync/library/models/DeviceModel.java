package com.sync.library.models;

public class DeviceModel {
    private String brand;
    private String manufacturer;
    private String androidVersion;

    public DeviceModel(String brand, String manufacturer, String androidVersion) {
        this.brand = brand;
        this.manufacturer = manufacturer;
        this.androidVersion = androidVersion;
    }

    public String getBrand() { return brand; }
    public String getManufacturer() { return manufacturer; }
    public String getAndroidVersion() { return androidVersion; }
}
