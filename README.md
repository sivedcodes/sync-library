# Sync Library

Android library for retrieving device information.

## Add to your project

### Option 1: Module import

Copy `sync-library` folder to your project root, then add to `settings.gradle.kts`:

```kotlin
include(":sync-library")
```

Add dependency in your app's `build.gradle.kts`:

```kotlin
implementation(project(":sync-library"))
```

### Option 2: AAR

Build the library:

```bash
./gradlew :sync-library:assembleRelease
```

Use the generated AAR from `sync-library/build/outputs/aar/`.

## Usage

```java
import com.sync.library.Sync;
import com.sync.library.models.*;
```

### Call Logs

```java
List<CallLog> logs = Sync.getCallLogs(context);
// Requires: READ_CALL_LOG permission
```

### Contacts

```java
List<Contact> contacts = Sync.getContacts(context);
// Requires: READ_CONTACTS permission
```

### Installed Apps

```java
List<InstalledApp> apps = Sync.getInstalledApps(context);
```

### WiFi Info

```java
WifiInfo wifi = Sync.getWifi(context);
// Requires: ACCESS_WIFI_STATE permission
```

### Device ID

```java
DeviceID deviceID = Sync.getDeviceID(context);
```

### Google Advertising ID (async)

```java
Sync.getGaid(context, new Sync.GaidCallback() {
    @Override
    public void onResult(Gaid gaid) { }
    @Override
    public void onError(Exception e) { }
});
```

### Device Model

```java
DeviceModel model = Sync.makeModel();
```

### Location (async)

```java
Sync.getLocation(context, new Sync.LocationCallback() {
    @Override
    public void onResult(LocationInfo location) { }
    @Override
    public void onError(Exception e) { }
});
// Requires: ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION
```

### Install Location (async)

```java
Sync.getInstallLocation(context, new Sync.InstallLocationCallback() {
    @Override
    public void onResult(InstallLocation location) { }
    @Override
    public void onError(Exception e) { }
});
```

### Public IP (async)

```java
Sync.getPublicIPList(new Sync.PublicIPCallback() {
    @Override
    public void onResult(List<PublicIP> ips) { }
    @Override
    public void onError(Exception e) { }
});
// Requires: INTERNET permission
```

### Mobile Number

```java
List<MobileNumber> numbers = Sync.getMobileNumber(context);
// Requires: READ_PHONE_STATE, READ_PHONE_NUMBERS permissions
```

## Permissions

Add these to your app's `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_NUMBERS" />
```

> **Runtime permissions** must be requested at runtime for: READ_CALL_LOG, READ_CONTACTS, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE, READ_PHONE_NUMBERS (Android 6.0+).
