plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
}

/*
 * Source of truth: radioplayer-automotive-radio -> core/designsystem.
 *
 * The upstream module relies on the `radio.android.library{,.compose}` convention
 * plugins from that project's `build-logic` and declares one `vendor` product flavor
 * (radioplayer / radioplayerGAS / renault) plus a `project(":core:radio-image")`
 * dependency. None of that applies to a standalone app, so the equivalents of the
 * convention plugins are inlined below and the flavor axis is dropped.
 * Kotlin/Android/Compose versions still come from the shared version catalog, which is
 * pinned to the same values upstream uses.
 */
android {
    namespace = "org.radioplayer.automotive.designsystem"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
        consumerProguardFiles("consumer-rules.pro")
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
    // Local, dependency-light stand-in for radioplayer's :core:radio-image module. It
    // provides only the ImageRequest / ImageSource / ImageFormat / RadioImage surface that
    // this module's Banner, Miniplayer, MetadataBlock and ContinueInApp components use.
    api(project(":core:radio-image"))

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Several design-system drawables (mic, pause, search, ...) tint themselves with
    // ?attr/colorControlNormal, which AppCompat defines. Upstream that attr reaches this
    // module transitively through the radio application; standalone it has to be declared.
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
}
