package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;

import androidx.core.content.ContextCompat;

import com.sync.library.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactImpl {

    public static List<Contact> getContacts(Context context, int limit) {
        List<Contact> contacts = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return contacts;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null,
                    android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );
            if (cursor != null) {
                int nameIdx = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numIdx = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER);
                int count = 0;
                while (cursor.moveToNext() && (limit <= 0 || count < limit)) {
                    String name = nameIdx >= 0 ? cursor.getString(nameIdx) : null;
                    String number = numIdx >= 0 ? cursor.getString(numIdx) : null;
                    contacts.add(new Contact(
                            name != null ? name : "Unknown",
                            number != null ? number : ""
                    ));
                    count++;
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return contacts;
    }

    public static List<Contact> getContacts(Context context) {
        return getContacts(context, 0);
    }
}
