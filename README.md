# Sync Library

Android library for retrieving device information — call logs, contacts, installed apps, WiFi, location, device info, and more.

---

## Step 1: Add to your project

### Option A — Via GitHub (JitPack)

Add JitPack repository in your **root** `settings.gradle.kts` (`settings.gradle` for Groovy):

```kotlin
// settings.gradle.kts (root)
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add dependency in your **app** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.sivedcodes:sync-library:v1.0.0")
    implementation("com.google.android.gms:play-services-ads:23.0.0")   // for GAID
    implementation("com.google.android.gms:play-services-location:21.1.0") // for location
}
```

[![](https://jitpack.io/v/sivedcodes/sync-library.svg)](https://jitpack.io/#sivedcodes/sync-library)

### Option B — Local module

Copy `sync-library/` folder into your project root, then:

**`settings.gradle.kts`**:

```kotlin
include(":sync-library")
```

**app `build.gradle.kts`**:

```kotlin
dependencies {
    implementation(project(":sync-library"))
}
```

---

## Step 2: Add permissions

Add these to your app's `AndroidManifest.xml` inside `<manifest>`:

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

---

## Step 3: Request runtime permissions

Example function to request multiple permissions in Kotlin:

```kotlin
private fun requestPermissions() {
    val permissions = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS
    )
    ActivityCompat.requestPermissions(this, permissions, 100)
}
```

Handle result:

```kotlin
override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>, grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 100) {
        // permissions granted — now call Sync methods
    }
}
```

---

## Step 4: Usage

### Call Logs

```kotlin
val logs = Sync.getCallLogs(this)  // List<CallLog>
logs.forEach { log ->
    log.name
    log.phoneNumber
    log.duration
    log.timestamp
    log.callType   // "INCOMING", "OUTGOING", "MISSED"
}
```

Requires: `READ_CALL_LOG` runtime permission.

---

### Contacts

```kotlin
val contacts = Sync.getContacts(this)  // List<Contact>
contacts.forEach { contact ->
    contact.name
    contact.phoneNumber
}
```

Requires: `READ_CONTACTS` runtime permission.

---

### Installed Apps

```kotlin
val apps = Sync.getInstalledApps(this)  // List<InstalledApp>
apps.forEach { app ->
    app.appName
    app.appVersion
    app.packageName
}
```

---

### WiFi Info

```kotlin
val wifi = Sync.getWifi(this)  // WifiInfo
wifi.bssid
wifi.ssid
```

Requires: `ACCESS_WIFI_STATE` permission (manifest only, no runtime).

---

### Device ID

```kotlin
val deviceId = Sync.getDeviceID(this)  // DeviceID
deviceId.deviceID   // Android ID
```

---

### Google Advertising ID (async)

```kotlin
Sync.getGaid(this, object : Sync.GaidCallback {
    override fun onResult(gaid: Gaid) {
        gaid.gaid   // Advertising ID
    }
    override fun onError(e: Exception) {
        e.printStackTrace()
    }
})
```

Requires: Google Play Services and `INTERNET` permission.

---

### Device Model

```kotlin
val model = Sync.makeModel()  // DeviceModel
model.brand
model.manufacturer
model.androidVersion
```

---

### Location (async)

```kotlin
Sync.getLocation(this, object : Sync.LocationCallback {
    override fun onResult(location: LocationInfo) {
        location.lat
        location.lng
        location.time
    }
    override fun onError(e: Exception) { }
})
```

Requires: `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` runtime permission.

---

### Install Location (async)

```kotlin
Sync.getInstallLocation(this, object : Sync.InstallLocationCallback {
    override fun onResult(location: InstallLocation) {
        location.lat
        location.lng
        location.time
    }
    override fun onError(e: Exception) { }
})
```

---

### Public IP (async)

```kotlin
Sync.getPublicIP(object : Sync.PublicIPCallback {
    override fun onResult(ip: String) {
        // "203.0.113.42"
    }
    override fun onError(e: Exception) { }
})
```

Requires: `INTERNET` permission.

---

### Mobile Number

```kotlin
val numbers = Sync.getMobileNumber(this)  // List<MobileNumber>
numbers.forEach { number ->
    number.simCard    // "SIM1", "SIM2", etc.
    number.phoneNumber
}
```

Requires: `READ_PHONE_STATE` + `READ_PHONE_NUMBERS` runtime permissions.

---

## Step 5: Coroutine wrappers (optional)

If you want to use async methods with coroutines:

```kotlin
suspend fun getGaidSuspend(context: Context): Gaid = suspendCancellableCoroutine { cont ->
    Sync.getGaid(context, object : Sync.GaidCallback {
        override fun onResult(gaid: Gaid) { cont.resume(gaid) }
        override fun onError(e: Exception) { cont.resumeWithException(e) }
    })
}
```

Then call from a coroutine scope:

```kotlin
lifecycleScope.launch {
    val gaid = getGaidSuspend(this@MainActivity)
    textView.text = gaid.gaid
}
```
