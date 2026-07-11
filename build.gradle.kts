plugins {
    id("com.android.library") version "8.2.2"
    id("maven-publish")
}

android {
    namespace = "com.sync.library"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-ads-identifier:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.work:work-runtime:2.9.0")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.sivedcodes"
            artifactId = "sync-library"
            version = "1.1.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
