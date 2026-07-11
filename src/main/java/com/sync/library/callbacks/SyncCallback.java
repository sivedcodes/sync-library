package com.sync.library.callbacks;

import com.sync.library.models.SyncResult;

public interface SyncCallback {
    void onResult(SyncResult result);
    void onError(Exception e);
}
