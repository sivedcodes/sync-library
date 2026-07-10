package com.sync.library.models;

public class InstalledApp {
    private String appName;
    private String appVersion;
    private String packageName;

    public InstalledApp(String appName, String appVersion, String packageName) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.packageName = packageName;
    }

    public String getAppName() { return appName; }
    public String getAppVersion() { return appVersion; }
    public String getPackageName() { return packageName; }
}
