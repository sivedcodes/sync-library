package com.sync.library.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import com.sync.library.models.BluetoothInfo;

public class BluetoothImpl {

    private static final String FAKE_MAC = "02:00:00:00:00:00";

    @SuppressLint("HardwareIds")
    public static BluetoothInfo getBluetoothAddress(Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) return new BluetoothInfo("");
        @SuppressLint("HardwareIds")
        String address = adapter.getAddress();
        if (address == null) return new BluetoothInfo("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && FAKE_MAC.equals(address)) {
            return new BluetoothInfo("");
        }
        return new BluetoothInfo(address);
    }
}
