package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.sync.library.callbacks.FileManagerCallback;
import com.sync.library.models.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileImpl {

    private static final String TAG = "FileImpl";

    public static void listFiles(Context context, String path, FileManagerCallback callback) {
        Async.EXECUTOR.execute(() -> {
            try {
                if (!hasStoragePermission(context)) {
                    Async.runOnMain(() -> callback.onError(
                            new SecurityException("Storage permission not granted")));
                    return;
                }
                File dir = new File(path);
                File[] files = dir.listFiles();
                List<FileInfo> result = new ArrayList<>();
                if (files != null) {
                    for (File f : files) {
                        String ext = f.isFile() ? getExtension(f.getName()) : "";
                        result.add(new FileInfo(
                                f.getName(), f.getAbsolutePath(),
                                f.length(), f.lastModified(),
                                f.isDirectory(), ext
                        ));
                    }
                }
                Async.runOnMain(() -> callback.onResult(result));
            } catch (Exception e) {
                Log.e(TAG, "listFiles failed", e);
                Async.runOnMain(() -> callback.onError(e));
            }
        });
    }

    public static void listFiles(Context context, FileManagerCallback callback) {
        listFiles(context, Environment.getExternalStorageDirectory().getAbsolutePath(), callback);
    }

    private static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO)
                    == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : "";
    }
}
