package com.sync.library.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstalledApp that = (InstalledApp) o;
        return Objects.equals(appName, that.appName)
                && Objects.equals(appVersion, that.appVersion)
                && Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, appVersion, packageName);
    }

    @Override
    public String toString() {
        return "InstalledApp{appName='" + appName + "', appVersion='" + appVersion
                + "', packageName='" + packageName + "'}";
    }
}
