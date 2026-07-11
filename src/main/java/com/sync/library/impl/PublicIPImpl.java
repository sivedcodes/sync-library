package com.sync.library.impl;

import android.util.Log;

import com.sync.library.callbacks.PublicIPCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PublicIPImpl {

    private static final String TAG = "PublicIPImpl";
    private static final String SERVICE_PRIMARY = "https://api.ipify.org";
    private static final String SERVICE_FALLBACK = "https://checkip.amazonaws.com";

    public static void getPublicIP(PublicIPCallback callback) {
        Async.EXECUTOR.execute(() -> {
            try {
                String ip = getPublicIPFromService(SERVICE_PRIMARY);
                if (ip == null) {
                    ip = getPublicIPFromService(SERVICE_FALLBACK);
                }
                if (ip != null) {
                    final String result = ip;
                    Async.runOnMain(() -> callback.onResult(result));
                } else {
                    Async.runOnMain(() -> callback.onError(new IOException("Could not fetch public IP")));
                }
            } catch (Exception e) {
                Log.e(TAG, "getPublicIP failed", e);
                Async.runOnMain(() -> callback.onError(e));
            }
        });
    }

    static String getPublicIPFromService(String serviceUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serviceUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String ip = reader.readLine();
                return ip != null ? ip.trim() : null;
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to fetch IP from " + serviceUrl, e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
