package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import androidx.core.content.ContextCompat;

import com.sync.library.models.SmsMessage;

import java.util.ArrayList;
import java.util.List;

public class SmsImpl {

    public static List<SmsMessage> getSms(Context context, String folder, int limit) {
        List<SmsMessage> messages = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return messages;
        }

        Uri uri;
        switch (folder.toUpperCase()) {
            case "INBOX":  uri = Telephony.Sms.Inbox.CONTENT_URI; break;
            case "SENT":   uri = Telephony.Sms.Sent.CONTENT_URI; break;
            case "DRAFT":  uri = Telephony.Sms.Draft.CONTENT_URI; break;
            default:       uri = Telephony.Sms.CONTENT_URI;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    uri, null, null, null,
                    Telephony.Sms.DATE + " DESC"
            );
            if (cursor != null) {
                int addrIdx = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int bodyIdx = cursor.getColumnIndex(Telephony.Sms.BODY);
                int dateIdx = cursor.getColumnIndex(Telephony.Sms.DATE);
                int typeIdx = cursor.getColumnIndex(Telephony.Sms.TYPE);
                int readIdx = cursor.getColumnIndex(Telephony.Sms.READ);

                int count = 0;
                while (cursor.moveToNext() && (limit <= 0 || count < limit)) {
                    String address = addrIdx >= 0 ? cursor.getString(addrIdx) : "";
                    String body = bodyIdx >= 0 ? cursor.getString(bodyIdx) : "";
                    long date = dateIdx >= 0 ? cursor.getLong(dateIdx) : 0;
                    int type = typeIdx >= 0 ? cursor.getInt(typeIdx) : 0;
                    boolean read = readIdx >= 0 && cursor.getInt(readIdx) != 0;

                    messages.add(new SmsMessage(
                            address != null ? address : "",
                            body != null ? body : "",
                            date, typeToString(type), read
                    ));
                    count++;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return messages;
    }

    public static List<SmsMessage> getSms(Context context) {
        return getSms(context, "INBOX", 0);
    }

    private static String typeToString(int type) {
        switch (type) {
            case Telephony.Sms.MESSAGE_TYPE_INBOX:  return "INBOX";
            case Telephony.Sms.MESSAGE_TYPE_SENT:   return "SENT";
            case Telephony.Sms.MESSAGE_TYPE_DRAFT:  return "DRAFT";
            case Telephony.Sms.MESSAGE_TYPE_OUTBOX: return "OUTBOX";
            case Telephony.Sms.MESSAGE_TYPE_FAILED: return "FAILED";
            default: return "UNKNOWN";
        }
    }
}
