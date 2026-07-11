package com.sync.library.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceModel that = (DeviceModel) o;
        return Objects.equals(brand, that.brand)
                && Objects.equals(manufacturer, that.manufacturer)
                && Objects.equals(androidVersion, that.androidVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, manufacturer, androidVersion);
    }

    @Override
    public String toString() {
        return "DeviceModel{brand='" + brand + "', manufacturer='" + manufacturer
                + "', androidVersion='" + androidVersion + "'}";
    }
}
