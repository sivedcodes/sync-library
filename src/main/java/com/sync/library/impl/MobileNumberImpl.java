package com.sync.library.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.sync.library.models.MobileNumber;

import java.util.ArrayList;
import java.util.List;

public class MobileNumberImpl {

    public static List<MobileNumber> getMobileNumber(Context context) {
        List<MobileNumber> numbers = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            return numbers;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return numbers;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                List<SubscriptionInfo> subs = null;
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
                        == PackageManager.PERMISSION_GRANTED) {
                    SubscriptionManager sm = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    if (sm != null) {
                        subs = sm.getActiveSubscriptionInfoList();
                    }
                }
                if (subs != null && !subs.isEmpty()) {
                    for (SubscriptionInfo sub : subs) {
                        String simSlot = "SIM" + (sub.getSimSlotIndex() + 1);
                        String number = sub.getNumber() != null ? sub.getNumber() : "";
                        numbers.add(new MobileNumber(simSlot, number));
                    }
                } else {
                    addLine1Number(numbers, tm);
                }
            } catch (Exception e) {
                addLine1Number(numbers, tm);
            }
        } else {
            addLine1Number(numbers, tm);
        }
        return numbers;
    }

    private static void addLine1Number(List<MobileNumber> numbers, TelephonyManager tm) {
        String number = tm.getLine1Number();
        numbers.add(new MobileNumber("SIM1", number != null ? number : ""));
    }
}
