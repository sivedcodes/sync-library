package com.sync.library.models;

import java.util.Objects;

public class DeviceID {
    private String deviceID;

    public DeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() { return deviceID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceID deviceID1 = (DeviceID) o;
        return Objects.equals(deviceID, deviceID1.deviceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceID);
    }

    @Override
    public String toString() {
        return "DeviceID{deviceID='" + deviceID + "'}";
    }
}
