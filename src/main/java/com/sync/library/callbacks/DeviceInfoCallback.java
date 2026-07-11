package com.sync.library.callbacks;

import com.sync.library.models.DeviceInfo;

public interface DeviceInfoCallback {
    void onResult(DeviceInfo info);
    void onError(Exception e);
}
