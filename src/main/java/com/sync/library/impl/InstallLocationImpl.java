package com.sync.library.impl;

import android.content.Context;

import com.sync.library.callbacks.InstallLocationCallback;
import com.sync.library.callbacks.LocationCallback;
import com.sync.library.models.InstallLocation;
import com.sync.library.models.LocationInfo;

public class InstallLocationImpl {

    public static void getInstallLocation(Context context, InstallLocationCallback callback) {
        LocationImpl.getLocation(context, new LocationCallback() {
            @Override
            public void onResult(LocationInfo location) {
                callback.onResult(new InstallLocation(
                        location.getLat(), location.getLng(), location.getTime()
                ));
            }
            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
