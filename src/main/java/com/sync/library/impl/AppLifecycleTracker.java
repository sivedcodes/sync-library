package com.sync.library.impl;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AppLifecycle";
    private int activityCount = 0;

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityCount == 0) {
            Log.d(TAG, "App in foreground");
            Async.EXECUTOR.execute(() ->
                    SyncEngine.updateAppInUse(activity.getApplicationContext(), true));
        }
        activityCount++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            Log.d(TAG, "App in background");
            Async.EXECUTOR.execute(() ->
                    SyncEngine.updateAppInUse(activity.getApplicationContext(), false));
        }
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
