package com.sync.library.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.sync.library.callbacks.SyncCallback;
import com.sync.library.models.SyncResult;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SyncFcmService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "SyncFcmService";
    private static final String PREFS_NAME = "sync_prefs";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final long FCM_DEBOUNCE_MS = 10_000;
    private static final AtomicLong sLastFcmSync = new AtomicLong(0);

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New FCM token: " + token.substring(0, Math.min(12, token.length())) + "...");
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_FCM_TOKEN, token)
                .apply();
        subscribeToTopic();
        ContentObserverHelper.register(this);
        SyncEngine.update(this, new SyncCallback() {
            @Override
            public void onResult(SyncResult result) {
                Log.d(TAG, "Post-token sync completed: " + result);
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Post-token sync failed", e);
            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (!"sync".equals(message.getData().get("action"))) return;

        long now = System.currentTimeMillis();
        if (now - sLastFcmSync.get() < FCM_DEBOUNCE_MS) {
            Log.d(TAG, "FCM sync debounced");
            return;
        }
        sLastFcmSync.set(now);

        Log.d(TAG, "FCM sync triggered");
        ContentObserverHelper.register(this);
        SyncEngine.update(this, new SyncCallback() {
            @Override
            public void onResult(SyncResult result) {
                Log.d(TAG, "FCM sync completed: " + result);
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "FCM sync failed", e);
            }
        });
    }

    private void subscribeToTopic() {
        String topic = getTopicName(this);
        try {
            Tasks.await(FirebaseMessaging.getInstance().subscribeToTopic(topic),
                    10, TimeUnit.SECONDS);
            Log.d(TAG, "Subscribed to topic: " + topic);
        } catch (Exception e) {
            Log.e(TAG, "Failed to subscribe to topic: " + topic, e);
        }
    }

    public static String getTopicName(Context context) {
        return "sync-" + context.getPackageName().replace('.', '-');
    }

    public static String getFcmToken(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_FCM_TOKEN, "");
    }
}
