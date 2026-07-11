package com.sync.library.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.sync.library.models.DeviceID;

public class DeviceIDImpl {

    @SuppressLint("HardwareIds")
    public static DeviceID getDeviceID(Context context) {
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return new DeviceID(id != null ? id : "");
    }
}
