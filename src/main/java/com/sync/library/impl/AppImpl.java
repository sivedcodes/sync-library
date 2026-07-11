package com.sync.library.impl;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.sync.library.models.InstalledApp;

import java.util.ArrayList;
import java.util.List;

public class AppImpl {

    public static List<InstalledApp> getInstalledApps(Context context) {
        List<InstalledApp> apps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(0);
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
}
