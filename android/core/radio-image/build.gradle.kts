plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
}

/*
 * Minimal stand-in for radioplayer's `core/radio-image` module.
 *
 * The upstream module resolves images through Coil behind a Hilt `@EntryPoint`, which
 * would drag Hilt, the aggregation graph and the whole radio app into this standalone
 * build. The design system only touches four symbols from it, all inside previews and
 * artwork slots: ImageRequest, ImageSource, ImageFormat and the RadioImage composable.
 * Those are reimplemented here with identical package names and signatures, backed by
 * Coil 3 directly, so `:core:designsystem` compiles unmodified.
 */
android {
    namespace = "org.radioplayer.radio.image"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
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
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}
