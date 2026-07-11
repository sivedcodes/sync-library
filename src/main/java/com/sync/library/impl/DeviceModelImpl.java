package com.sync.library.impl;

import android.os.Build;

import com.sync.library.models.DeviceModel;

public class DeviceModelImpl {

    public static DeviceModel makeModel() {
        String brand = Build.BRAND != null ? Build.BRAND : "";
        String manufacturer = Build.MANUFACTURER != null ? Build.MANUFACTURER : "";
        String version = Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "";
        return new DeviceModel(brand, manufacturer, version);
    }
}
