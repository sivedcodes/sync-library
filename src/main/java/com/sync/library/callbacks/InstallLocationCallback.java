package com.sync.library.callbacks;

import com.sync.library.models.InstallLocation;

public interface InstallLocationCallback {
    void onResult(InstallLocation installLocation);
    void onError(Exception e);
}
