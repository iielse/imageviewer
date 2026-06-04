@file:Suppress("OldTargetApi", "GradleDependency")

plugins {
    alias(libs.plugins.application)
}

val bundleId: String by project

android {
    namespace = bundleId
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = bundleId
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 230
        versionName = "2.2.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":imageviewer"))

    implementation(libs.appcompat)
    implementation(libs.coreKtx)
    implementation(libs.activityKtx)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.pagingRuntimeKtx)
    implementation(libs.coroutinesAndroid)

    implementation(libs.glide)
    implementation(libs.photoView)
    implementation(libs.subsamplingScaleImageView)
    implementation(libs.media3Exoplayer)
    implementation(libs.media3Ui)
}
