package com.sync.library.models;

import java.util.Objects;

public class BluetoothInfo {
    private String bluetoothAddress;

    public BluetoothInfo(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getBluetoothAddress() { return bluetoothAddress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothInfo that = (BluetoothInfo) o;
        return Objects.equals(bluetoothAddress, that.bluetoothAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bluetoothAddress);
    }

    @Override
    public String toString() {
        return "BluetoothInfo{bluetoothAddress='" + bluetoothAddress + "'}";
    }
}
