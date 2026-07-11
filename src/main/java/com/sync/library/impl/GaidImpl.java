package com.sync.library.impl;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.sync.library.callbacks.GaidCallback;
import com.sync.library.models.Gaid;

public class GaidImpl {

    private static final String TAG = "GaidImpl";

    public static void getGaid(Context context, GaidCallback callback) {
        Async.EXECUTOR.execute(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                String id = adInfo != null ? adInfo.getId() : "";
                Gaid result = new Gaid(id);
                Async.runOnMain(() -> callback.onResult(result));
            } catch (Exception e) {
                Log.e(TAG, "getGaid failed", e);
                Async.runOnMain(() -> callback.onError(e));
            }
        });
    }
}
