package com.sync.library.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.core.content.ContextCompat;

import com.sync.library.models.NotificationInfo;

import java.util.ArrayList;
import java.util.List;

public class NotificationImpl {

    @SuppressLint("NewApi")
    public static List<NotificationInfo> getActiveNotifications(Context context) {
        List<NotificationInfo> list = new ArrayList<>();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return list;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return list;
            }
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return list;

        StatusBarNotification[] active = nm.getActiveNotifications();
        if (active == null) return list;

        for (StatusBarNotification n : active) {
            String pkg = n.getPackageName();
            long time = n.getPostTime();
            String channelId = n.getNotification().getChannelId();

            Bundle extras = n.getNotification().extras;
            String title = extras != null ? extras.getString(android.app.Notification.EXTRA_TITLE, "") : "";
            String text = extras != null ? extras.getString(android.app.Notification.EXTRA_TEXT, "") : "";

            list.add(new NotificationInfo(pkg, title, text, time, channelId));
        }

        return list;
    }
}
