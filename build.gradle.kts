plugins {
    id("com.android.library") version "8.2.2"
}

android {
    namespace = "com.sync.library"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.core:core:1.12.0")
}
