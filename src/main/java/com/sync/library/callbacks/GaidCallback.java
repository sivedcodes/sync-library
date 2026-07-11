package com.sync.library.callbacks;

import com.sync.library.models.Gaid;

public interface GaidCallback {
    void onResult(Gaid gaid);
    void onError(Exception e);
}
