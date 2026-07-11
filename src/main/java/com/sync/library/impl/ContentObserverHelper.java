package com.sync.library.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.sync.library.callbacks.SyncCallback;
import com.sync.library.models.SyncResult;

import java.util.concurrent.atomic.AtomicBoolean;

public class ContentObserverHelper {

    private static final String TAG = "ContentObserverHelper";
    private static final long DEBOUNCE_MS = 30_000;

    private static Context sContext;
    private static HandlerThread sThread;
    private static Handler sHandler;
    private static ContentResolver sResolver;
    private static SyncObserver sObserver;
    private static final AtomicBoolean sRegistered = new AtomicBoolean(false);

    private static final Runnable SYNC_RUNNABLE = () -> {
        Log.d(TAG, "Debounce window passed, triggering sync");
        Context ctx = sContext;
        if (ctx == null) return;
        SyncEngine.update(ctx, new SyncCallback() {
            @Override
            public void onResult(SyncResult result) {
                Log.d(TAG, "Observer sync completed: " + result);
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Observer sync failed", e);
            }
        });
    };

    public static void register(Context context) {
        if (sRegistered.getAndSet(true)) return;

        sContext = context.getApplicationContext();
        sThread = new HandlerThread("SyncObserver");
        sThread.start();
        sHandler = new Handler(sThread.getLooper());
        sResolver = sContext.getContentResolver();
        sObserver = new SyncObserver(sHandler);

        registerUri(ContactsContract.Contacts.CONTENT_URI);
        registerUri(CallLog.Calls.CONTENT_URI);
        registerUri(Telephony.Sms.CONTENT_URI);

        Log.d(TAG, "ContentObservers registered");
    }

    public static void unregister() {
        if (!sRegistered.getAndSet(false)) return;

        if (sHandler != null) {
            sHandler.removeCallbacks(SYNC_RUNNABLE);
        }
        if (sResolver != null && sObserver != null) {
            sResolver.unregisterContentObserver(sObserver);
        }
        if (sThread != null) {
            sThread.quitSafely();
        }
        sContext = null;
        sThread = null;
        sHandler = null;
        sResolver = null;
        sObserver = null;

        Log.d(TAG, "ContentObservers unregistered");
    }

    private static void registerUri(Uri uri) {
        try {
            sResolver.registerContentObserver(uri, true, sObserver);
        } catch (SecurityException e) {
            Log.w(TAG, "Permission denied for URI: " + uri);
        }
    }

    private static class SyncObserver extends ContentObserver {

        SyncObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "Change detected: " + uri);
            sHandler.removeCallbacks(SYNC_RUNNABLE);
            sHandler.postDelayed(SYNC_RUNNABLE, DEBOUNCE_MS);
        }
    }
}
