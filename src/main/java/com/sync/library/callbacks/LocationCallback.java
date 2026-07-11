package com.sync.library.callbacks;

import com.sync.library.models.LocationInfo;

public interface LocationCallback {
    void onResult(LocationInfo location);
    void onError(Exception e);
}
