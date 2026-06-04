@file:Suppress("GradleDependency")

import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.library)
    `maven-publish`
}

android {
    namespace = "com.github.iielse.imageviewer"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.coreKtx)
    implementation(libs.transition)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleLiveDataKtx)
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.pagingRuntimeKtx)
    implementation(libs.viewpager2)
    implementation(libs.constraintlayout)
    implementation(libs.exoplayer)
    implementation(libs.photoView)
    implementation(libs.subsamplingScaleImageView)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
            }
        }
    }
}
