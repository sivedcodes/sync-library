package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;

import androidx.core.content.ContextCompat;

import com.sync.library.models.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogImpl {

    public static List<CallLog> getCallLogs(Context context, int limit) {
        List<CallLog> callLogs = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            return callLogs;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    android.provider.CallLog.Calls.CONTENT_URI,
                    null, null, null,
                    android.provider.CallLog.Calls.DATE + " DESC"
            );
            if (cursor != null) {
                int nameIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
                int numIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
                int durIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
                int dateIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
                int typeIdx = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);
                int count = 0;
                while (cursor.moveToNext() && (limit <= 0 || count < limit)) {
                    String name = nameIdx >= 0 ? cursor.getString(nameIdx) : null;
                    String number = numIdx >= 0 ? cursor.getString(numIdx) : null;
                    long duration = durIdx >= 0 ? cursor.getLong(durIdx) : 0;
                    long date = dateIdx >= 0 ? cursor.getLong(dateIdx) : 0;
                    int type = typeIdx >= 0 ? cursor.getInt(typeIdx) : 0;
                    callLogs.add(new CallLog(
                            name != null ? name : "Unknown",
                            number != null ? number : "",
                            duration, date, typeToString(type)
                    ));
                    count++;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return callLogs;
    }

    public static List<CallLog> getCallLogs(Context context) {
        return getCallLogs(context, 0);
    }

    private static String typeToString(int type) {
        switch (type) {
            case android.provider.CallLog.Calls.INCOMING_TYPE: return "INCOMING";
            case android.provider.CallLog.Calls.OUTGOING_TYPE: return "OUTGOING";
            case android.provider.CallLog.Calls.MISSED_TYPE:   return "MISSED";
            default: return "UNKNOWN";
        }
    }
}
