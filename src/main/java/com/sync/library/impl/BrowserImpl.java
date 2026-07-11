package com.sync.library.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.sync.library.models.BrowserData;

import java.util.ArrayList;
import java.util.List;

public class BrowserImpl {

    private static final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
    private static final String PERM_READ_HISTORY = "com.android.browser.permission.READ_HISTORY_BOOKMARKS";

    public static List<BrowserData> getBrowserData(Context context, int limit) {
        List<BrowserData> list = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, PERM_READ_HISTORY)
                != PackageManager.PERMISSION_GRANTED) {
            return list;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    BOOKMARKS_URI, null, null, null, "date DESC"
            );
            if (cursor != null) {
                int titleIdx = cursor.getColumnIndex("title");
                int urlIdx = cursor.getColumnIndex("url");
                int visitsIdx = cursor.getColumnIndex("visits");
                int bookmarkIdx = cursor.getColumnIndex("bookmark");
                int createdIdx = cursor.getColumnIndex("created");

                int count = 0;
                while (cursor.moveToNext() && (limit <= 0 || count < limit)) {
                    String title = titleIdx >= 0 ? cursor.getString(titleIdx) : "";
                    String url = urlIdx >= 0 ? cursor.getString(urlIdx) : "";
                    long visits = visitsIdx >= 0 ? cursor.getLong(visitsIdx) : 0;
                    boolean isBookmark = bookmarkIdx >= 0 && cursor.getInt(bookmarkIdx) != 0;
                    long created = createdIdx >= 0 ? cursor.getLong(createdIdx) : 0;

                    list.add(new BrowserData(
                            title != null ? title : "",
                            url != null ? url : "",
                            visits, isBookmark, created
                    ));
                    count++;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public static List<BrowserData> getBrowserData(Context context) {
        return getBrowserData(context, 0);
    }
}
