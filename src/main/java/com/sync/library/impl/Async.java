package com.sync.library.impl;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Async {
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;

    static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    static final Handler MAIN = new Handler(Looper.getMainLooper());

    static void runOnMain(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            MAIN.post(r);
        }
    }

    static void shutdown() {
        EXECUTOR.shutdown();
    }
}
