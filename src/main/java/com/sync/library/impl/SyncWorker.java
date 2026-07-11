package com.sync.library.impl;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sync.library.callbacks.SyncCallback;
import com.sync.library.models.SyncResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "SyncWorker started");
        final CountDownLatch latch = new CountDownLatch(1);
        final Result[] finalResult = {Result.success()};

        SyncEngine.update(getApplicationContext(), new SyncCallback() {
            @Override
            public void onResult(SyncResult result) {
                Log.d(TAG, "SyncWorker completed: " + result);
                finalResult[0] = Result.success();
                latch.countDown();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "SyncWorker error", e);
                finalResult[0] = Result.retry();
                latch.countDown();
            }
        });

        try {
            latch.await(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Log.e(TAG, "SyncWorker interrupted", e);
            return Result.retry();
        }

        return finalResult[0];
    }
}
