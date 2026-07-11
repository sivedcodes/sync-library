package com.sync.library.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Tasks;
import com.sync.library.callbacks.DeviceInfoCallback;
import com.sync.library.models.DeviceInfo;
import com.sync.library.models.MobileNumber;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeviceInfoImpl {

    private static final String TAG = "DeviceInfoImpl";

    public static void getDeviceInfo(Context context, DeviceInfoCallback callback) {
        String brand = Build.BRAND != null ? Build.BRAND : "";
        String model = Build.MODEL != null ? Build.MODEL : "";
        String manufacturer = Build.MANUFACTURER != null ? Build.MANUFACTURER : "";
        String version = Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "";

        @SuppressLint("HardwareIds")
        String deviceID = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID
        );

        List<MobileNumber> numbers = MobileNumberImpl.getMobileNumber(context);
        String simCard = numbers.isEmpty() ? "" : numbers.get(0).getSimCard();
        String simNumber = numbers.isEmpty() ? "" : numbers.get(0).getPhoneNumber();

        Async.EXECUTOR.execute(() -> {
            try {
                String ip = PublicIPImpl.getPublicIPFromService("https://api.ipify.org");
                if (ip == null) ip = PublicIPImpl.getPublicIPFromService("https://checkip.amazonaws.com");
                if (ip == null) ip = "";

                String gaid = "";
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    if (adInfo != null) gaid = adInfo.getId();
                } catch (Exception e) {
                    Log.w(TAG, "Failed to get GAID", e);
                }

                double lat = 0, lng = 0;
                long time = 0;
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        FusedLocationProviderClient fusedClient =
                                LocationServices.getFusedLocationProviderClient(context);
                        android.location.Location loc = Tasks.await(
                                fusedClient.getLastLocation(), 10, TimeUnit.SECONDS
                        );
                        if (loc != null) {
                            lat = loc.getLatitude();
                            lng = loc.getLongitude();
                            time = loc.getTime();
                        }
                    } catch (Exception e) {
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                        Log.w(TAG, "Failed to get location", e);
                    }
                }

                DeviceInfo result = new DeviceInfo(
                        brand, model, manufacturer, version,
                        ip, gaid, deviceID, lat, lng, time,
                        simCard, simNumber
                );
                Async.runOnMain(() -> callback.onResult(result));
            } catch (Exception e) {
                Log.e(TAG, "getDeviceInfo failed", e);
                Async.runOnMain(() -> callback.onError(e));
            }
        });
    }
}
