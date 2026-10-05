plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
}

import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

/**
 * Reads a key from a properties file, defaulting to `keystore.properties` at the root.
 *
 * Ported 1:1 from radioplayer-automotive-appstore/app/build.gradle.kts.
 */
fun getLocalProperty(key: String, file: String = "keystore.properties"): String? {
    val localProperties = File("$rootDir/$file")
    val properties = Properties()

    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")

    return properties.getProperty(key)
}

android {
    namespace = "com.automotive.appstore"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.radioplayer.automotive.appstore"
        minSdk = 29
        targetSdk = 37
        versionCode = 2
        versionName = "2.0.0"
    }

    /**
     * The store installs other apps through `PackageInstaller`, which requires
     * `INSTALL_PACKAGES` — a `signature|privileged` permission. Only an APK signed with the
     * AOSP **platform** key and installed as a privileged app is granted it; a normally-signed
     * APK is refused at `session.commit()` and every install fails.
     *
     * This mirrors the reference app: a `platform` signing config applied to *both* debug and
     * release, so a locally built APK behaves exactly like the shipped one. `keystore.properties`
     * is read for the credentials and is git-ignored; `keystore/platform.jks` must come from the
     * platform build.
     */
    signingConfigs {
        if (File("$rootDir/keystore/platform.jks").isFile &&
            File("$rootDir/keystore.properties").isFile
        ) {
            create("platform") {
                storeFile = File("$rootDir/keystore/platform.jks")
                storePassword = getLocalProperty(key = "keystore.password")
                keyAlias = getLocalProperty(key = "keystore.key.alias")
                keyPassword = getLocalProperty(key = "keystore.key.password")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("platform")?.let { signingConfig = it }
        }

        debug {
            isMinifyEnabled = false
            signingConfigs.findByName("platform")?.let { signingConfig = it }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:radio-image"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Window size classes drive the adaptive layout (rail width, top-bar density, type scale).
    implementation(libs.winsize)
    // `WindowMetricsCalculator`, used by `rememberStableWindowSizeClass` to read the real window
    // bounds. It arrives transitively via `winsize`, but is depended on directly here, so it is
    // declared rather than relied upon.
    implementation(libs.androidx.window)
    // Material Icons are the Android counterpart to the web app's `lucide-react` glyphs.
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.media)
    implementation(libs.coil.compose)
    // Network + APK install stack, ported 1:1 from radioplayer-automotive-appstore. Coil's
    // OkHttp backend is added as well so remote store icons resolve over the same client.
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.coil.network)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
}
